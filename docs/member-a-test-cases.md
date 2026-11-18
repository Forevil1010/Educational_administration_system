# 成员 A 功能与事务测试记录

## 测试前提

- MySQL 8.x 已启动，双方已按 README 的顺序创建数据表。
- 已插入学生、选课和教学班测试数据，并正确配置 `db.properties`。
- 每次涉及容量的测试前，记录该教学班的 `capacity` 和当前“已选”人数。
- 当前仅完成 Java 编译，尚未连接 MySQL 执行以下用例；实际结果必须根据真实运行填写，不能预先写成通过。

| 编号 | 测试场景 | 测试输入/准备 | 预期结果 | 实际结果 | 是否通过 | 问题修复记录 |
|---|---|---|---|---|---|---|
| A01 | 初始化数据核对 | 分别统计 student、enrollment | 两张表均至少有 10 条记录，外键数据有效 | 待执行 | 待执行 | 待记录 |
| A02 | 正常添加学生 | 添加不存在的学号 S2099001，其他字段合法 | 影响行数为 1，可按学号查到该学生 | 待执行 | 待执行 | 待记录 |
| A03 | 重复学号添加 | 再次添加 S2099001 | 唯一性约束生效，程序提示错误，表中不增加重复记录 | 待执行 | 待执行 | 待记录 |
| A04 | 查询不存在的学生 | 查询学号 NOT_EXIST | 显示“未找到该学生”，程序不崩溃 | 待执行 | 待执行 | 待记录 |
| A05 | 修改学生信息 | 修改 S2099001 的专业和行政班 | 指定记录更新，其他学生不变 | 待执行 | 待执行 | 待记录 |
| A06 | 删除无选课学生 | 删除未选课的 S2099001 | 删除成功，再次查询为空 | 待执行 | 待执行 | 待记录 |
| A07 | 删除有关联学生 | 删除已有选课记录的 S2024001 | 外键阻止删除，学生及其选课记录保持完整 | 待执行 | 待执行 | 待记录 |
| A08 | 正常选课 | 学生存在、教学班存在且有空位，学生此前未选该班 | 提交事务，返回选课号，“已选”人数增加 1 | 待执行 | 待执行 | 待记录 |
| A09 | 重复选课 | 对 A08 的同一学生和教学班再次选课 | 提示不能重复选课，记录数和“已选”人数不变 | 待执行 | 待执行 | 待记录 |
| A10 | 名额已满选课 | 准备“已选”人数等于 capacity 的教学班，再让新学生选课 | 提示名额已满并回滚，不产生新记录，人数不变 | 待执行 | 待执行 | 待记录 |
| A11 | 选课异常回滚 | 使用下方临时触发器强制 INSERT 失败，然后正常选课 | 程序报告数据库错误并执行 rollback，选课记录和人数均不变化 | 待执行 | 待执行 | 待记录 |
| A12 | 正常退课 | 对一条“已选”记录执行退课 | 状态改为“已退”，选课号保留，“已选”人数减少 1 | 待执行 | 待执行 | 待记录 |
| A13 | 重复退课 | 对 A12 同一记录再次退课 | 提示没有可退记录，数据不变化 | 待执行 | 待执行 | 待记录 |
| A14 | 退课后重新选课 | 教学班有空位时重新选择 A12 的教学班 | 原记录恢复“已选”，复用原选课号，不产生重复行 | 待执行 | 待执行 | 待记录 |
| A15 | 并发抢最后一个名额 | 两个客户端同时让不同学生选择仅剩 1 个名额的教学班 | 一个事务成功，另一个提示名额已满；最终人数不超过 capacity | 待执行 | 待执行 | 待记录 |

## A11 故障注入与清理

只在隔离的测试数据库执行。触发器会让测试期间所有新增选课失败，用于验证异常路径和回滚处理。

```sql
USE educational_administration;

DELIMITER //
CREATE TRIGGER test_enrollment_insert_failure
BEFORE INSERT ON enrollment
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '测试：模拟选课写入失败';
END//
DELIMITER ;
```

运行一次正常选课并核对失败后，必须立即清理：

```sql
DROP TRIGGER IF EXISTS test_enrollment_insert_failure;
```

核对 SQL：

```sql
SELECT teaching_class_id, status, COUNT(*) AS record_count
FROM enrollment
WHERE teaching_class_id = 被测教学班号
GROUP BY teaching_class_id, status;
```

## A15 并发结果核对

```sql
SELECT tc.teaching_class_id, tc.capacity,
       COUNT(CASE WHEN e.status = '已选' THEN 1 END) AS active_count
FROM teaching_class AS tc
LEFT JOIN enrollment AS e ON tc.teaching_class_id = e.teaching_class_id
WHERE tc.teaching_class_id = 被测教学班号
GROUP BY tc.teaching_class_id, tc.capacity;
```

应满足 `active_count <= capacity`，并保存两个客户端的实际输出及该查询结果作为测试证据。
