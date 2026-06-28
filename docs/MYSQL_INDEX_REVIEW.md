# MySQL 索引复习

## 1. 索引是什么

索引是数据库为了加快查询速度而维护的一种数据结构。

可以把索引理解成书的目录：

- 没有目录时，要找某个内容，需要从第一页翻到最后一页。
- 有目录时，可以先根据目录定位到大概位置，再去读取目标内容。

在 MySQL 中，索引通常建立在表的一个或多个字段上。查询时，如果 SQL 条件能用到索引，MySQL 就可以少扫描很多数据。

索引本身也需要存储空间，并且在新增、修改、删除数据时要同步维护，所以索引不是越多越好。

## 2. 为什么索引能加快查询

没有索引时，MySQL 可能需要全表扫描，也就是一行一行检查是否满足条件。

例如：

```sql
SELECT * FROM student WHERE create_user_id = 1;
```

如果 `create_user_id` 没有索引，数据量大时 MySQL 可能要扫描整张 `student` 表。

如果给 `create_user_id` 建立索引：

```sql
CREATE INDEX idx_student_create_user_id ON student(create_user_id);
```

MySQL 可以先在索引结构中快速找到 `create_user_id = 1` 对应的数据位置，再回到表中读取完整记录。

简单来说：

- 没有索引：从头到尾找。
- 有索引：先查目录，再定位数据。

## 3. B+ 树索引的基本理解

InnoDB 常用的索引结构是 B+ 树。

B+ 树可以理解成一种多层目录结构：

- 根节点：最上层入口。
- 中间节点：继续缩小查找范围。
- 叶子节点：保存最终索引值，并且叶子节点之间有顺序链表。

B+ 树适合数据库索引，主要原因有：

- 树的高度较低，查找数据时磁盘 IO 次数少。
- 叶子节点有序，适合范围查询，例如 `score >= 80 AND score <= 100`。
- 所有真实数据或主键值都在叶子节点，查询路径稳定。

在 InnoDB 中，主键索引的叶子节点保存整行数据；普通索引的叶子节点保存索引字段和对应的主键值。

## 4. 主键索引、普通索引、联合索引的区别

### 主键索引

主键索引是建立在主键字段上的索引。

特点：

- 一张表只能有一个主键索引。
- 主键值不能为 `NULL`。
- 主键值必须唯一。
- InnoDB 中主键索引的叶子节点保存整行数据。

本项目的 `student.id` 是主键：

```sql
id VARCHAR(20) PRIMARY KEY
```

所以 `id` 本身已经有主键索引，不需要再单独创建普通索引。

### 普通索引

普通索引是最常见的索引类型，用来加快普通字段查询。

例如：

```sql
CREATE INDEX idx_student_create_user_id ON student(create_user_id);
CREATE INDEX idx_student_score ON student(score);
```

普通索引允许字段值重复，也允许为 `NULL`。

### 联合索引

联合索引是在多个字段上建立的索引。

例如：

```sql
CREATE INDEX idx_student_user_score ON student(create_user_id, score);
```

联合索引遵循最左前缀原则。以上索引可以较好支持：

```sql
WHERE create_user_id = 1
WHERE create_user_id = 1 AND score >= 80
```

但不一定能很好支持只按 `score` 查询：

```sql
WHERE score >= 80
```

因为 `score` 不是这个联合索引的最左字段。

## 5. 什么字段适合建索引

适合建索引的字段通常有这些特点：

- 经常出现在 `WHERE` 条件中的字段。
- 经常用于 `JOIN` 关联的字段。
- 经常用于 `ORDER BY` 或 `GROUP BY` 的字段。
- 区分度较高的字段，例如用户 id、手机号、订单号。
- 查询频繁，新增、修改、删除相对不那么频繁的表字段。

不太适合建索引的字段：

- 数据量很小的表。
- 区分度很低的字段，例如性别、是否删除这类只有少量取值的字段。
- 很少作为查询条件的字段。
- 更新非常频繁的字段。

## 6. 什么情况下索引会失效

