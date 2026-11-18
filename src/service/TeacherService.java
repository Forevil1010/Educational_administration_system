package service;

import dao.TeacherDAO;
import entity.Teacher;
import util.SqlExceptions;

import java.sql.SQLException;
import java.util.List;

public class TeacherService {
    private final TeacherDAO teacherDAO = new TeacherDAO();

    public int add(String teacherNo, String teacherName, String title, String department) throws BusinessException {
        Teacher teacher = new Teacher(requireText(teacherNo, "教师号"), requireText(teacherName, "姓名"),
                requireText(title, "职称"), requireText(department, "所属单位"));
        checkLength(teacher.getTeacherNo(), 20, "教师号");
        checkLength(teacher.getTeacherName(), 50, "姓名");
        checkLength(teacher.getTitle(), 20, "职称");
        checkLength(teacher.getDepartment(), 50, "所属单位");
        try {
            return teacherDAO.insert(teacher);
        } catch (SQLException ex) {
            if (SqlExceptions.isDuplicateKey(ex)) {
                throw new BusinessException("教师号已存在：" + teacher.getTeacherNo());
            }
            throw new BusinessException("添加教师失败：" + ex.getMessage());
        }
    }

    public Teacher findByNo(String teacherNo) throws BusinessException {
        try {
            return teacherDAO.findByNo(requireText(teacherNo, "教师号"));
        } catch (SQLException ex) {
            throw new BusinessException("查询教师失败：" + ex.getMessage());
        }
    }

    public List<Teacher> findByName(String teacherName) throws BusinessException {
        try {
            return teacherDAO.findByName(requireText(teacherName, "姓名"));
        } catch (SQLException ex) {
            throw new BusinessException("查询教师失败：" + ex.getMessage());
        }
    }

    public List<Teacher> findAll() throws BusinessException {
        try {
            return teacherDAO.findAll();
        } catch (SQLException ex) {
            throw new BusinessException("查询教师失败：" + ex.getMessage());
        }
    }

    public int updateTitleAndDepartment(String teacherNo, String title, String department) throws BusinessException {
        String no = requireText(teacherNo, "教师号");
        String newTitle = requireText(title, "职称");
        String newDepartment = requireText(department, "所属单位");
        checkLength(newTitle, 20, "职称");
        checkLength(newDepartment, 50, "所属单位");
        try {
            int rows = teacherDAO.updateTitleAndDepartment(no, newTitle, newDepartment);
            if (rows == 0) {
                throw new BusinessException("教师不存在：" + no);
            }
            return rows;
        } catch (SQLException ex) {
            throw new BusinessException("修改教师失败：" + ex.getMessage());
        }
    }

    public int delete(String teacherNo) throws BusinessException {
        String no = requireText(teacherNo, "教师号");
        try {
            int rows = teacherDAO.deleteByNo(no);
            if (rows == 0) {
                throw new BusinessException("教师不存在：" + no);
            }
            return rows;
        } catch (SQLException ex) {
            if (SqlExceptions.isReferencedRow(ex)) {
                throw new BusinessException("该教师已开设教学班，不能删除：" + no);
            }
            throw new BusinessException("删除教师失败：" + ex.getMessage());
        }
    }

    static String requireText(String value, String label) throws BusinessException {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(label + "不能为空");
        }
        return value.trim();
    }

    static void checkLength(String value, int maxLength, String label) throws BusinessException {
        if (value.length() > maxLength) {
            throw new BusinessException(label + "长度不能超过 " + maxLength);
        }
    }
}
