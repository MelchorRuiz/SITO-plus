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

DATABASE_PATH = os.getenv("DATABASE_PATH", "./teachers.db")
AUTH_SERVICE_URL = os.getenv("AUTH_SERVICE_URL", "http://authentication")
HUMAN_RESOURCES_SERVICE_URL = os.getenv("HUMAN_RESOURCES_SERVICE_URL", "http://human-resources/api/v1")

class Role(str, Enum):
    STUDENT = "student"
    TEACHER = "teacher"
    SCHOOL_SERVICES = "school-services"
    HUMAN_RESOURCES = "human-resources"
    
class StatusResponse(BaseModel):
    status: str

class GradeRequest(BaseModel):
    id_student: int
    student_name: str
    id_group: int
    group_name: str
    partial_1: float
    partial_2: float
    partial_3: float

class GradeResponse(BaseModel):
    id: int
    id_student: int
    student_name: str
    id_teacher: str
    teacher_name: str
    id_group: int
    group_name: str
    partial_1: float
    partial_2: float
    partial_3: float

class UpdateGradesRequest(BaseModel):
    partial_1: Optional[float] = None
    partial_2: Optional[float] = None
    partial_3: Optional[float] = None

class GradeExistsResponse(BaseModel):
    exists: bool
    grade_id: Optional[int] = None
    partial_1: Optional[float] = None
    partial_2: Optional[float] = None
    partial_3: Optional[float] = None

class StudentGradesResponse(BaseModel):
    grades: List[GradeResponse]


