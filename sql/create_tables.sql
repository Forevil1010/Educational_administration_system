CREATE DATABASE IF NOT EXISTS educational_administration
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE educational_administration;

CREATE TABLE IF NOT EXISTS student (
    student_id VARCHAR(20) NOT NULL COMMENT '学号',
    student_name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) NOT NULL DEFAULT '未知' COMMENT '性别',
    major VARCHAR(100) NOT NULL COMMENT '专业',
    class_name VARCHAR(50) NOT NULL COMMENT '行政班',
    PRIMARY KEY (student_id),
    CONSTRAINT chk_student_gender CHECK (gender IN ('男', '女', '未知'))
) ENGINE = InnoDB;

-- 必须先由成员 B 创建 teaching_class，且 teaching_class_id 类型为 BIGINT。
CREATE TABLE IF NOT EXISTS enrollment (
    enrollment_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '选课号',
    student_id VARCHAR(20) NOT NULL COMMENT '学号',
    teaching_class_id BIGINT NOT NULL COMMENT '教学班号',
    status VARCHAR(10) NOT NULL DEFAULT '已选' COMMENT '选课状态',
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近选课时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
    PRIMARY KEY (enrollment_id),
    UNIQUE KEY uk_enrollment_student_class (student_id, teaching_class_id),
    KEY idx_enrollment_class_status (teaching_class_id, status),
    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id) REFERENCES student (student_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_enrollment_teaching_class
        FOREIGN KEY (teaching_class_id) REFERENCES teaching_class (teaching_class_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT chk_enrollment_status CHECK (status IN ('已选', '已退'))
) ENGINE = InnoDB;
