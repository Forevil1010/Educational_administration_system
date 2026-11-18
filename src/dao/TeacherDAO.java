package dao;

import entity.Teacher;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {
    public int insert(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teacher (teacher_no, teacher_name, title, department) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacher.getTeacherNo());
            statement.setString(2, teacher.getTeacherName());
            statement.setString(3, teacher.getTitle());
            statement.setString(4, teacher.getDepartment());
            return statement.executeUpdate();
        }
    }

    public int updateTitleAndDepartment(String teacherNo, String title, String department) throws SQLException {
        String sql = "UPDATE teacher SET title = ?, department = ? WHERE teacher_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, department);
            statement.setString(3, teacherNo);
            return statement.executeUpdate();
        }
    }

    public int deleteByNo(String teacherNo) throws SQLException {
        String sql = "DELETE FROM teacher WHERE teacher_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherNo);
            return statement.executeUpdate();
        }
    }

    public Teacher findByNo(String teacherNo) throws SQLException {
        String sql = "SELECT teacher_no, teacher_name, title, department FROM teacher WHERE teacher_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapRow(resultSet);
            }
        }
    }

    public List<Teacher> findByName(String teacherName) throws SQLException {
        String sql = "SELECT teacher_no, teacher_name, title, department FROM teacher "
                + "WHERE teacher_name LIKE ? ORDER BY teacher_no";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + teacherName + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapList(resultSet);
            }
        }
    }

    public List<Teacher> findAll() throws SQLException {
        String sql = "SELECT teacher_no, teacher_name, title, department FROM teacher ORDER BY teacher_no";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapList(resultSet);
        }
    }

    private List<Teacher> mapList(ResultSet resultSet) throws SQLException {
        List<Teacher> teachers = new ArrayList<Teacher>();
        while (resultSet.next()) {
            teachers.add(mapRow(resultSet));
        }
        return teachers;
    }

    private Teacher mapRow(ResultSet resultSet) throws SQLException {
        return new Teacher(
                resultSet.getString("teacher_no"),
                resultSet.getString("teacher_name"),
                resultSet.getString("title"),
                resultSet.getString("department"));
    }
}
