package edu.course.selection;

import edu.course.selection.dao.EnrollmentDAO;
import edu.course.selection.dao.StudentDAO;
import edu.course.selection.model.CourseSelectionView;
import edu.course.selection.model.GradeView;
import edu.course.selection.model.Student;
import edu.course.selection.service.EnrollmentService;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final StudentDAO STUDENT_DAO = new StudentDAO();
    private static final EnrollmentDAO ENROLLMENT_DAO = new EnrollmentDAO();
    private static final EnrollmentService ENROLLMENT_SERVICE = new EnrollmentService();

    public static void main(String[] args) {
        boolean running = true;
        System.out.println("=== 高校教学管理系统：学生与选课 ===");
        while (running) {
            printMenu();
            int choice = readInt("请选择功能：");
            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> findStudent();
                    case 3 -> listStudents();
                    case 4 -> updateStudent();
                    case 5 -> deleteStudent();
                    case 6 -> enroll();
                    case 7 -> drop();
                    case 8 -> showSchedule();
                    case 9 -> showGrades();
                    case 0 -> running = false;
                    default -> System.out.println("无效选项，请重新输入。");
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("操作违反唯一性、外键或检查约束：" + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("业务操作失败：" + e.getMessage());
            } catch (SQLException e) {
                System.out.println("数据库操作失败：" + e.getMessage());
            }
        }
        SCANNER.close();
        System.out.println("程序已退出。");
    }

    private static void printMenu() {
        System.out.println("\n1. 添加学生");
        System.out.println("2. 按学号查询学生");
        System.out.println("3. 查询全部学生");
        System.out.println("4. 修改学生");
        System.out.println("5. 删除学生");
        System.out.println("6. 选课");
        System.out.println("7. 退课");
        System.out.println("8. 查询我的课表");
        System.out.println("9. 查询我的成绩");
        System.out.println("0. 退出");
    }

    private static void addStudent() throws SQLException {
        Student student = new Student(
                readRequired("学号："),
                readRequired("姓名："),
                readRequired("性别（男/女/未知）："),
                readRequired("专业："),
                readRequired("行政班：")
        );
        STUDENT_DAO.insert(student);
        System.out.println("学生添加成功。");
    }

    private static void findStudent() throws SQLException {
        String studentId = readRequired("请输入学号：");
        Student student = STUDENT_DAO.findById(studentId);
        if (student == null) {
            System.out.println("未找到该学生。");
        } else {
            printStudentHeader();
            System.out.println(student);
        }
    }

    private static void listStudents() throws SQLException {
        List<Student> students = STUDENT_DAO.findAll();
        if (students.isEmpty()) {
            System.out.println("暂无学生记录。");
            return;
        }
        printStudentHeader();
        students.forEach(System.out::println);
    }

    private static void updateStudent() throws SQLException {
        String studentId = readRequired("请输入要修改的学号：");
        Student student = STUDENT_DAO.findById(studentId);
        if (student == null) {
            System.out.println("未找到该学生，未执行修改。");
            return;
        }
        student.setStudentName(readWithDefault("姓名", student.getStudentName()));
        student.setGender(readWithDefault("性别（男/女/未知）", student.getGender()));
        student.setMajor(readWithDefault("专业", student.getMajor()));
        student.setClassName(readWithDefault("行政班", student.getClassName()));
        boolean updated = STUDENT_DAO.update(student);
        System.out.println(updated ? "学生信息修改成功。" : "学生信息未修改。");
    }

    private static void deleteStudent() throws SQLException {
        String studentId = readRequired("请输入要删除的学号：");
        boolean deleted = STUDENT_DAO.deleteById(studentId);
        System.out.println(deleted ? "学生删除成功。" : "未找到该学生，未执行删除。");
    }

    private static void enroll() throws SQLException {
        String studentId = readRequired("学号：");
        long teachingClassId = readLong("教学班号：");
        long enrollmentId = ENROLLMENT_SERVICE.enroll(studentId, teachingClassId);
        System.out.println("选课成功，选课号：" + enrollmentId);
    }

    private static void drop() throws SQLException {
        String studentId = readRequired("学号：");
        long teachingClassId = readLong("教学班号：");
        ENROLLMENT_SERVICE.drop(studentId, teachingClassId);
        System.out.println("退课成功。");
    }

    private static void showSchedule() throws SQLException {
        String studentId = readRequired("学号：");
        List<CourseSelectionView> schedule = ENROLLMENT_DAO.findScheduleByStudent(studentId);
        if (schedule.isEmpty()) {
            System.out.println("暂无有效课表记录。");
            return;
        }
        schedule.forEach(System.out::println);
    }

    private static void showGrades() throws SQLException {
        String studentId = readRequired("学号：");
        List<GradeView> grades = ENROLLMENT_DAO.findGradesByStudent(studentId);
        if (grades.isEmpty()) {
            System.out.println("暂无课程成绩记录。");
            return;
        }
        grades.forEach(System.out::println);
    }

    private static void printStudentHeader() {
        System.out.println("学号\t姓名\t性别\t专业\t行政班");
    }

    private static String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("输入不能为空。");
        }
    }

    private static String readWithDefault(String label, String currentValue) {
        System.out.print(label + "（当前：" + currentValue + "，直接回车保持不变）：");
        String value = SCANNER.nextLine().trim();
        return value.isEmpty() ? currentValue : value;
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("请输入整数。");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("请输入整数编号。");
            }
        }
    }
}
