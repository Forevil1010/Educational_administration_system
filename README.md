# 高校教学管理系统

成员 A 和成员 B 的代码已经放在同一个分支里。教学班表名和主键还没有约定：成员 B 使用 `teaching_section.section_no`，成员 A 的选课表引用 `teaching_class.teaching_class_id`。约定之前，不要把 `sql` 目录里的两段脚本连成一次执行。

## 成员 B：开课

当前完成可以独立运行和测试的开课功能：教师维护、课程维护、开设教学班、调整教学班。成绩录入、成绩修改、成绩查询和教学班成绩统计依赖学生表、选课表，尚未实现。

### 环境

- JDK 8
- MySQL 8.4
- MySQL Connector/J 8.4.0
- 程序形式：Java 命令行

数据库连接写在 `config/db.properties`。本地默认端口 `3307`，库名 `edu_admin`，用户 `root`，密码 `root`。端口使用 3307，是为了避开本机已经占用的 3306。

### 建库

在 MySQL 8.4 中创建数据库后，在项目根目录执行 `sql/create_tables.sql` 和 `sql/insert_data.sql` 里成员 B 的那一段，以及 `sql/query.sql` 里成员 B 的查询。

```text
CREATE DATABASE edu_admin DEFAULT CHARACTER SET utf8mb4;
```

### 编译和运行

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

### 已实现功能

- 添加、按教师号或姓名查询、修改职称和所属单位、删除教师。已开设教学班的教师不能删除。
- 添加、按课程号或课程名查询、修改学分和学时、删除课程。已开设教学班的课程不能删除。
- 开设教学班。课程和教师必须已存在，容量必须大于 0。
- 调整教学班的容量、授课教师、上课时间和地点。
- 删除尚未被其他表引用的教学班。

已开课教师或课程被删除时，程序提示外键限制，不产生孤立教学班。查询不到记录时打印空结果，程序不退出。

### 目录

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

## 成员 A：学生与选课

### 技术环境

- Java：JDK 17（项目编译目标为 Java 17）
- 构建工具：Maven 3.9.x
- 数据库：MySQL 8.x
- JDBC 驱动：MySQL Connector/J 8.4.0
- 程序形式：命令行应用，无 Spring、无 Web 前端

### 已实现功能

- 学生添加、按学号查询、查询全部、修改、删除
- 学生课表查询和成绩查询
- 选课事务：检查学生与教学班、防重复、容量检查、退课后重新选课、失败回滚
- 退课事务：检查有效选课记录、更新状态、失败回滚
- 数据库异常和业务异常的中文提示

### 目录

- `sql/create_tables.sql`：学生、选课建表脚本，与成员 B 的脚本暂存在同一文件
- `sql/insert_data.sql`：两张表各 10 条测试数据
- `sql/query.sql`：学生、选课、课表和成绩查询
- `src/main/java/edu/course/selection`：Java 源码
- `docs/member-a-design.md`：需求、概念设计、逻辑设计和 3NF 分析
- `docs/member-a-test-cases.md`：功能与事务测试用例
- `docs/ai-record.md`：选课事务 Prompt、模型不合理初稿及修正记录

### 与成员 B 的接口

A 只创建 `student` 和 `enrollment`，不创建或修改教师、课程、教学班、成绩表。

成员 A 目前写明的字段如下，尚未和成员 B 的教学班表对齐：

- `student.student_id`：`VARCHAR(20)` 主键
- `enrollment.enrollment_id`：`BIGINT` 自增主键
- `enrollment.teaching_class_id`：`BIGINT`，引用 B 的 `teaching_class.teaching_class_id`
- B 的 `grade.enrollment_id`：`BIGINT`，引用 A 的 `enrollment.enrollment_id`

课表和成绩查询还要求 B 提供 `teaching_class(course_id, capacity)`、`course(course_name)`、`grade(score, is_recorded)`。完整说明见 `docs/member-a-design.md`。

推荐合稿执行顺序：

1. B 创建教师、课程、教学班。
2. A 执行 `create_tables.sql` 创建学生和选课。
3. B 创建引用选课的成绩表。
4. A 插入学生，B 插入教学班，A 再执行 `insert_data.sql` 中的选课数据。
5. B 插入成绩，双方分别执行查询和测试。

A 的选课测试数据使用教学班号 1—10；如 B 使用其他编号，合稿时统一修改测试数据。

### 配置数据库连接

编辑 `src/main/resources/db.properties`，填写本机 MySQL 用户名和密码。默认连接：

- 数据库：`educational_administration`
- 用户：`root`
- 密码：空，运行前必须按本机情况配置

也可通过 `DB_URL`、`DB_USER`、`DB_PASSWORD` 环境变量覆盖，不要把真实密码提交到公共仓库或发给公共大模型。

### 运行

1. 按上述顺序执行双方 SQL，并配置数据库连接。
2. 在 VSCode 终端运行：

```powershell
mvn compile
mvn exec:java
```

在 IntelliJ IDEA 中直接打开项目根目录的 `pom.xml`，等待 Maven 依赖加载后运行 `edu.course.selection.Main`，结果与 VSCode 一致。

### 验证状态

- Java 编译：已通过 `mvn compile`。
- MySQL 功能与事务测试：待安装并启动 MySQL、合入成员 B 的表后执行；测试记录见 `docs/member-a-test-cases.md`。
