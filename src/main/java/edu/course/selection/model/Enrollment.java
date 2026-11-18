package edu.course.selection.model;

public class Enrollment {
    public static final String STATUS_ENROLLED = "已选";
    public static final String STATUS_DROPPED = "已退";

    private long enrollmentId;
    private String studentId;
    private long teachingClassId;
    private String status;

    public Enrollment(long enrollmentId, String studentId, long teachingClassId, String status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.teachingClassId = teachingClassId;
        this.status = status;
    }

    public long getEnrollmentId() {
        return enrollmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public long getTeachingClassId() {
        return teachingClassId;
    }

    public String getStatus() {
        return status;
    }
}
