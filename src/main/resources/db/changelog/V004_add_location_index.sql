-- liquibase formatted sql

-- changeset Seungwon-Choi:4

CREATE INDEX idx_memory_location ON memories (latitude, longitude);