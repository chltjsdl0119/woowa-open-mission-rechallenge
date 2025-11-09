-- liquibase formatted sql

-- changeset Seungwon-Choi:1 runInTransaction:false
CREATE TABLE IF NOT EXISTS members (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY   NOT NULL COMMENT '회원 PK',
    name       VARCHAR(100)                        NOT NULL COMMENT '회원 이름',
    nickname   VARCHAR(100)                        NOT NULL COMMENT '회원 이름',
    age        INT                                 NOT NULL COMMENT '회원 나이',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '데이터 생성일자',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '데이터 수정일자'
);
