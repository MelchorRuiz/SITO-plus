FROM python:3.13.7-alpine3.21

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir --upgrade -r requirements.txt

COPY . .

CMD ["fastapi", "run", "main.py", "--port", "80"]
