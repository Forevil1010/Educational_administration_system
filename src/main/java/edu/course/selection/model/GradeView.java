package edu.course.selection.model;

import java.math.BigDecimal;

public class GradeView {
    private final long enrollmentId;
    private final long teachingClassId;
    private final String courseName;
    private final BigDecimal score;
    private final boolean recorded;

    public GradeView(long enrollmentId, long teachingClassId, String courseName,
                     BigDecimal score, boolean recorded) {
        this.enrollmentId = enrollmentId;
        this.teachingClassId = teachingClassId;
        this.courseName = courseName;
        this.score = score;
        this.recorded = recorded;
    }

    @Override
    public String toString() {
        String scoreText = recorded && score != null ? score.toString() : "未录入";
        return "选课号=" + enrollmentId
                + "，教学班号=" + teachingClassId
                + "，课程=" + courseName
                + "，成绩=" + scoreText;
    }
}