常见导致索引失效或不容易使用索引的情况包括：

### 6.1 前后模糊查询

```sql
SELECT * FROM student WHERE name LIKE '%张%';
```

`LIKE '%xxx%'` 这种前后模糊查询通常不容易使用普通 B+ 树索引，因为条件左边是不确定的，MySQL 很难从索引树的有序结构中定位起点。

如果是右模糊查询，通常更容易使用索引：

```sql
SELECT * FROM student WHERE name LIKE '张%';
```

### 6.2 对索引字段使用函数

```sql
SELECT * FROM student WHERE CAST(id AS UNSIGNED) = 903;
```

如果对索引字段做函数计算，MySQL 可能无法直接使用原字段上的索引。

### 6.3 对索引字段进行计算

```sql
SELECT * FROM student WHERE score + 10 >= 90;
```

建议改成：

```sql
SELECT * FROM student WHERE score >= 80;
```

### 6.4 类型不一致

如果字段类型和查询参数类型不一致，可能发生隐式类型转换，影响索引使用。

本项目 `student.id` 是 `VARCHAR(20)`，推荐按字符串查询：

```sql
SELECT * FROM student WHERE id = '903';
```

### 6.5 联合索引不满足最左前缀原则

如果有联合索引：

```sql
CREATE INDEX idx_student_user_score ON student(create_user_id, score);
```

查询只使用第二个字段：

```sql
SELECT * FROM student WHERE score >= 80;
```

这个查询没有使用联合索引的最左字段 `create_user_id`，不一定能充分利用该联合索引。

### 6.6 使用 OR 时条件中有字段没有索引

```sql
SELECT * FROM student WHERE create_user_id = 1 OR age = 18;
```

如果 `create_user_id` 有索引，但 `age` 没有索引，优化器可能选择全表扫描。

## 7. explain 的作用

`EXPLAIN` 用来查看一条 SQL 的执行计划。

它不会真正返回业务数据，而是告诉我们 MySQL 大概准备怎么执行这条 SQL，例如：

- 是否可能使用索引。
- 实际使用了哪个索引。
- 预计扫描多少行。
- 查询类型是否高效。
- 是否出现额外操作，例如排序、临时表、回表等。

示例：

```sql
EXPLAIN SELECT * FROM student WHERE create_user_id = 1;
```

学习索引时，`EXPLAIN` 是最重要的验证工具之一。

## 8. explain 常见字段含义

### type

`type` 表示访问类型，能大致看出查询效率。

常见值从较好到较差大致如下：

- `const`：通过主键或唯一索引等值查询，一次就能定位到数据，通常很快。
- `ref`：使用非唯一索引等值查询，可能匹配多行。
- `range`：使用索引做范围查询，例如 `score >= 80`。
- `index`：扫描整个索引。
- `ALL`：全表扫描，通常需要重点关注。

### possible_keys

`possible_keys` 表示 MySQL 认为这条 SQL 可能用到哪些索引。

如果这里是 `NULL`，说明优化器认为没有合适的索引可用。

### key

`key` 表示 MySQL 最终实际选择使用的索引。

如果 `key` 是 `NULL`，说明最终没有使用索引。

### rows

`rows` 表示 MySQL 预计需要扫描的行数。

一般来说，`rows` 越小越好。但它是估算值，不是绝对准确值。

### Extra

`Extra` 表示额外执行信息。

常见内容：

- `Using where`：使用 `WHERE` 条件过滤。
- `Using index`：可能使用了覆盖索引，不需要回表读取完整行。
- `Using index condition`：使用索引条件下推，MySQL 会先在索引层过滤一部分条件，再回表读取数据。
- `Using filesort`：需要额外排序，数据量大时要关注。
- `Using temporary`：使用临时表，常见于部分排序或分组场景。

## 9. MySQL Workbench explain 练习结果

本次练习中，建索引前 `student` 表只有 `PRIMARY` 主键索引。

### 9.1 id 主键查询

执行 SQL：

```sql
EXPLAIN SELECT * FROM student WHERE id = '903';
```

