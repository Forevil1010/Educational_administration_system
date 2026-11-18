package dao;

import entity.Course;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    public int insert(Course course) throws SQLException {
        String sql = "INSERT INTO course (course_no, course_name, credit, class_hours) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.getCourseNo());
            statement.setString(2, course.getCourseName());
            statement.setBigDecimal(3, course.getCredit());
            statement.setInt(4, course.getClassHours());
            return statement.executeUpdate();
        }
    }

    public int updateCreditAndHours(String courseNo, java.math.BigDecimal credit, int classHours) throws SQLException {
        String sql = "UPDATE course SET credit = ?, class_hours = ? WHERE course_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, credit);
            statement.setInt(2, classHours);
            statement.setString(3, courseNo);
            return statement.executeUpdate();
        }
    }

    public int deleteByNo(String courseNo) throws SQLException {
        String sql = "DELETE FROM course WHERE course_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseNo);
            return statement.executeUpdate();
        }
    }

    public Course findByNo(String courseNo) throws SQLException {
        String sql = "SELECT course_no, course_name, credit, class_hours FROM course WHERE course_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapRow(resultSet);
            }
        }
    }

    public List<Course> findByName(String courseName) throws SQLException {
        String sql = "SELECT course_no, course_name, credit, class_hours FROM course "
                + "WHERE course_name LIKE ? ORDER BY course_no";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + courseName + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapList(resultSet);
            }
        }
    }

    public List<Course> findAll() throws SQLException {
        String sql = "SELECT course_no, course_name, credit, class_hours FROM course ORDER BY course_no";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapList(resultSet);
        }
    }

    private List<Course> mapList(ResultSet resultSet) throws SQLException {
        List<Course> courses = new ArrayList<Course>();
        while (resultSet.next()) {
            courses.add(mapRow(resultSet));
        }
        return courses;
    }

    private Course mapRow(ResultSet resultSet) throws SQLException {
        return new Course(
                resultSet.getString("course_no"),
                resultSet.getString("course_name"),
                resultSet.getBigDecimal("credit"),
                resultSet.getInt("class_hours"));
    }
}
