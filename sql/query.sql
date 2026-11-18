-- 成员 B：以下查询只使用教师、课程、教学班，可以单独执行。
-- 成绩统计需要连接学生和选课，暂不写在这里。

-- 1. 单表条件查询：计算机学院教师，按教师号排序。
SELECT teacher_no, teacher_name, title, department
FROM teacher
WHERE department = '计算机学院'
ORDER BY teacher_no;

-- 2. 单表条件查询：学分不低于 3 的课程，按学分降序、课程号升序。
SELECT course_no, course_name, credit, class_hours
FROM course
WHERE credit >= 3
ORDER BY credit DESC, course_no;

-- 3. 多表内连接：教学班及其课程、授课教师。
SELECT s.section_no, s.semester, c.course_no, c.course_name,
       t.teacher_no, t.teacher_name, s.capacity, s.class_time, s.location
FROM teaching_section s
INNER JOIN course c ON s.course_no = c.course_no
INNER JOIN teacher t ON s.teacher_no = t.teacher_no
ORDER BY s.section_no;

-- 4. 左连接：尚未开设教学班的课程。
SELECT c.course_no, c.course_name, s.section_no
FROM course c
LEFT JOIN teaching_section s ON c.course_no = s.course_no
WHERE s.section_no IS NULL
ORDER BY c.course_no;

-- 5. 分组统计：每位已开课教师的教学班数量，只保留至少开出 1 个班的教师。
SELECT t.teacher_no, t.teacher_name, COUNT(s.section_no) AS section_count
FROM teacher t
INNER JOIN teaching_section s ON t.teacher_no = s.teacher_no
GROUP BY t.teacher_no, t.teacher_name
HAVING COUNT(s.section_no) >= 1
ORDER BY section_count DESC, t.teacher_no;

-- 成员 A：学生和选课查询。
-- 课表、成绩查询使用 teaching_class、course_id 和 grade，与成员 B 的表还没有对齐。约定之前不要接着上一段执行。

USE educational_administration;

-- 1. 单表条件查询：按专业查询学生并排序。
SELECT student_id, student_name, gender, major, class_name
FROM student
WHERE major = '计算机科学与技术'
ORDER BY student_id;

-- 2. 多表查询：查询指定学生的全部选课记录（包括已退记录）。
SELECT s.student_id, s.student_name, e.enrollment_id,
       e.teaching_class_id, e.status, e.enrolled_at
FROM student AS s
LEFT JOIN enrollment AS e ON s.student_id = e.student_id
WHERE s.student_id = 'S2024001'
ORDER BY e.enrollment_id;

-- 3. 学生课表：依赖成员 B 的 teaching_class(course_id) 和 course(course_name)。
SELECT e.enrollment_id, e.teaching_class_id, c.course_name,
       e.status, e.enrolled_at
FROM enrollment AS e
JOIN teaching_class AS tc ON e.teaching_class_id = tc.teaching_class_id
JOIN course AS c ON tc.course_id = c.course_id
WHERE e.student_id = 'S2024001' AND e.status = '已选'
ORDER BY e.enrollment_id;

-- 4. 学生成绩：只查询成员 B 的 grade 表，不创建或修改该表。
SELECT e.enrollment_id, e.teaching_class_id, c.course_name,
       g.score, g.is_recorded
FROM enrollment AS e
JOIN teaching_class AS tc ON e.teaching_class_id = tc.teaching_class_id
JOIN course AS c ON tc.course_id = c.course_id
LEFT JOIN grade AS g ON e.enrollment_id = g.enrollment_id
WHERE e.student_id = 'S2024001' AND e.status = '已选'
ORDER BY e.enrollment_id;
