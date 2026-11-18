package service;

import dao.CourseDAO;
import dao.TeacherDAO;
import dao.TeachingSectionDAO;
import entity.TeachingSection;
import util.SqlExceptions;

import java.sql.SQLException;
import java.util.List;

public class TeachingSectionService {
    private final TeachingSectionDAO sectionDAO = new TeachingSectionDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    public int open(String sectionNo, String courseNo, String teacherNo, String semester,
                    int capacity, String classTime, String location) throws BusinessException {
        TeachingSection section = build(sectionNo, courseNo, teacherNo, semester, capacity, classTime, location);
        ensureCourseExists(section.getCourseNo());
        ensureTeacherExists(section.getTeacherNo());
        try {
            return sectionDAO.insert(section);
        } catch (SQLException ex) {
            if (SqlExceptions.isDuplicateKey(ex)) {
                throw new BusinessException("教学班号已存在：" + section.getSectionNo());
            }
            if (SqlExceptions.isMissingParent(ex)) {
                throw new BusinessException("课程或教师不存在，不能开设教学班");
            }
            throw new BusinessException("开设教学班失败：" + ex.getMessage());
        }
    }

    public int adjust(String sectionNo, String teacherNo, int capacity, String classTime, String location)
            throws BusinessException {
        String no = TeacherService.requireText(sectionNo, "教学班号");
        TeachingSection existing = findByNo(no);
        if (existing == null) {
            throw new BusinessException("教学班不存在：" + no);
        }
        String newTeacherNo = TeacherService.requireText(teacherNo, "教师号");
        ensureTeacherExists(newTeacherNo);
        int newCapacity = checkCapacity(capacity);
        String newTime = TeacherService.requireText(classTime, "上课时间");
        String newLocation = TeacherService.requireText(location, "上课地点");
        TeacherService.checkLength(newTime, 50, "上课时间");
        TeacherService.checkLength(newLocation, 50, "上课地点");
        // 新容量不能小于已选人数，要等选课表完成后补充。
        existing.setTeacherNo(newTeacherNo);
        existing.setCapacity(newCapacity);
        existing.setClassTime(newTime);
        existing.setLocation(newLocation);
        try {
            int rows = sectionDAO.updateSchedule(existing);
            if (rows == 0) {
                throw new BusinessException("教学班不存在：" + no);
            }
            return rows;
        } catch (SQLException ex) {
            if (SqlExceptions.isMissingParent(ex)) {
                throw new BusinessException("教师不存在：" + newTeacherNo);
            }
            throw new BusinessException("调整教学班失败：" + ex.getMessage());
        }
    }

    public TeachingSection findByNo(String sectionNo) throws BusinessException {
        try {
            return sectionDAO.findByNo(TeacherService.requireText(sectionNo, "教学班号"));
        } catch (SQLException ex) {
            throw new BusinessException("查询教学班失败：" + ex.getMessage());
        }
    }

    public List<TeachingSection> findBySemester(String semester) throws BusinessException {
        try {
            return sectionDAO.findBySemester(TeacherService.requireText(semester, "学期"));
        } catch (SQLException ex) {
            throw new BusinessException("查询教学班失败：" + ex.getMessage());
        }
    }

    public List<TeachingSection> findAll() throws BusinessException {
        try {
            return sectionDAO.findAll();
        } catch (SQLException ex) {
            throw new BusinessException("查询教学班失败：" + ex.getMessage());
        }
    }

    public int delete(String sectionNo) throws BusinessException {
        String no = TeacherService.requireText(sectionNo, "教学班号");
        try {
            int rows = sectionDAO.deleteByNo(no);
            if (rows == 0) {
                throw new BusinessException("教学班不存在：" + no);
            }
            return rows;
        } catch (SQLException ex) {
            if (SqlExceptions.isReferencedRow(ex)) {
                throw new BusinessException("该教学班已有关联记录，不能删除：" + no);
            }
            throw new BusinessException("删除教学班失败：" + ex.getMessage());
        }
    }

    private TeachingSection build(String sectionNo, String courseNo, String teacherNo, String semester,
                                  int capacity, String classTime, String location) throws BusinessException {
        TeachingSection section = new TeachingSection();
        section.setSectionNo(TeacherService.requireText(sectionNo, "教学班号"));
        section.setCourseNo(TeacherService.requireText(courseNo, "课程号"));
        section.setTeacherNo(TeacherService.requireText(teacherNo, "教师号"));
        section.setSemester(TeacherService.requireText(semester, "学期"));
        section.setCapacity(checkCapacity(capacity));
        section.setClassTime(TeacherService.requireText(classTime, "上课时间"));
        section.setLocation(TeacherService.requireText(location, "上课地点"));
        TeacherService.checkLength(section.getSectionNo(), 20, "教学班号");
        TeacherService.checkLength(section.getCourseNo(), 20, "课程号");
        TeacherService.checkLength(section.getTeacherNo(), 20, "教师号");
        TeacherService.checkLength(section.getSemester(), 20, "学期");
        TeacherService.checkLength(section.getClassTime(), 50, "上课时间");
        TeacherService.checkLength(section.getLocation(), 50, "上课地点");
        return section;
    }

    private void ensureTeacherExists(String teacherNo) throws BusinessException {
        try {
            if (teacherDAO.findByNo(teacherNo) == null) {
                throw new BusinessException("教师不存在：" + teacherNo);
            }
        } catch (SQLException ex) {
            throw new BusinessException("查询教师失败：" + ex.getMessage());
        }
    }

    private void ensureCourseExists(String courseNo) throws BusinessException {
        try {
            if (courseDAO.findByNo(courseNo) == null) {
                throw new BusinessException("课程不存在：" + courseNo);
            }
        } catch (SQLException ex) {
            throw new BusinessException("查询课程失败：" + ex.getMessage());
        }
    }

    private int checkCapacity(int capacity) throws BusinessException {
        if (capacity <= 0 || capacity > 500) {
            throw new BusinessException("容量必须大于 0 且不超过 500");
        }
        return capacity;
    }
}
