package edu.course.selection.service;

import edu.course.selection.dao.EnrollmentDAO;
import edu.course.selection.dao.StudentDAO;
import edu.course.selection.model.Enrollment;
import edu.course.selection.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class EnrollmentService {
    private final StudentDAO studentDAO = new StudentDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public long enroll(String studentId, long teachingClassId) throws SQLException {
        try (Connection connection = DBUtil.getConnection()) {
            try {
                connection.setAutoCommit(false);

                if (!studentDAO.exists(connection, studentId)) {
                    throw new IllegalStateException("学生不存在，不能选课");
                }

                // 同一教学班的选课先锁定该行，避免并发检查容量后发生超额录取。
                Integer capacity = enrollmentDAO.lockTeachingClassCapacity(connection, teachingClassId);
                if (capacity == null) {
                    throw new IllegalStateException("教学班不存在");
                }

                Enrollment existing = enrollmentDAO.findByStudentAndClassForUpdate(
                        connection, studentId, teachingClassId);
                if (existing != null && Enrollment.STATUS_ENROLLED.equals(existing.getStatus())) {
                    throw new IllegalStateException("不能重复选择同一教学班");
                }

                int activeCount = enrollmentDAO.countActiveByTeachingClass(connection, teachingClassId);
                if (activeCount >= capacity) {
                    throw new IllegalStateException("教学班名额已满，选课失败");
                }

                long enrollmentId;
                if (existing == null) {
                    enrollmentId = enrollmentDAO.insert(connection, studentId, teachingClassId);
                } else {
                    if (!enrollmentDAO.updateStatus(
                            connection, existing.getEnrollmentId(), Enrollment.STATUS_ENROLLED)) {
                        throw new SQLException("重新选课失败，未更新原选课记录");
                    }
                    enrollmentId = existing.getEnrollmentId();
                }

                connection.commit();
                return enrollmentId;
            } catch (SQLException | RuntimeException e) {
                rollback(connection, e);
                throw e;
            }
        }
    }

    public void drop(String studentId, long teachingClassId) throws SQLException {
        try (Connection connection = DBUtil.getConnection()) {
            try {
                connection.setAutoCommit(false);

                if (enrollmentDAO.lockTeachingClassCapacity(connection, teachingClassId) == null) {
                    throw new IllegalStateException("教学班不存在");
                }

                Enrollment existing = enrollmentDAO.findByStudentAndClassForUpdate(
                        connection, studentId, teachingClassId);
                if (existing == null || !Enrollment.STATUS_ENROLLED.equals(existing.getStatus())) {
                    throw new IllegalStateException("没有可退的有效选课记录");
                }

                if (!enrollmentDAO.updateStatus(
                        connection, existing.getEnrollmentId(), Enrollment.STATUS_DROPPED)) {
                    throw new SQLException("退课失败，未更新选课记录");
                }

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                rollback(connection, e);
                throw e;
            }
        }
    }

    private void rollback(Connection connection, Exception originalException) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }
}
