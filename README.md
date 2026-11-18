# 高校教学管理系统：成员 A（学生与选课）

## 技术环境

- Java：JDK 17（项目编译目标为 Java 17）
- 构建工具：Maven 3.9.x
- 数据库：MySQL 8.x
- JDBC 驱动：MySQL Connector/J 8.4.0
- 程序形式：命令行应用，无 Spring、无 Web 前端

## 已实现功能

- 学生添加、按学号查询、查询全部、修改、删除
- 学生课表查询和成绩查询
- 选课事务：检查学生与教学班、防重复、容量检查、退课后重新选课、失败回滚
- 退课事务：检查有效选课记录、更新状态、失败回滚
- 数据库异常和业务异常的中文提示

## 目录

- `sql/create_tables.sql`：学生、选课建表脚本
- `sql/insert_data.sql`：两张表各 10 条测试数据
- `sql/query.sql`：学生、选课、课表和成绩查询
- `src/main/java/edu/course/selection`：Java 源码
- `docs/member-a-design.md`：需求、概念设计、逻辑设计和 3NF 分析
- `docs/member-a-test-cases.md`：功能与事务测试用例
- `docs/ai-record.md`：选课事务 Prompt、模型不合理初稿及修正记录

## 与成员 B 的接口

A 只创建 `student` 和 `enrollment`，不创建或修改教师、课程、教学班、成绩表。

统一字段如下：

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

## 配置数据库连接

编辑 `src/main/resources/db.properties`，填写本机 MySQL 用户名和密码。默认连接：

- 数据库：`educational_administration`
- 用户：`root`
- 密码：空，运行前必须按本机情况配置

也可通过 `DB_URL`、`DB_USER`、`DB_PASSWORD` 环境变量覆盖，不要把真实密码提交到公共仓库或发给公共大模型。

## 运行

1. 按上述顺序执行双方 SQL，并配置数据库连接。
2. 在 VSCode 终端运行：

```powershell
mvn compile
mvn exec:java
```

在 IntelliJ IDEA 中直接打开项目根目录的 `pom.xml`，等待 Maven 依赖加载后运行 `edu.course.selection.Main`，结果与 VSCode 一致。

## 验证状态

- Java 编译：已通过 `mvn compile`。
- MySQL 功能与事务测试：待安装并启动 MySQL、合入成员 B 的表后执行；测试记录见 `docs/member-a-test-cases.md`。
