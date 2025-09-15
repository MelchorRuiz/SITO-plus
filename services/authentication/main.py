from enum import Enum
from pydantic import BaseModel
from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from passlib.hash import bcrypt
import sqlite3
import jwt
from datetime import datetime, timedelta
from dotenv import load_dotenv
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import os

load_dotenv()

DATABASE_PATH = os.getenv("DATABASE_PATH", "./auth.db")
SECRET_KEY = os.getenv("SECRET_KEY", "your_secret_key")
ALGORITHM = os.getenv("ALGORITHM", "HS256")
TOKEN_EXPIRATION_MINUTES = int(os.getenv("TOKEN_EXPIRATION_MINUTES", 30))

app = FastAPI(
    title="Authentication Microservice",
    description="This microservice handles user authentication, including registration, login, and password management.",
    version="1.0.0",
)
security = HTTPBearer()

origins = [
    "http://localhost:8000",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def init_db():
    with sqlite3.connect(DATABASE_PATH) as conn:
        cursor = conn.cursor()
        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                hashed_password TEXT NOT NULL,
                role TEXT NOT NULL
            )
            """
        )
        conn.commit()

init_db()

def get_db_connection():
    conn = sqlite3.connect(DATABASE_PATH)
    conn.row_factory = sqlite3.Row
    return conn


class User(BaseModel):
    id: int
    username: str
    hashed_password: str
    role: str


class Role(str, Enum):
    STUDENT = "student"
    TEACHER = "teacher"
    SCHOOL_SERVICES = "school-services"
    HUMAN_RESOURCES = "human-resources"


class UserCreate(BaseModel):
    username: str
    password: str
    role: Role


class RegisterResponse(BaseModel):
    message: str


class UserLogin(BaseModel):
    username: str
    password: str


class LoginResponse(BaseModel):
    message: str
    role: Role
    token: str


class ChangePasswordRequest(BaseModel):
    current_password: str
    new_password: str


class ChangePasswordResponse(BaseModel):
    message: str

    
class TokenPayload(BaseModel):
    sub: str
    role: str
    exp: int


class ValidateTokenResponse(BaseModel):
    valid: bool
    data: TokenPayload


def create_access_token(data: dict):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=TOKEN_EXPIRATION_MINUTES)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


def validate_registration_permissions(token_payload: dict, role_to_register: Role):
    user_role = token_payload.get("role")
    if user_role == Role.HUMAN_RESOURCES:
        if role_to_register not in [Role.HUMAN_RESOURCES, Role.TEACHER, Role.SCHOOL_SERVICES]:
            raise HTTPException(status_code=403, detail="Human Resources can only register HR, teachers, and school services personnel")
    elif user_role == Role.SCHOOL_SERVICES:
        if role_to_register != Role.STUDENT:
            raise HTTPException(status_code=403, detail="School Services can only register students")
    else:
        raise HTTPException(status_code=403, detail="You do not have permission to register users")


@app.post("/register", response_model=RegisterResponse)
def register_user(user: UserCreate, token: str = Depends(security)):
    try:
        token_payload = jwt.decode(token.credentials, SECRET_KEY, algorithms=[ALGORITHM])
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token has expired")
    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")

    validate_registration_permissions(token_payload, user.role)

    hashed_password = bcrypt.hash(user.password)
    with get_db_connection() as conn:
        cursor = conn.cursor()
        try:
            cursor.execute(
                "INSERT INTO users (username, hashed_password, role) VALUES (?, ?, ?)",
                (user.username, hashed_password, user.role.value),
            )
            conn.commit()
        except sqlite3.IntegrityError:
            raise HTTPException(status_code=400, detail="Username already exists")
    return RegisterResponse(message="User registered successfully")


@app.post("/change-password", response_model=ChangePasswordResponse)
def change_password(request: ChangePasswordRequest, token: str = Depends(security)):
    try:
        token_payload = jwt.decode(token.credentials, SECRET_KEY, algorithms=[ALGORITHM])
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token has expired")
    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")

    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT hashed_password FROM users WHERE username = ?", (token_payload.get("sub"),))
        db_user = cursor.fetchone()
        if not db_user or not bcrypt.verify(request.current_password, db_user["hashed_password"]):
            raise HTTPException(status_code=400, detail="Invalid credentials")

        hashed_password = bcrypt.hash(request.new_password)
        cursor.execute("UPDATE users SET hashed_password = ? WHERE username = ?", (hashed_password, token_payload.get("sub")))
        conn.commit()
    return ChangePasswordResponse(message="Password updated successfully")


@app.post("/login", response_model=LoginResponse)
def login(user: UserLogin):
    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT hashed_password, role FROM users WHERE username = ?", (user.username,))
        db_user = cursor.fetchone()
        if not db_user or not bcrypt.verify(user.password, db_user["hashed_password"]):
            raise HTTPException(status_code=400, detail="Invalid credentials")

        token_data = {"sub": user.username, "role": db_user["role"]}
        access_token = create_access_token(data=token_data)

    return LoginResponse(message="Login successful", role=db_user["role"], token=access_token)


@app.post("/validate-token", response_model=ValidateTokenResponse)
def validate_token(token: str = Depends(security)):
    try:
        payload = jwt.decode(token.credentials, SECRET_KEY, algorithms=[ALGORITHM])
        return ValidateTokenResponse(valid=True, data=TokenPayload(**payload))
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token has expired")
    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")
