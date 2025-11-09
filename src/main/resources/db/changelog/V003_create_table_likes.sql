-- liquibase formatted sql

-- changeset Seungwon-Choi:3 runInTransaction:false
CREATE TABLE IF NOT EXISTS likes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY   NOT NULL COMMENT '좋아요 PK',
    member_id  BIGINT                              NOT NULL COMMENT '회원 FK',
    memory_id  BIGINT                              NOT NULL COMMENT '기억 FK',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '데이터 생성일자',

    CONSTRAINT fk_likes_member FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_likes_memory FOREIGN KEY (memory_id) REFERENCES memories(id)
);
