# 高校教学管理系统（开课部分）

当前完成可以独立运行和测试的开课功能：教师维护、课程维护、开设教学班、调整教学班。成绩录入、成绩修改、成绩查询和教学班成绩统计依赖学生表、选课表，尚未实现。

## 环境

- JDK 8
- MySQL 8.4
- MySQL Connector/J 8.4.0
- 程序形式：Java 命令行

数据库连接写在 `config/db.properties`。本地默认端口 `3307`，库名 `edu_admin`，用户 `root`，密码 `root`。端口使用 3307，是为了避开本机已经占用的 3306。

## 建库

在 MySQL 8.4 中创建数据库后，在项目根目录执行：

```text
CREATE DATABASE edu_admin DEFAULT CHARACTER SET utf8mb4;
```

然后依次执行：

- `sql/create_tables.sql`
- `sql/insert_data.sql`
- `sql/query.sql`

`query.sql` 里是当前可以独立验证的查询。成绩统计查询等选课数据完成后再补充。

## 编译和运行

将 `mysql-connector-j-8.4.0.jar` 放到 `lib` 目录。在项目根目录执行：

```text
javac -encoding UTF-8 -cp lib/mysql-connector-j-8.4.0.jar -d out src/util/*.java src/entity/*.java src/dao/*.java src/service/*.java src/Main.java src/test/*.java
java "-Dfile.encoding=UTF-8" -cp "out;lib/mysql-connector-j-8.4.0.jar" Main
```

功能测试：

```text
java "-Dfile.encoding=UTF-8" -cp "out;lib/mysql-connector-j-8.4.0.jar" test.IndependentFunctionTest
```

Windows PowerShell 下 classpath 分隔符用分号，`-Dfile.encoding=UTF-8` 需要加引号。源文件在 `src` 下按 `util`、`dao`、`service` 放置，入口是 `src/Main.java`。

## 已实现功能

- 添加、按教师号或姓名查询、修改职称和所属单位、删除教师。已开设教学班的教师不能删除。
- 添加、按课程号或课程名查询、修改学分和学时、删除课程。已开设教学班的课程不能删除。
- 开设教学班。课程和教师必须已存在，容量必须大于 0。
- 调整教学班的容量、授课教师、上课时间和地点。
- 删除尚未被其他表引用的教学班。

已开课教师或课程被删除时，程序提示外键限制，不产生孤立教学班。查询不到记录时打印空结果，程序不退出。

## 目录

```text
src/util/DBUtil.java
src/dao/TeacherDAO.java
src/dao/CourseDAO.java
src/dao/TeachingSectionDAO.java
src/service/TeacherService.java
src/service/CourseService.java
src/service/TeachingSectionService.java
src/Main.java
sql/create_tables.sql
sql/insert_data.sql
sql/query.sql
```