app = FastAPI(
    title="Teachers Microservice",
    description="This microservice manages teacher-related operations within the SITO+ system.",
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
            CREATE TABLE IF NOT EXISTS grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_student INTEGER NOT NULL,
                student_name TEXT NOT NULL,
                id_teacher INTEGER NOT NULL,
                teacher_name TEXT NOT NULL,
                id_group INTEGER NOT NULL,
                group_name TEXT NOT NULL,
                partial_1 REAL NOT NULL,
                partial_2 REAL NOT NULL,
                partial_3 REAL NOT NULL
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

def get_teacher_name(token: str):
    try:
        response = requests.get(
            f"{HUMAN_RESOURCES_SERVICE_URL}/profesores/yo",
            headers={"Authorization": f"Bearer {token}"}
        )
        print(response)
        
        if response.status_code == 200:
            nombre = response.json().get("nombre", "")
            apellido = response.json().get("apellido", "")
            return f"{nombre} {apellido}".strip()
        else:
            # If we can't get the teacher name, return empty string as fallback
            return ""
    except requests.RequestException:
        # If the service is unavailable, return empty string as fallback
        return ""


@app.get("/status", response_model=StatusResponse)
def get_status() -> StatusResponse:
    return StatusResponse(status="ok")

@app.post("/register-grade", response_model=GradeResponse)
def register_grade(
    grade_data: GradeRequest,
    current_user: dict = Depends(require_role(Role.TEACHER))
):
    try:
        # Get teacher name from human resources service
        teacher_name = get_teacher_name(current_user["token"])
        
        with get_db_connection() as conn:
            cursor = conn.cursor()
            
            # Insert the grade record
            cursor.execute(
                """
                INSERT INTO grades (
                    id_student, student_name, id_teacher, teacher_name, id_group, group_name,
                    partial_1, partial_2, partial_3
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                (
                    grade_data.id_student,
                    grade_data.student_name,
                    current_user["username"],
                    teacher_name,
                    grade_data.id_group,
                    grade_data.group_name,
                    grade_data.partial_1,
                    grade_data.partial_2,
                    grade_data.partial_3
                )
            )

            cursor.execute(
                "SELECT last_insert_rowid()"
            )
            grade_id = cursor.fetchone()[0]

            conn.commit()

            # Retrieve the inserted record to get the created_at timestamp
            cursor.execute(
                "SELECT * FROM grades WHERE id = ?",
                (grade_id,)
            )
            
            record = cursor.fetchone()
            if not record:
                raise HTTPException(status_code=500, detail="Failed to retrieve created grade record")
            
            return GradeResponse(
                id=record["id"],
                id_student=record["id_student"],
                student_name=record["student_name"],
                id_teacher=record["id_teacher"],
                teacher_name=record["teacher_name"],
                id_group=record["id_group"],
                group_name=record["group_name"],
                partial_1=record["partial_1"],
                partial_2=record["partial_2"],
                partial_3=record["partial_3"]
            )
            
    except sqlite3.IntegrityError as e:
        if "UNIQUE constraint failed" in str(e):
            raise HTTPException(status_code=400, detail="Grade record with this ID already exists")
        raise HTTPException(status_code=400, detail=f"Database constraint error: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")

@app.patch("/update-grades/{grade_id}", response_model=GradeResponse)
def update_partial_grades(
    grade_id: int,
    update_data: UpdateGradesRequest,
    current_user: dict = Depends(require_role(Role.TEACHER))
):
    try:
        # Validate that at least one partial grade is provided
        if all(value is None for value in [update_data.partial_1, update_data.partial_2, update_data.partial_3]):
            raise HTTPException(status_code=400, detail="At least one partial grade must be provided")
        
        with get_db_connection() as conn:
            cursor = conn.cursor()
            
            # Check if the grade record exists
            cursor.execute("SELECT * FROM grades WHERE id = ?", (grade_id,))
            existing_record = cursor.fetchone()
            
            if not existing_record:
                raise HTTPException(status_code=404, detail="Grade record not found")
            
            # Build dynamic update query based on provided fields
            update_fields = []
            update_values = []
            
            if update_data.partial_1 is not None:
                update_fields.append("partial_1 = ?")
                update_values.append(update_data.partial_1)
            
            if update_data.partial_2 is not None:
                update_fields.append("partial_2 = ?")
                update_values.append(update_data.partial_2)
            
            if update_data.partial_3 is not None:
                update_fields.append("partial_3 = ?")
                update_values.append(update_data.partial_3)
            
            # Add the grade_id for the WHERE clause
            update_values.append(grade_id)
            
            # Execute the update
            update_query = f"UPDATE grades SET {', '.join(update_fields)} WHERE id = ?"
            cursor.execute(update_query, update_values)
            
            if cursor.rowcount == 0:
                raise HTTPException(status_code=404, detail="Grade record not found")
            
            conn.commit()
            
            # Retrieve the updated record
            cursor.execute("SELECT * FROM grades WHERE id = ?", (grade_id,))
            updated_record = cursor.fetchone()
            
            return GradeResponse(
                id=updated_record["id"],
                id_student=updated_record["id_student"],
                student_name=updated_record["student_name"],
                id_teacher=updated_record["id_teacher"],
                teacher_name=updated_record["teacher_name"],
                id_group=updated_record["id_group"],
                group_name=updated_record["group_name"],
                partial_1=updated_record["partial_1"],
                partial_2=updated_record["partial_2"],
                partial_3=updated_record["partial_3"]
            )
            
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")

@app.get("/check-grades/{id_student}/{id_group}", response_model=GradeExistsResponse)
def check_student_grades(
    id_student: int,
    id_group: int,
    current_user: dict = Depends(require_role(Role.TEACHER))
):
    try:
        with get_db_connection() as conn:
            cursor = conn.cursor()
            
            # Check if grades exist for the student in the specified group
            cursor.execute(
                "SELECT * FROM grades WHERE id_student = ? AND id_group = ? AND id_teacher = ?",
                (id_student, id_group, current_user["username"])
            )
            
            result = cursor.fetchone()
            
            if result:
                return GradeExistsResponse(
                    exists=True,
                    grade_id=result["id"],
                    partial_1=result["partial_1"],
                    partial_2=result["partial_2"],
                    partial_3=result["partial_3"],
                )
            else:
                return GradeExistsResponse(
                    exists=False,
                    grade_id=None,
                    partial_1=None,
                    partial_2=None,
                    partial_3=None,
                )
                
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")

@app.get("/my-grades", response_model=StudentGradesResponse)
def get_my_grades(
    current_user: dict = Depends(require_role(Role.STUDENT))
):
    """Get all grades for the authenticated student"""
    try:
        with get_db_connection() as conn:
            cursor = conn.cursor()
            
            # Get all grades for the student
            cursor.execute(
                "SELECT * FROM grades WHERE id_student = ?",
                (int(current_user["username"]),)
            )
            
            results = cursor.fetchall()
            
            grades = []
            for row in results:
                grades.append(GradeResponse(
                    id=row["id"],
                    id_student=row["id_student"],
                    student_name=row["student_name"],
                    id_teacher=row["id_teacher"],
                    teacher_name=row["teacher_name"],
                    id_group=row["id_group"],
                    group_name=row["group_name"],
                    partial_1=row["partial_1"],
                    partial_2=row["partial_2"],
                    partial_3=row["partial_3"]
                ))
            
            return StudentGradesResponse(grades=grades)
                
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
