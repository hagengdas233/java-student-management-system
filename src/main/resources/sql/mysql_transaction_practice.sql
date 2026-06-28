-- MySQL 事务练习脚本
-- 使用前请先连接到项目对应的 student_system 数据库。
-- 本脚本用于学习事务、提交、回滚和隔离级别。

-- 1. 查看当前事务隔离级别
SELECT @@transaction_isolation;
SELECT @@global.transaction_isolation;

-- 2. 设置当前会话隔离级别
-- 只影响当前 MySQL Workbench 查询窗口，不影响其他窗口。
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT @@transaction_isolation;

-- 也可以切回 MySQL InnoDB 默认隔离级别。
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SELECT @@transaction_isolation;

-- 3. 查询学生数据，记录事务开始前的成绩
SELECT * FROM student WHERE id = '903';

-- 4. 开启事务并修改学生成绩，然后回滚
START TRANSACTION;

UPDATE student SET score = 99 WHERE id = '903';

-- 当前窗口在事务内可以看到自己修改后的数据。
SELECT * FROM student WHERE id = '903';

-- 回滚事务，撤销本次 UPDATE。
ROLLBACK;

-- 回滚后再次查询，score 应恢复为修改前的值。
SELECT * FROM student WHERE id = '903';

-- 5. 开启事务并修改学生成绩，然后提交
START TRANSACTION;

UPDATE student SET score = 99 WHERE id = '903';

-- 提交前，当前窗口可以看到 score=99；其他窗口是否能看到，取决于隔离级别和是否提交。
SELECT * FROM student WHERE id = '903';

COMMIT;

-- 提交后，再次查询可以看到已提交的数据。
SELECT * FROM student WHERE id = '903';

-- 6. 如需恢复练习数据，可再执行一次事务把成绩改回示例值。
START TRANSACTION;

UPDATE student SET score = 88 WHERE id = '903';

COMMIT;

SELECT * FROM student WHERE id = '903';

-- 7. 两个窗口演示：READ COMMITTED 下的不可重复读
-- 操作方式：
-- 窗口 A 执行下面 A 组 SQL。
-- 窗口 B 在窗口 A 两次 SELECT 之间执行 B 组 SQL。
--
-- 窗口 A：
-- SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
-- START TRANSACTION;
-- SELECT * FROM student WHERE id = '903';
-- -- 等窗口 B 修改并 COMMIT 后，再执行：
-- SELECT * FROM student WHERE id = '903';
-- COMMIT;
--
-- 窗口 B：
-- START TRANSACTION;
-- UPDATE student SET score = 99 WHERE id = '903';
-- COMMIT;
--
-- 现象说明：
-- 在 READ COMMITTED 下，窗口 A 第二次 SELECT 可能读到窗口 B 已提交的新 score。
-- 同一个事务内两次读取同一行结果不同，就是不可重复读。

-- 8. 两个窗口演示：REPEATABLE READ 下的可重复读
-- 操作方式：
-- 窗口 A 执行下面 A 组 SQL。
-- 窗口 B 在窗口 A 两次 SELECT 之间执行 B 组 SQL。
--
-- 窗口 A：
-- SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- START TRANSACTION;
-- SELECT * FROM student WHERE id = '903';
-- -- 等窗口 B 修改并 COMMIT 后，再执行：
-- SELECT * FROM student WHERE id = '903';
-- COMMIT;
--
-- 窗口 B：
-- START TRANSACTION;
-- UPDATE student SET score = 100 WHERE id = '903';
-- COMMIT;
--
-- 现象说明：
-- 在 REPEATABLE READ 下，窗口 A 两次普通 SELECT 通常看到相同结果。
-- 这是因为 InnoDB 使用 MVCC，让同一个事务内的快照读保持稳定。

-- 9. 实验一：普通 SELECT 快照读，观察 REPEATABLE READ 下的一致性快照
-- 操作方式：
-- 窗口 A 执行下面 A 组 SQL。
-- 窗口 B 在窗口 A 两次范围查询之间执行 B 组 SQL。
--
-- 窗口 A：
-- SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- START TRANSACTION;
-- SELECT * FROM student WHERE score >= 80;
-- -- 等窗口 B 插入并 COMMIT 后，再执行同样的普通 SELECT：
-- SELECT * FROM student WHERE score >= 80;
-- COMMIT;
--
-- 窗口 B：
-- START TRANSACTION;
-- INSERT INTO student (id, name, age, score, create_user_id, create_username)
-- VALUES ('tx_snapshot_1', '事务快照读练习', 20, 90, 1, 'tx_user');
-- COMMIT;
--
-- 现象说明：
-- 在 REPEATABLE READ 下，窗口 A 的两次普通 SELECT 通常看到同一份一致性快照。
-- 即使窗口 B 已经插入 score >= 80 的新学生并提交，窗口 A 在同一个事务内的第二次普通 SELECT 通常仍看不到这条新数据。

-- 10. 实验二：当前读，观察 SELECT ... FOR UPDATE 加锁
-- 操作方式：
-- 窗口 A 执行下面 A 组 SQL，先不要 COMMIT。
-- 窗口 B 在窗口 A 未提交时执行 B 组 SQL，观察是否阻塞或等待。
--
-- 窗口 A：
-- SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- START TRANSACTION;
-- SELECT * FROM student WHERE score >= 80 FOR UPDATE;
-- -- 保持事务不提交，切到窗口 B 执行插入或修改。
-- -- 观察完后再执行：
-- COMMIT;
--
-- 窗口 B 方案 1：尝试插入符合条件的数据
-- START TRANSACTION;
-- INSERT INTO student (id, name, age, score, create_user_id, create_username)
-- VALUES ('tx_lock_1', '事务当前读练习', 21, 90, 1, 'tx_user');
-- COMMIT;
--
-- 窗口 B 方案 2：尝试修改已有数据，让它符合 score >= 80
-- START TRANSACTION;
-- UPDATE student SET score = 95 WHERE id = '903';
-- COMMIT;
--
-- 现象说明：
-- SELECT ... FOR UPDATE 是当前读，会读取最新已提交数据并加锁。
-- 在 REPEATABLE READ 下，范围当前读如果走合适索引，InnoDB 可能使用行锁、间隙锁、临键锁。
-- 窗口 B 插入或修改符合 score >= 80 条件的数据时，可能被阻塞或等待窗口 A 提交。
-- 是否阻塞、锁范围有多大，和索引、SQL 条件、数据分布、执行计划有关。

-- 11. 练习结束后，可以按需要清理实验数据并恢复 score。
-- START TRANSACTION;
-- DELETE FROM student WHERE id IN ('tx_snapshot_1', 'tx_lock_1');
-- UPDATE student SET score = 88 WHERE id = '903';
-- COMMIT;