练习结果：

```text
key=PRIMARY
type=const
rows=1
```

说明：`id` 是主键，本身就有主键索引。根据主键等值查询时，MySQL 可以直接定位到一行数据，`type=const`、`rows=1`，查询效率很高。

### 9.2 create_user_id 建索引前

执行 SQL：

```sql
EXPLAIN SELECT * FROM student WHERE create_user_id = 1;
```

建索引前结果：

```text
type=ALL
key=NULL
rows≈11
```

说明：`type=ALL` 表示全表扫描，`key=NULL` 表示没有实际使用索引。此时 MySQL 预计扫描约 11 行，数据量继续变大后，这类查询会越来越慢。

### 9.3 给 create_user_id 创建索引

执行 SQL：

```sql
CREATE INDEX idx_student_create_user_id ON student(create_user_id);
```

这个索引用来优化按创建人查询学生的场景，例如 `/students/my`。

### 9.4 create_user_id 建索引后

再次执行：

```sql
EXPLAIN SELECT * FROM student WHERE create_user_id = 1;
```

建索引后结果：

```text
possible_keys=idx_student_create_user_id
key=idx_student_create_user_id
type=ref
rows≈2
```

说明：`possible_keys` 和 `key` 都显示 `idx_student_create_user_id`，表示 MySQL 认为该索引可用，并且最终实际使用了它。`type=ref` 表示使用普通索引做等值查询，`rows≈2` 表示预计扫描行数明显减少。

结论：`/students/my` 这类按创建人查询的接口，可以通过 `create_user_id` 索引减少扫描行数。

### 9.5 给 score 创建索引

执行 SQL：

```sql
CREATE INDEX idx_student_score ON student(score);
```

这个索引用于练习成绩范围查询。

### 9.6 score 范围查询建索引后

执行 SQL：

```sql
EXPLAIN SELECT * FROM student WHERE score >= 80 AND score <= 100;
```

建索引后结果：

```text
possible_keys=idx_student_score
key=idx_student_score
type=range
rows≈6
Extra=Using index condition
```

说明：`type=range` 表示 MySQL 使用索引做范围扫描，`key=idx_student_score` 表示实际使用了 `score` 索引，`Extra=Using index condition` 表示 MySQL 在索引层先做一部分条件过滤。

结论：`score` 范围查询可以使用范围索引。

### 9.7 name 前后模糊查询

执行 SQL：

```sql
EXPLAIN SELECT * FROM student WHERE name LIKE '%张%';
```

练习结果：

```text
type=ALL
key=NULL
```

说明：`LIKE '%张%'` 是前后模糊查询，左侧 `%` 导致 MySQL 很难利用普通 B+ 树索引的有序性定位扫描起点，所以通常不容易使用普通 B+ 树索引。

## 10. 结合 student 表说明哪些查询适合建索引

本项目 `student` 表核心字段包括：

```sql
id
name
age
score
create_user_id
create_username
```

### 10.1 根据 id 查询学生详情

对应接口：

```http
GET /students/{id}
```

对应 SQL：

```sql
SELECT * FROM student WHERE id = '903';
```

`id` 是主键，本身就有主键索引，适合做单条详情查询。这个字段不需要重复创建普通索引。

### 10.2 根据 create_user_id 查询当前用户创建的学生

对应接口：

```http
GET /students/my
```

对应 SQL：

```sql
SELECT * FROM student WHERE create_user_id = 1;
```

`create_user_id` 适合建索引，因为 `/students/my` 会按创建人查询。用户数据变多后，如果没有索引，就可能扫描整张学生表。

推荐索引：

```sql
CREATE INDEX idx_student_create_user_id ON student(create_user_id);
```

### 10.3 根据 score 做范围查询

分页条件查询中会按成绩范围筛选：

```sql
SELECT * FROM student WHERE score >= 80 AND score <= 100;
```

`score` 可以用于范围查询索引练习。建立索引后，MySQL 可能使用 `range` 类型扫描指定成绩区间。

推荐练习索引：

