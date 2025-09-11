from enum import Enum
from pydantic import BaseModel
from fastapi import FastAPI
from fastapi import HTTPException
from fastapi.middleware.cors import CORSMiddleware
from passlib.hash import bcrypt
import sqlite3

DATABASE_PATH = "./auth.db"

app = FastAPI(
    title="Authentication Microservice",
    description="This microservice handles user authentication, including registration, login, and password management.",
    version="1.0.0",
)

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


class UserLogin(BaseModel):
    username: str
    password: str


class RegisterResponse(BaseModel):
    message: str

class ChangePasswordResponse(BaseModel):
    message: str

class LoginResponse(BaseModel):
    message: str
    role: Role


class ChangePasswordRequest(BaseModel):
    username: str
    current_password: str
    new_password: str


@app.post("/register", response_model=RegisterResponse)
def register_user(user: UserCreate):
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
def change_password(request: ChangePasswordRequest):
    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT hashed_password FROM users WHERE username = ?", (request.username,))
        db_user = cursor.fetchone()
        if not db_user or not bcrypt.verify(request.current_password, db_user["hashed_password"]):
            raise HTTPException(status_code=400, detail="Invalid credentials")

        hashed_password = bcrypt.hash(request.new_password)
        cursor.execute("UPDATE users SET hashed_password = ? WHERE username = ?", (hashed_password, request.username))
        if cursor.rowcount == 0:
            raise HTTPException(status_code=404, detail="User not found")
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
    return LoginResponse(message="Login successful", role=db_user["role"])
