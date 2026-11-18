import entity.Course;
import entity.Teacher;
import entity.TeachingSection;
import service.BusinessException;
import service.CourseService;
import service.TeacherService;
import service.TeachingSectionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final TeacherService teacherService = new TeacherService();
    private final CourseService courseService = new CourseService();
    private final TeachingSectionService sectionService = new TeachingSectionService();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        while (true) {
            System.out.println();
            System.out.println("高校教学管理系统 — 开课");
            System.out.println("1. 教师管理");
            System.out.println("2. 课程管理");
            System.out.println("3. 教学班管理");
            System.out.println("0. 退出");
            String choice = readLine("请选择：");
            if ("1".equals(choice)) {
                manageTeachers();
            } else if ("2".equals(choice)) {
                manageCourses();
            } else if ("3".equals(choice)) {
                manageSections();
            } else if ("0".equals(choice)) {
                System.out.println("已退出。");
                return;
            } else {
                System.out.println("无效选项。");
            }
        }
    }

    private void manageTeachers() {
        while (true) {
            System.out.println();
            System.out.println("教师管理");
            System.out.println("1. 添加教师");
            System.out.println("2. 按教师号查询");
            System.out.println("3. 按姓名查询");
            System.out.println("4. 修改职称和所属单位");
            System.out.println("5. 删除教师");
            System.out.println("6. 列出全部教师");
            System.out.println("0. 返回");
            String choice = readLine("请选择：");
            try {
                if ("1".equals(choice)) {
                    int rows = teacherService.add(readLine("教师号："), readLine("姓名："),
                            readLine("职称："), readLine("所属单位："));
                    System.out.println("添加成功，影响行数：" + rows);
                } else if ("2".equals(choice)) {
                    printTeacher(teacherService.findByNo(readLine("教师号：")));
                } else if ("3".equals(choice)) {
                    printTeachers(teacherService.findByName(readLine("姓名：")));
                } else if ("4".equals(choice)) {
                    int rows = teacherService.updateTitleAndDepartment(readLine("教师号："),
                            readLine("新职称："), readLine("新所属单位："));
                    System.out.println("修改成功，影响行数：" + rows);
                } else if ("5".equals(choice)) {
                    int rows = teacherService.delete(readLine("教师号："));
                    System.out.println("删除成功，影响行数：" + rows);
                } else if ("6".equals(choice)) {
                    printTeachers(teacherService.findAll());
                } else if ("0".equals(choice)) {
                    return;
                } else {
                    System.out.println("无效选项。");
                }
            } catch (BusinessException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void manageCourses() {
        while (true) {
            System.out.println();
            System.out.println("课程管理");
            System.out.println("1. 添加课程");
            System.out.println("2. 按课程号查询");
            System.out.println("3. 按课程名查询");
            System.out.println("4. 修改学分和学时");
            System.out.println("5. 删除课程");
            System.out.println("6. 列出全部课程");
            System.out.println("0. 返回");
            String choice = readLine("请选择：");
            try {
                if ("1".equals(choice)) {
                    int rows = courseService.add(readLine("课程号："), readLine("课程名："),
                            readDecimal("学分："), readInt("学时："));
                    System.out.println("添加成功，影响行数：" + rows);
                } else if ("2".equals(choice)) {
                    printCourse(courseService.findByNo(readLine("课程号：")));
                } else if ("3".equals(choice)) {
                    printCourses(courseService.findByName(readLine("课程名：")));
                } else if ("4".equals(choice)) {
                    int rows = courseService.updateCreditAndHours(readLine("课程号："),
                            readDecimal("新学分："), readInt("新学时："));
                    System.out.println("修改成功，影响行数：" + rows);
                } else if ("5".equals(choice)) {
                    int rows = courseService.delete(readLine("课程号："));
                    System.out.println("删除成功，影响行数：" + rows);
                } else if ("6".equals(choice)) {
                    printCourses(courseService.findAll());
                } else if ("0".equals(choice)) {
                    return;
                } else {
                    System.out.println("无效选项。");
                }
            } catch (BusinessException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void manageSections() {
        while (true) {
            System.out.println();
            System.out.println("教学班管理");
            System.out.println("1. 开设教学班");
            System.out.println("2. 按教学班号查询");
            System.out.println("3. 按学期查询");
            System.out.println("4. 调整容量、教师、时间和地点");
            System.out.println("5. 删除教学班");
            System.out.println("6. 列出全部教学班");
            System.out.println("0. 返回");
            String choice = readLine("请选择：");
            try {
                if ("1".equals(choice)) {
                    int rows = sectionService.open(readLine("教学班号："), readLine("课程号："),
                            readLine("教师号："), readLine("学期："), readInt("容量："),
                            readLine("上课时间："), readLine("上课地点："));
                    System.out.println("开设成功，影响行数：" + rows);
                } else if ("2".equals(choice)) {
                    printSection(sectionService.findByNo(readLine("教学班号：")));
                } else if ("3".equals(choice)) {
                    printSections(sectionService.findBySemester(readLine("学期：")));
                } else if ("4".equals(choice)) {
                    int rows = sectionService.adjust(readLine("教学班号："), readLine("新教师号："),
                            readInt("新容量："), readLine("新上课时间："), readLine("新上课地点："));
                    System.out.println("调整成功，影响行数：" + rows);
                } else if ("5".equals(choice)) {
                    int rows = sectionService.delete(readLine("教学班号："));
                    System.out.println("删除成功，影响行数：" + rows);
                } else if ("6".equals(choice)) {
                    printSections(sectionService.findAll());
                } else if ("0".equals(choice)) {
                    return;
                } else {
                    System.out.println("无效选项。");
                }
            } catch (BusinessException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void printTeacher(Teacher teacher) {
        if (teacher == null) {
            System.out.println("未查询到教师。");
            return;
        }
        System.out.println(teacher.getTeacherNo() + "  " + teacher.getTeacherName()
                + "  " + teacher.getTitle() + "  " + teacher.getDepartment());
    }

    private void printTeachers(List<Teacher> teachers) {
        if (teachers.isEmpty()) {
            System.out.println("未查询到教师。");
            return;
        }
        for (Teacher teacher : teachers) {
            printTeacher(teacher);
        }
    }

    private void printCourse(Course course) {
        if (course == null) {
            System.out.println("未查询到课程。");
            return;
        }
        System.out.println(course.getCourseNo() + "  " + course.getCourseName()
                + "  学分" + course.getCredit() + "  学时" + course.getClassHours());
    }

    private void printCourses(List<Course> courses) {
        if (courses.isEmpty()) {
            System.out.println("未查询到课程。");
            return;
        }
        for (Course course : courses) {
            printCourse(course);
        }
    }

    private void printSection(TeachingSection section) {
        if (section == null) {
            System.out.println("未查询到教学班。");
            return;
        }
        System.out.println(section.getSectionNo() + "  " + section.getSemester()
                + "  " + section.getCourseNo() + " " + section.getCourseName()
                + "  " + section.getTeacherNo() + " " + section.getTeacherName()
                + "  容量" + section.getCapacity()
                + "  " + section.getClassTime() + "  " + section.getLocation());
    }

    private void printSections(List<TeachingSection> sections) {
        if (sections.isEmpty()) {
            System.out.println("未查询到教学班。");
            return;
        }
        for (TeachingSection section : sections) {
            printSection(section);
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readInt(String prompt) throws BusinessException {
        String text = readLine(prompt).trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new BusinessException("请输入整数。");
        }
    }

    private BigDecimal readDecimal(String prompt) throws BusinessException {
        String text = readLine(prompt).trim();
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException ex) {
            throw new BusinessException("请输入数字。");
        }
    }
}
