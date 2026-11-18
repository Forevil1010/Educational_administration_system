package entity;

import java.math.BigDecimal;

public class Course {
    private String courseNo;
    private String courseName;
    private BigDecimal credit;
    private int classHours;

    public Course() {
    }

    public Course(String courseNo, String courseName, BigDecimal credit, int classHours) {
        this.courseNo = courseNo;
        this.courseName = courseName;
        this.credit = credit;
        this.classHours = classHours;
    }

    public String getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(String courseNo) {
        this.courseNo = courseNo;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public int getClassHours() {
        return classHours;
    }

    public void setClassHours(int classHours) {
        this.classHours = classHours;
    }
}
