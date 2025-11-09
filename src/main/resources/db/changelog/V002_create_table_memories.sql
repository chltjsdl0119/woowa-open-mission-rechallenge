-- liquibase formatted sql

-- changeset Seungwon-Choi:2 runInTransaction:false
CREATE TABLE IF NOT EXISTS memories (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY   NOT NULL COMMENT '기억 PK',
    title      VARCHAR(200)                        NOT NULL COMMENT '기억 제목',
    content    TEXT                                NOT NULL COMMENT '기억 내용',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '데이터 생성일자',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '데이터 수정일자'
);
