from enum import Enum
from typing import Union, Optional, List
from pydantic import BaseModel
from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import sqlite3
import requests
import os
from datetime import datetime
from dotenv import load_dotenv

load_dotenv()

DATABASE_PATH = os.getenv("DATABASE_PATH", "./students.db")
AUTH_SERVICE_URL = os.getenv("AUTH_SERVICE_URL", "http://authentication")

class Role(str, Enum):
    STUDENT = "student"
    TEACHER = "teacher"
    SCHOOL_SERVICES = "school-services"
    HUMAN_RESOURCES = "human-resources"

class Student(BaseModel):
    id: int
    name: str
    major: str
    username: str

class StudentCreate(BaseModel):
    name: str
    major: str

class StudentUpdate(BaseModel):
    name: Optional[str] = None
    major: Optional[str] = None

class StudentResponse(BaseModel):
    id: int
    name: str
    major: str

class StudentsListResponse(BaseModel):
    students: List[StudentResponse]

class MessageResponse(BaseModel):
    message: str


app = FastAPI(
    title="Student Microservice",
    description="This microservice handles student-related operations, including profile management and course enrollment.",
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
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                major TEXT NOT NULL,
                username TEXT UNIQUE NOT NULL
            )
            """
        )
        conn.commit()

init_db()

def get_db_connection():
    conn = sqlite3.connect(DATABASE_PATH)
    conn.row_factory = sqlite3.Row
    return conn

def validate_token(token: HTTPAuthorizationCredentials = Depends(security)):
    try:
        # Call the authentication service to validate the token
        response = requests.post(
            f"{AUTH_SERVICE_URL}/validate-token",
            headers={"Authorization": f"Bearer {token.credentials}"}
        )
        
        if response.status_code == 200:
            return response.json()["data"]
        elif response.status_code == 401:
            raise HTTPException(status_code=401, detail="Token has expired or is invalid")
        else:
            raise HTTPException(status_code=401, detail="Token validation failed")
    except requests.RequestException:
        raise HTTPException(status_code=503, detail="Authentication service unavailable")

def get_current_user(token_payload: dict = Depends(validate_token), token: HTTPAuthorizationCredentials = Depends(security)):
    username = token_payload.get("sub")
    role = token_payload.get("role")
    if not username or not role:
        raise HTTPException(status_code=401, detail="Invalid token payload")
    return {"username": username, "role": role, "token": token.credentials}

def require_role(required_role: Role):
    def role_checker(current_user: dict = Depends(get_current_user)):
        if current_user["role"] != required_role.value:
            raise HTTPException(
                status_code=403, 
                detail=f"Access denied. Required role: {required_role.value}"
            )
        return current_user
    return role_checker


class StatusResponse(BaseModel):
    status: str


@app.get("/status", response_model=StatusResponse)
def get_status() -> StatusResponse:
    return StatusResponse(status="ok")

@app.get("/me", response_model=StudentResponse)
def get_student_profile(current_user: dict = Depends(require_role(Role.STUDENT))):
    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT id, name, major, username FROM students WHERE username = ?",
            (current_user["username"],)
        )
        student = cursor.fetchone()
        
        if not student:
            raise HTTPException(status_code=404, detail="Student profile not found")
        
        return StudentResponse(
            id=student["id"],
            name=student["name"],
            major=student["major"]
        )

@app.post("/students", response_model=StudentResponse)
def add_student(
    student_data: StudentCreate,
    current_user: dict = Depends(require_role(Role.SCHOOL_SERVICES))
):
    with get_db_connection() as conn:
        cursor = conn.cursor()
        
        current_year = datetime.now().year
        year_prefix = str(current_year)[-2:]
        
        cursor.execute(
            "SELECT MAX(id) FROM students WHERE id LIKE ?",
            (f"{year_prefix}%",)
        )
        max_id_result = cursor.fetchone()
        
        if max_id_result[0] is None:
            consecutive_number = 1
        else:
            max_id = max_id_result[0]
            consecutive_number = (max_id % 1000000) + 1
        
        student_id = int(f"{year_prefix}{consecutive_number:06d}")
        
        try:
            auth_response = requests.post(
                f"{AUTH_SERVICE_URL}/register",
                json={
                    "username": str(student_id),
                    "password": str(student_id),
                    "role": "student"
                },
                headers={"Authorization": f"Bearer {current_user.get('token', '')}"}
            )
            
            if auth_response.status_code != 200:
                raise HTTPException(
                    status_code=400, 
                    detail=f"Failed to register student in authentication service: {auth_response.text}"
                )
        except requests.RequestException:
            raise HTTPException(
                status_code=503, 
                detail="Authentication service unavailable"
            )

        try:
            cursor.execute(
                "INSERT INTO students (id, name, major, username) VALUES (?, ?, ?, ?)",
                (student_id, student_data.name, student_data.major, str(student_id))
            )
            conn.commit()
            
            return StudentResponse(
                id=student_id,
                name=student_data.name,
                major=student_data.major
            )
        except sqlite3.IntegrityError:
            raise HTTPException(status_code=400, detail="Student ID already exists")

@app.get("/students", response_model=StudentsListResponse)
def get_all_students(current_user: dict = Depends(require_role(Role.SCHOOL_SERVICES))):
    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT id, name, major FROM students ORDER BY name")
        students = cursor.fetchall()
        
        students_list = [
            StudentResponse(
                id=student["id"],
                name=student["name"],
                major=student["major"]
            )
            for student in students
        ]
        
        return StudentsListResponse(students=students_list)

@app.patch("/students/{student_id}", response_model=StudentResponse)
def update_student(
    student_id: int,
    student_data: StudentUpdate,
    current_user: dict = Depends(require_role(Role.SCHOOL_SERVICES))
):
    with get_db_connection() as conn:
        cursor = conn.cursor()

        # Check if student exists
        cursor.execute("SELECT id, name, major FROM students WHERE id = ?", (student_id,))
        existing_student = cursor.fetchone()
        
        if not existing_student:
            raise HTTPException(status_code=404, detail="Student not found")
        
        update_fields = []
        update_values = []
        
        if student_data.name is not None:
            update_fields.append("name = ?")
            update_values.append(student_data.name)
        
        if student_data.major is not None:
            update_fields.append("major = ?")
            update_values.append(student_data.major)
        
        if not update_fields:
            return StudentResponse(
                id=existing_student["id"],
                name=existing_student["name"],
                major=existing_student["major"]
            )
        
        # Execute update
        update_values.append(student_id)
        update_query = f"UPDATE students SET {', '.join(update_fields)} WHERE id = ?"
        cursor.execute(update_query, update_values)
        conn.commit()
        
        # Fetch updated student
        cursor.execute("SELECT id, name, major, username FROM students WHERE id = ?", (student_id,))
        updated_student = cursor.fetchone()
        
        return StudentResponse(
            id=updated_student["id"],
            name=updated_student["name"],
            major=updated_student["major"]
        )

@app.delete("/students/{student_id}", response_model=MessageResponse)
def delete_student(
    student_id: int,
    current_user: dict = Depends(require_role(Role.SCHOOL_SERVICES))
):
    with get_db_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT id FROM students WHERE id = ?", (student_id,))
        student = cursor.fetchone()
        if not student:
            raise HTTPException(status_code=404, detail="Student not found")

        try:
            auth_response = requests.delete(
                f"{AUTH_SERVICE_URL}/delete-user/{student_id}",
                headers={"Authorization": f"Bearer {current_user.get('token', '')}"}
            )
            if auth_response.status_code != 200:
                raise HTTPException(
                    status_code=400,
                    detail=f"Failed to delete student in authentication service: {auth_response.text}"
                )
        except requests.RequestException:
            raise HTTPException(status_code=503, detail="Authentication service unavailable")

        cursor.execute("DELETE FROM students WHERE id = ?", (student_id,))
        conn.commit()
        return MessageResponse(message="Student deleted successfully")
