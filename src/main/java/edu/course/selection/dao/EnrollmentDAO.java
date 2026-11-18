package edu.course.selection.dao;

import edu.course.selection.model.CourseSelectionView;
import edu.course.selection.model.Enrollment;
import edu.course.selection.model.GradeView;
import edu.course.selection.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public Integer lockTeachingClassCapacity(Connection connection, long teachingClassId) throws SQLException {
        String sql = "SELECT capacity FROM teaching_class WHERE teaching_class_id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, teachingClassId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("capacity") : null;
            }
        }
    }

    public Enrollment findByStudentAndClassForUpdate(Connection connection, String studentId,
                                                    long teachingClassId) throws SQLException {
        String sql = "SELECT enrollment_id, student_id, teaching_class_id, status "
                + "FROM enrollment WHERE student_id = ? AND teaching_class_id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setLong(2, teachingClassId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new Enrollment(
                        resultSet.getLong("enrollment_id"),
                        resultSet.getString("student_id"),
                        resultSet.getLong("teaching_class_id"),
                        resultSet.getString("status")
                );
            }
        }
    }

    public int countActiveByTeachingClass(Connection connection, long teachingClassId) throws SQLException {
        String sql = "SELECT COUNT(*) AS active_count FROM enrollment "
                + "WHERE teaching_class_id = ? AND status = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, teachingClassId);
            statement.setString(2, Enrollment.STATUS_ENROLLED);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("active_count");
            }
        }
    }

    public long insert(Connection connection, String studentId, long teachingClassId) throws SQLException {
        String sql = "INSERT INTO enrollment (student_id, teaching_class_id, status) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, studentId);
            statement.setLong(2, teachingClassId);
            statement.setString(3, Enrollment.STATUS_ENROLLED);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("新增选课记录后未获得选课号");
    }

    public boolean updateStatus(Connection connection, long enrollmentId, String status) throws SQLException {
        String sql = "UPDATE enrollment SET status = ?, "
                + "enrolled_at = CASE WHEN ? = '已选' THEN CURRENT_TIMESTAMP ELSE enrolled_at END "
                + "WHERE enrollment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, status);
            statement.setLong(3, enrollmentId);
            return statement.executeUpdate() == 1;
        }
    }

    public List<CourseSelectionView> findScheduleByStudent(String studentId) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.teaching_class_id, c.course_name, e.status, e.enrolled_at "
                + "FROM enrollment e "
                + "JOIN teaching_class tc ON e.teaching_class_id = tc.teaching_class_id "
                + "JOIN course c ON tc.course_id = c.course_id "
                + "WHERE e.student_id = ? AND e.status = ? ORDER BY e.enrollment_id";
        List<CourseSelectionView> selections = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, Enrollment.STATUS_ENROLLED);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    selections.add(new CourseSelectionView(
                            resultSet.getLong("enrollment_id"),
                            resultSet.getLong("teaching_class_id"),
                            resultSet.getString("course_name"),
                            resultSet.getString("status"),
                            resultSet.getTimestamp("enrolled_at")
                    ));
                }
            }
        }
        return selections;
    }

    public List<GradeView> findGradesByStudent(String studentId) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.teaching_class_id, c.course_name, g.score, g.is_recorded "
                + "FROM enrollment e "
                + "JOIN teaching_class tc ON e.teaching_class_id = tc.teaching_class_id "
                + "JOIN course c ON tc.course_id = c.course_id "
                + "LEFT JOIN grade g ON e.enrollment_id = g.enrollment_id "
                + "WHERE e.student_id = ? AND e.status = ? ORDER BY e.enrollment_id";
        List<GradeView> grades = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, Enrollment.STATUS_ENROLLED);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    grades.add(new GradeView(
                            resultSet.getLong("enrollment_id"),
                            resultSet.getLong("teaching_class_id"),
                            resultSet.getString("course_name"),
                            resultSet.getBigDecimal("score"),
                            resultSet.getBoolean("is_recorded")
                    ));
                }
            }
        }
        return grades;
    }
}
