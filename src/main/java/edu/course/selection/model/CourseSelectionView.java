package edu.course.selection.model;

import java.sql.Timestamp;

public class CourseSelectionView {
    private final long enrollmentId;
    private final long teachingClassId;
    private final String courseName;
    private final String status;
    private final Timestamp enrolledAt;

    public CourseSelectionView(long enrollmentId, long teachingClassId, String courseName,
                               String status, Timestamp enrolledAt) {
        this.enrollmentId = enrollmentId;
        this.teachingClassId = teachingClassId;
        this.courseName = courseName;
        this.status = status;
        this.enrolledAt = enrolledAt;
    }

    @Override
    public String toString() {
        return "选课号=" + enrollmentId
                + "，教学班号=" + teachingClassId
                + "，课程=" + courseName
                + "，状态=" + status
                + "，选课时间=" + enrolledAt;
    }
}
