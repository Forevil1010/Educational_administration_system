package test;

import entity.Course;
import entity.Teacher;
import entity.TeachingSection;
import service.BusinessException;
import service.CourseService;
import service.TeacherService;
import service.TeachingSectionService;

import java.math.BigDecimal;
import java.util.List;

public class IndependentFunctionTest {
    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        TeacherService teacherService = new TeacherService();
        CourseService courseService = new CourseService();
        TeachingSectionService sectionService = new TeachingSectionService();
        cleanup(teacherService, courseService, sectionService);
        try {
            checkSeedData(teacherService, courseService, sectionService);
            checkTeacherFlow(teacherService);
            checkCourseFlow(courseService);
            checkSectionFlow(teacherService, courseService, sectionService);
        } finally {
            cleanup(teacherService, courseService, sectionService);
        }
        System.out.println();
        System.out.println("通过 " + passed + "，失败 " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void checkSeedData(TeacherService teacherService, CourseService courseService,
                                      TeachingSectionService sectionService) throws RuntimeException {
        try {
            check("初始教师不少于 10 条", teacherService.findAll().size() >= 10);
            check("初始课程不少于 10 条", courseService.findAll().size() >= 10);
            check("初始教学班不少于 10 条", sectionService.findAll().size() >= 10);
            Teacher zhang = teacherService.findByNo("T2023001");
            check("按教师号查到张伟", zhang != null && "张伟".equals(zhang.getTeacherName()));
            check("查询不存在的教师返回空", teacherService.findByNo("T0000000") == null);
            List<Teacher> byName = teacherService.findByName("不存在的姓名");
            check("按姓名查询无结果时返回空列表", byName.isEmpty());
            TeachingSection section = sectionService.findByNo("SEC2025001");
            check("教学班查询带出课程名和教师名", section != null
                    && "数据库系统".equals(section.getCourseName())
                    && "张伟".equals(section.getTeacherName()));
        } catch (BusinessException ex) {
            check("读取初始数据：" + ex.getMessage(), false);
        }
    }

    private static void checkTeacherFlow(TeacherService teacherService) {
        try {
            int rows = teacherService.add("TTEST001", "测试教师", "讲师", "计算机学院");
            check("正常添加教师影响 1 行", rows == 1);
            Teacher added = teacherService.findByNo("TTEST001");
            check("添加后可以按教师号查到", added != null && "测试教师".equals(added.getTeacherName()));
        } catch (BusinessException ex) {
            check("正常添加教师：" + ex.getMessage(), false);
        }
        expectFail(new Action() {
            public void run() throws BusinessException {
                teacherService.add("TTEST001", "另一人", "副教授", "数学学院");
            }
        }, "重复教师号被拒绝");
        try {
            int rows = teacherService.updateTitleAndDepartment("TTEST001", "副教授", "软件学院");
            Teacher updated = teacherService.findByNo("TTEST001");
            check("修改教师影响 1 行且职称已更新", rows == 1 && updated != null
                    && "副教授".equals(updated.getTitle()) && "软件学院".equals(updated.getDepartment()));
        } catch (BusinessException ex) {
            check("修改教师：" + ex.getMessage(), false);
        }
        expectFail(new Action() {
            public void run() throws BusinessException {
                teacherService.updateTitleAndDepartment("T0000000", "讲师", "计算机学院");
            }
        }, "修改不存在的教师被拒绝");
    }

    private static void checkCourseFlow(CourseService courseService) {
        try {
            int rows = courseService.add("CTEST001", "测试课程", new BigDecimal("2.5"), 32);
            Course added = courseService.findByNo("CTEST001");
            check("正常添加课程影响 1 行", rows == 1 && added != null
                    && added.getCredit().compareTo(new BigDecimal("2.5")) == 0);
        } catch (BusinessException ex) {
            check("正常添加课程：" + ex.getMessage(), false);
        }
        expectFail(new Action() {
            public void run() throws BusinessException {
                courseService.add("CTEST001", "重复课程", new BigDecimal("3"), 48);
            }
        }, "重复课程号被拒绝");
        expectFail(new Action() {
            public void run() throws BusinessException {
                courseService.add("CTEST002", "无效学分", BigDecimal.ZERO, 32);
            }
        }, "学分为 0 被拒绝");
        try {
            int rows = courseService.updateCreditAndHours("CTEST001", new BigDecimal("3.0"), 48);
            Course updated = courseService.findByNo("CTEST001");
            check("修改课程学分和学时", rows == 1 && updated != null && updated.getClassHours() == 48);
        } catch (BusinessException ex) {
            check("修改课程：" + ex.getMessage(), false);
        }
        expectFail(new Action() {
            public void run() throws BusinessException {
                courseService.delete("C0000000");
            }
        }, "删除不存在的课程被拒绝");
    }

    private static void checkSectionFlow(final TeacherService teacherService, final CourseService courseService,
                                         final TeachingSectionService sectionService) {
        expectFail(new Action() {
            public void run() throws BusinessException {
                sectionService.open("STEST001", "CTEST001", "TTEST001", "2025-2026-1", 0, "周一1-2节", "教1-199");
            }
        }, "容量为 0 时拒绝开课");
        expectFail(new Action() {
            public void run() throws BusinessException {
                sectionService.open("STEST001", "C0000000", "TTEST001", "2025-2026-1", 30, "周一1-2节", "教1-199");
            }
        }, "课程不存在时拒绝开课");
        expectFail(new Action() {
            public void run() throws BusinessException {
                sectionService.open("STEST001", "CTEST001", "T0000000", "2025-2026-1", 30, "周一1-2节", "教1-199");
            }
        }, "教师不存在时拒绝开课");
        try {
            int rows = sectionService.open("STEST001", "CTEST001", "TTEST001", "2025-2026-1",
                    30, "周一1-2节", "教1-199");
            TeachingSection opened = sectionService.findByNo("STEST001");
            check("正常开设教学班", rows == 1 && opened != null && opened.getCapacity() == 30
                    && "测试课程".equals(opened.getCourseName()));
        } catch (BusinessException ex) {
            check("正常开设教学班：" + ex.getMessage(), false);
        }
        expectFail(new Action() {
            public void run() throws BusinessException {
                sectionService.open("STEST001", "CTEST001", "TTEST001", "2025-2026-1", 20, "周二1-2节", "教1-188");
            }
        }, "重复教学班号被拒绝");
        expectFail(new Action() {
            public void run() throws BusinessException {
                teacherService.delete("TTEST001");
            }
        }, "删除已开课教师被拒绝");
        expectFail(new Action() {
            public void run() throws BusinessException {
                courseService.delete("CTEST001");
            }
        }, "删除已开课课程被拒绝");
        try {
            int rows = sectionService.adjust("STEST001", "T2023002", 36, "周三5-6节", "教2-209");
            TeachingSection adjusted = sectionService.findByNo("STEST001");
            check("调整教学班容量、教师、时间和地点", rows == 1 && adjusted != null
                    && adjusted.getCapacity() == 36 && "T2023002".equals(adjusted.getTeacherNo())
                    && "李娜".equals(adjusted.getTeacherName()));
            int deleted = sectionService.delete("STEST001");
            check("删除没有关联记录的教学班", deleted == 1 && sectionService.findByNo("STEST001") == null);
            int teacherRows = teacherService.delete("TTEST001");
            int courseRows = courseService.delete("CTEST001");
            check("教学班删除后可以删除教师和课程", teacherRows == 1 && courseRows == 1);
            check("查询不存在的教学班返回空", sectionService.findByNo("S0000000") == null);
            check("查询不存在的学期返回空列表", sectionService.findBySemester("1999-2000-1").isEmpty());
        } catch (BusinessException ex) {
            check("调整或删除教学班：" + ex.getMessage(), false);
        }
    }

    private static void cleanup(TeacherService teacherService, CourseService courseService,
                                TeachingSectionService sectionService) {
        tryDeleteSection(sectionService, "STEST001");
        tryDeleteTeacher(teacherService, "TTEST001");
        tryDeleteCourse(courseService, "CTEST001");
        tryDeleteCourse(courseService, "CTEST002");
    }

    private static void tryDeleteSection(TeachingSectionService sectionService, String sectionNo) {
        try {
            sectionService.delete(sectionNo);
        } catch (BusinessException ignored) {
        }
    }

    private static void tryDeleteTeacher(TeacherService teacherService, String teacherNo) {
        try {
            teacherService.delete(teacherNo);
        } catch (BusinessException ignored) {
        }
    }

    private static void tryDeleteCourse(CourseService courseService, String courseNo) {
        try {
            courseService.delete(courseNo);
        } catch (BusinessException ignored) {
        }
    }

    private static void expectFail(Action action, String name) {
        try {
            action.run();
            check(name, false);
        } catch (BusinessException ex) {
            check(name + "（" + ex.getMessage() + "）", true);
        }
    }

    private static void check(String name, boolean success) {
        if (success) {
            passed++;
            System.out.println("[通过] " + name);
        } else {
            failed++;
            System.out.println("[失败] " + name);
        }
    }

    private interface Action {
        void run() throws BusinessException;
    }
}
