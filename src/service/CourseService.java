package service;

import dao.CourseDAO;
import entity.Course;
import util.SqlExceptions;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO = new CourseDAO();

    public int add(String courseNo, String courseName, BigDecimal credit, int classHours) throws BusinessException {
        Course course = new Course(TeacherService.requireText(courseNo, "课程号"),
                TeacherService.requireText(courseName, "课程名"), checkCredit(credit), checkHours(classHours));
        TeacherService.checkLength(course.getCourseNo(), 20, "课程号");
        TeacherService.checkLength(course.getCourseName(), 50, "课程名");
        try {
            return courseDAO.insert(course);
        } catch (SQLException ex) {
            if (SqlExceptions.isDuplicateKey(ex)) {
                throw new BusinessException("课程号已存在：" + course.getCourseNo());
            }
            throw new BusinessException("添加课程失败：" + ex.getMessage());
        }
    }

    public Course findByNo(String courseNo) throws BusinessException {
        try {
            return courseDAO.findByNo(TeacherService.requireText(courseNo, "课程号"));
        } catch (SQLException ex) {
            throw new BusinessException("查询课程失败：" + ex.getMessage());
        }
    }

    public List<Course> findByName(String courseName) throws BusinessException {
        try {
            return courseDAO.findByName(TeacherService.requireText(courseName, "课程名"));
        } catch (SQLException ex) {
            throw new BusinessException("查询课程失败：" + ex.getMessage());
        }
    }

    public List<Course> findAll() throws BusinessException {
        try {
            return courseDAO.findAll();
        } catch (SQLException ex) {
            throw new BusinessException("查询课程失败：" + ex.getMessage());
        }
    }

    public int updateCreditAndHours(String courseNo, BigDecimal credit, int classHours) throws BusinessException {
        String no = TeacherService.requireText(courseNo, "课程号");
        BigDecimal newCredit = checkCredit(credit);
        int newHours = checkHours(classHours);
        try {
            int rows = courseDAO.updateCreditAndHours(no, newCredit, newHours);
            if (rows == 0) {
                throw new BusinessException("课程不存在：" + no);
            }
            return rows;
        } catch (SQLException ex) {
            throw new BusinessException("修改课程失败：" + ex.getMessage());
        }
    }

    public int delete(String courseNo) throws BusinessException {
        String no = TeacherService.requireText(courseNo, "课程号");
        try {
            int rows = courseDAO.deleteByNo(no);
            if (rows == 0) {
                throw new BusinessException("课程不存在：" + no);
            }
            return rows;
        } catch (SQLException ex) {
            if (SqlExceptions.isReferencedRow(ex)) {
                throw new BusinessException("该课程已开设教学班，不能删除：" + no);
            }
            throw new BusinessException("删除课程失败：" + ex.getMessage());
        }
    }

    static BigDecimal checkCredit(BigDecimal credit) throws BusinessException {
        if (credit == null || credit.compareTo(BigDecimal.ZERO) <= 0 || credit.compareTo(new BigDecimal("10")) > 0) {
            throw new BusinessException("学分必须大于 0 且不超过 10");
        }
        return credit;
    }

    static int checkHours(int classHours) throws BusinessException {
        if (classHours <= 0 || classHours > 200) {
            throw new BusinessException("学时必须大于 0 且不超过 200");
        }
        return classHours;
    }
}
