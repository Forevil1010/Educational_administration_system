package entity;

public class Teacher {
    private String teacherNo;
    private String teacherName;
    private String title;
    private String department;

    public Teacher() {
    }

    public Teacher(String teacherNo, String teacherName, String title, String department) {
        this.teacherNo = teacherNo;
        this.teacherName = teacherName;
        this.title = title;
        this.department = department;
    }

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
