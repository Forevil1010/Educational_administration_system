package edu.course.selection.dao;

import edu.course.selection.model.Student;
import edu.course.selection.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void insert(Student student) throws SQLException {
        String sql = "INSERT INTO student "
                + "(student_id, student_name, gender, major, class_name) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            setStudentParameters(statement, student);
            statement.executeUpdate();
        }
    }

    public Student findById(String studentId) throws SQLException {
        String sql = "SELECT student_id, student_name, gender, major, class_name "
                + "FROM student WHERE student_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapStudent(resultSet) : null;
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT student_id, student_name, gender, major, class_name "
                + "FROM student ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(mapStudent(resultSet));
            }
        }
        return students;
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE student SET student_name = ?, gender = ?, major = ?, class_name = ? "
                + "WHERE student_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getStudentName());
            statement.setString(2, student.getGender());
            statement.setString(3, student.getMajor());
            statement.setString(4, student.getClassName());
            statement.setString(5, student.getStudentId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteById(String studentId) throws SQLException {
        String sql = "DELETE FROM student WHERE student_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean exists(Connection connection, String studentId) throws SQLException {
        String sql = "SELECT 1 FROM student WHERE student_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void setStudentParameters(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getStudentId());
        statement.setString(2, student.getStudentName());
        statement.setString(3, student.getGender());
        statement.setString(4, student.getMajor());
        statement.setString(5, student.getClassName());
    }

    private Student mapStudent(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getString("student_id"),
                resultSet.getString("student_name"),
                resultSet.getString("gender"),
                resultSet.getString("major"),
                resultSet.getString("class_name")
        );
    }
}
