from typing import Union
from pydantic import BaseModel
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

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


class RootResponse(BaseModel):
    Hello: str


class ItemResponse(BaseModel):
    item_id: int
    q: Union[str, None]


@app.get("/", response_model=RootResponse)
def read_root() -> RootResponse:
    return RootResponse(Hello="From Auth Service")


@app.get("/items/{item_id}", response_model=ItemResponse)
def read_item(item_id: int, q: Union[str, None] = None) -> ItemResponse:
    return ItemResponse(item_id=item_id, q=q)
