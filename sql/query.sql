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
