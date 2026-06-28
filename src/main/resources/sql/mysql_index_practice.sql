-- MySQL 索引练习脚本
-- 使用前请先连接到项目对应的 student_system 数据库。
-- 本脚本用于学习 explain 和索引效果对比。

-- 1. 查看 student 表结构
DESC student;
SHOW FULL COLUMNS FROM student;

-- 2. 查看 student 当前索引
SHOW INDEX FROM student;

-- 3. 建索引前：使用 explain 分析根据 id 查询
-- id 是主键，本身已经有主键索引，通常会使用 PRIMARY。
EXPLAIN SELECT * FROM student WHERE id = '903';

-- 4. 建索引前：使用 explain 分析根据 create_user_id 查询
-- create_user_id 用于 /students/my 按创建人查询；没有索引时可能全表扫描。
EXPLAIN SELECT * FROM student WHERE create_user_id = 1;

-- 5. 建索引前：使用 explain 分析 name like 查询
-- LIKE '%张%' 属于前后模糊查询，通常不容易使用普通 B+ 树索引。
EXPLAIN SELECT * FROM student WHERE name LIKE '%张%';

-- 6. 建索引前：使用 explain 分析 score 范围查询
-- score 可用于范围查询索引练习。
EXPLAIN SELECT * FROM student WHERE score >= 80 AND score <= 100;

-- 7. 给 create_user_id 创建普通索引
-- 如果重复执行本脚本，索引已存在时这里会报 Duplicate key name，可先手动 DROP INDEX 后再练习。
CREATE INDEX idx_student_create_user_id ON student(create_user_id);

-- 8. 给 score 创建普通索引
CREATE INDEX idx_student_score ON student(score);

-- 9. 再次查看当前索引，确认新增索引是否存在
SHOW INDEX FROM student;

-- 10. 建索引后：再次 explain，根据 create_user_id 查询
-- 对比 possible_keys、key、type、rows，观察是否使用 idx_student_create_user_id。
EXPLAIN SELECT * FROM student WHERE create_user_id = 1;

-- 11. 建索引后：再次 explain，score 范围查询
-- 对比 possible_keys、key、type、rows，观察是否使用 idx_student_score，type 是否可能变为 range。
EXPLAIN SELECT * FROM student WHERE score >= 80 AND score <= 100;

-- 12. 建索引后：再次 explain，根据 id 查询
-- id 是主键索引，建普通索引前后通常不影响它使用 PRIMARY。
EXPLAIN SELECT * FROM student WHERE id = '903';

-- 13. 建索引后：再次 explain，name 前后模糊查询
-- 本脚本没有给 name 建索引；即使有普通索引，LIKE '%张%' 通常也不容易有效使用。
EXPLAIN SELECT * FROM student WHERE name LIKE '%张%';
