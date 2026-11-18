-- 以下查询只使用教师、课程、教学班，可以在选课表完成前独立执行。
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
