package dao;

import entity.TeachingSection;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeachingSectionDAO {
    private static final String SELECT_COLUMNS = "SELECT s.section_no, s.course_no, c.course_name, "
            + "s.teacher_no, t.teacher_name, s.semester, s.capacity, s.class_time, s.location "
            + "FROM teaching_section s "
            + "INNER JOIN course c ON s.course_no = c.course_no "
            + "INNER JOIN teacher t ON s.teacher_no = t.teacher_no ";

    public int insert(TeachingSection section) throws SQLException {
        String sql = "INSERT INTO teaching_section "
                + "(section_no, course_no, teacher_no, semester, capacity, class_time, location) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, section.getSectionNo());
            statement.setString(2, section.getCourseNo());
            statement.setString(3, section.getTeacherNo());
            statement.setString(4, section.getSemester());
            statement.setInt(5, section.getCapacity());
            statement.setString(6, section.getClassTime());
            statement.setString(7, section.getLocation());
            return statement.executeUpdate();
        }
    }

    public int updateSchedule(TeachingSection section) throws SQLException {
        String sql = "UPDATE teaching_section SET teacher_no = ?, capacity = ?, class_time = ?, location = ? "
                + "WHERE section_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, section.getTeacherNo());
            statement.setInt(2, section.getCapacity());
            statement.setString(3, section.getClassTime());
            statement.setString(4, section.getLocation());
            statement.setString(5, section.getSectionNo());
            return statement.executeUpdate();
        }
    }

    public int deleteByNo(String sectionNo) throws SQLException {
        String sql = "DELETE FROM teaching_section WHERE section_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sectionNo);
            return statement.executeUpdate();
        }
    }

    public TeachingSection findByNo(String sectionNo) throws SQLException {
        String sql = SELECT_COLUMNS + "WHERE s.section_no = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sectionNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapRow(resultSet);
            }
        }
    }

    public List<TeachingSection> findBySemester(String semester) throws SQLException {
        String sql = SELECT_COLUMNS + "WHERE s.semester = ? ORDER BY s.section_no";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, semester);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapList(resultSet);
            }
        }
    }

    public List<TeachingSection> findAll() throws SQLException {
        String sql = SELECT_COLUMNS + "ORDER BY s.section_no";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapList(resultSet);
        }
    }

    private List<TeachingSection> mapList(ResultSet resultSet) throws SQLException {
        List<TeachingSection> sections = new ArrayList<TeachingSection>();
        while (resultSet.next()) {
            sections.add(mapRow(resultSet));
        }
        return sections;
    }

    private TeachingSection mapRow(ResultSet resultSet) throws SQLException {
        TeachingSection section = new TeachingSection();
        section.setSectionNo(resultSet.getString("section_no"));
        section.setCourseNo(resultSet.getString("course_no"));
        section.setCourseName(resultSet.getString("course_name"));
        section.setTeacherNo(resultSet.getString("teacher_no"));
        section.setTeacherName(resultSet.getString("teacher_name"));
        section.setSemester(resultSet.getString("semester"));
        section.setCapacity(resultSet.getInt("capacity"));
        section.setClassTime(resultSet.getString("class_time"));
        section.setLocation(resultSet.getString("location"));
        return section;
    }
}