```sql
CREATE INDEX idx_student_score ON student(score);
```

需要注意：如果符合条件的数据比例很高，优化器也可能认为全表扫描更划算。

### 10.4 根据 name 模糊查询

分页条件查询中存在姓名模糊查询：

```sql
SELECT * FROM student WHERE name LIKE '%张%';
```

`name LIKE '%xxx%'` 这种前后模糊查询通常不容易使用普通索引。如果业务需要高效搜索姓名关键字，可以进一步了解全文索引、搜索引擎，或者调整成右模糊查询等方案。

## 11. 常见面试题和回答

### 1. 什么是索引？

索引是数据库为了提高查询效率维护的数据结构，类似书的目录。通过索引可以减少扫描行数，但索引会占用存储空间，也会增加写入和更新时的维护成本。

### 2. 为什么索引能提高查询速度？

因为索引可以帮助数据库快速定位数据位置，避免全表扫描。InnoDB 常用 B+ 树索引，查询时可以从根节点逐层缩小范围，最终定位到叶子节点。

### 3. 为什么 MySQL 常用 B+ 树作为索引结构？

B+ 树高度低，能减少磁盘 IO；叶子节点有序并通过链表连接，适合范围查询；所有数据都集中在叶子节点，查询路径比较稳定。

### 4. 主键索引和普通索引有什么区别？

主键索引要求唯一且非空，一张表只能有一个。InnoDB 中主键索引的叶子节点保存整行数据。普通索引可以重复，也可以为 `NULL`，叶子节点通常保存索引字段和主键值，查询完整数据时可能需要回表。

### 5. 什么是回表？

使用普通索引查询时，普通索引叶子节点通常只能找到主键值。如果查询字段不都在这个普通索引里，MySQL 还要根据主键再去主键索引中查询完整行数据，这个过程叫回表。

### 6. 什么是覆盖索引？

如果一条 SQL 查询所需的字段都能从某个索引中直接拿到，不需要再回表查询完整行，这种情况叫覆盖索引。`EXPLAIN` 的 `Extra` 中可能出现 `Using index`。

### 7. 联合索引为什么有最左前缀原则？

联合索引是按照字段顺序建立的。例如 `(create_user_id, score)` 会先按 `create_user_id` 排序，再在相同 `create_user_id` 内按 `score` 排序。所以查询必须从最左字段开始，才能更好利用索引的有序性。

### 8. `LIKE '%张%'` 为什么通常不能使用普通索引？

普通 B+ 树索引依赖从左到右的有序匹配。`'%张%'` 左侧不确定，MySQL 很难定位索引扫描起点，所以通常不能有效使用普通索引。

### 9. `EXPLAIN` 主要看哪些字段？

重点看 `type`、`possible_keys`、`key`、`rows`、`Extra`。其中 `type` 看访问类型，`key` 看实际使用的索引，`rows` 看预计扫描行数，`Extra` 看是否有额外排序、临时表等信息。

### 10. 索引是不是越多越好？

不是。索引会占用磁盘空间，并且插入、更新、删除数据时需要维护索引。索引应该建在高频查询、区分度较高、能明显减少扫描行数的字段上。

### 11. 本项目哪些字段适合建索引？

`id` 是主键，本身已经有主键索引，适合根据学号查询学生详情。`create_user_id` 适合建索引，因为 `/students/my` 会按创建人查询。`score` 可以用于范围查询索引练习。`name LIKE '%xxx%'` 这种前后模糊查询通常不容易使用普通索引。

### 12. 面试总结：结合本项目怎么说明索引优化？

本项目中 `id` 是主键索引，根据 `id` 查询学生详情时可以使用 `PRIMARY`，`type=const`，`rows=1`，效率很高。`create_user_id` 适合建索引，用于查询当前用户创建的学生，例如 `/students/my`，建索引后可以从全表扫描变为普通索引等值查询。`score` 可以用于范围查询索引练习，建索引后范围查询可能使用 `type=range`。`LIKE '%xxx%'` 这种前后模糊查询通常不适合普通 B+ 树索引优化。
