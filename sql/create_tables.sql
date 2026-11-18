-- 开课部分可独立建表：教师、课程、教学班。
-- 成绩表依赖选课记录，等学生表和选课表确定后再创建。

DROP TABLE IF EXISTS teaching_section;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS teacher;

CREATE TABLE teacher (
    teacher_no   VARCHAR(20)  NOT NULL COMMENT '教师号',
    teacher_name VARCHAR(50)  NOT NULL COMMENT '姓名',
    title        VARCHAR(20)  NOT NULL COMMENT '职称',
    department   VARCHAR(50)  NOT NULL COMMENT '所属单位',
    PRIMARY KEY (teacher_no),
    CONSTRAINT chk_teacher_name CHECK (CHAR_LENGTH(TRIM(teacher_name)) > 0),
    CONSTRAINT chk_teacher_title CHECK (CHAR_LENGTH(TRIM(title)) > 0),
    CONSTRAINT chk_teacher_department CHECK (CHAR_LENGTH(TRIM(department)) > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '教师';

-- 按姓名查询教师时使用。
CREATE INDEX idx_teacher_name ON teacher (teacher_name);

CREATE TABLE course (
    course_no   VARCHAR(20)  NOT NULL COMMENT '课程号',
    course_name VARCHAR(50)  NOT NULL COMMENT '课程名',
    credit      DECIMAL(3,1) NOT NULL COMMENT '学分',
    class_hours INT          NOT NULL COMMENT '学时',
    PRIMARY KEY (course_no),
    CONSTRAINT chk_course_name CHECK (CHAR_LENGTH(TRIM(course_name)) > 0),
    CONSTRAINT chk_course_credit CHECK (credit > 0 AND credit <= 10),
    CONSTRAINT chk_course_hours CHECK (class_hours > 0 AND class_hours <= 200)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '课程';

-- 按课程名查询时使用。
CREATE INDEX idx_course_name ON course (course_name);

CREATE TABLE teaching_section (
    section_no  VARCHAR(20) NOT NULL COMMENT '教学班号',
    course_no   VARCHAR(20) NOT NULL COMMENT '课程号',
    teacher_no  VARCHAR(20) NOT NULL COMMENT '教师号',
    semester    VARCHAR(20) NOT NULL COMMENT '学期',
    capacity    INT         NOT NULL COMMENT '容量',
    class_time  VARCHAR(50) NOT NULL COMMENT '上课时间',
    location    VARCHAR(50) NOT NULL COMMENT '上课地点',
    PRIMARY KEY (section_no),
    CONSTRAINT fk_section_course FOREIGN KEY (course_no) REFERENCES course (course_no),
    CONSTRAINT fk_section_teacher FOREIGN KEY (teacher_no) REFERENCES teacher (teacher_no),
    CONSTRAINT chk_section_semester CHECK (CHAR_LENGTH(TRIM(semester)) > 0),
    CONSTRAINT chk_section_capacity CHECK (capacity > 0 AND capacity <= 500),
    CONSTRAINT chk_section_time CHECK (CHAR_LENGTH(TRIM(class_time)) > 0),
    CONSTRAINT chk_section_location CHECK (CHAR_LENGTH(TRIM(location)) > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '教学班';

-- 按学期查询教学班时使用。教师号、课程号索引由外键自动建立。
CREATE INDEX idx_section_semester ON teaching_section (semester);
