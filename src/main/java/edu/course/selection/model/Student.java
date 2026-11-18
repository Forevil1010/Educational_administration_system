package edu.course.selection.model;

public class Student {
    private String studentId;
    private String studentName;
    private String gender;
    private String major;
    private String className;

    public Student() {
    }

    public Student(String studentId, String studentName, String gender, String major, String className) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gender = gender;
        this.major = major;
        this.className = className;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return studentId + "\t" + studentName + "\t" + gender + "\t" + major + "\t" + className;
    }
}
