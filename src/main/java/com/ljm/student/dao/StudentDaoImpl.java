package com.ljm.student.dao;
import com.ljm.student.util.DBUtil;
import com.ljm.student.entity.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class StudentDaoImpl implements StudentDao {

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        int age = rs.getInt("age");
        int score = rs.getInt("score");

        return new Student(id, name, age, score);
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();

        String sql = "select id, name, age, score from student";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Student student = mapRowToStudent(rs);
                list.add(student);
            }
        } catch (SQLException e) {
            System.out.println("查询所有学生失败");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public Student findById(String id) {
        String sql = "select id, name, age, score from student where id = ? ";
        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("根据学号查询学生失败");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean addStudent(Student student) {
        String sql = "insert into student(id, name, age, score) values(?, ?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, student.getId());
            ps.setString(2, student.getName());
            ps.setInt(3, student.getAge());
            ps.setInt(4, student.getScore());

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("数据库添加失败");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteStudent(String id) {
        String sql = "delete from student where id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, id);

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("数据库删除失败");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateStudent(Student student) {
        String sql = "update student set name = ?, age = ?, score = ? where id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, student.getName());
            ps.setInt(2, student.getAge());
            ps.setInt(3, student.getScore());
            ps.setString(4, student.getId());

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("数据库修改失败");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Student> findByPage(int page, int pageSize) {
        List<Student>list =new ArrayList<>();
        String sql="select id,name ,age ,score from student limit ?,?";
        int offset=(page-1)*pageSize;
        try(
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
                ps.setInt(1,offset);
                ps.setInt(2,pageSize);

                try(ResultSet rs = ps.executeQuery()){
                    while (rs.next()) {
                        Student student = mapRowToStudent(rs);
                        list.add(student);
                    }
                }
        }catch (SQLException e){
            System.out.println("分页查询失败");
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Student> findByNameLike(String name) {
        List<Student> list = new ArrayList<>();
        String sql = "select id,name,age,score from student where name like ?";
        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student student = mapRowToStudent(rs);
                    list.add(student);
                }
            }
        } catch (SQLException e) {
            System.out.println("模糊查找失败");
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Student> findAllOrderByScoreDesc() {
        List<Student>list =new ArrayList<>();
        String sql="select id,name,age,score from student order by score desc";
        try(Connection conn = DBUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        )
        {
            while (rs.next()) {
                Student student = mapRowToStudent(rs);
                list.add(student);
            }
        }catch (SQLException e){
            System.out.println("排序失败");
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countStudent() {
        String sql="select count(*) total from student";
        try(Connection conn = DBUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ){
            if(rs.next()){
                return rs.getInt("total");
            }
        }catch (SQLException e){
            System.out.println("查询总人数失败");
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public double avgScore() {
        String sql = "select avg(score) as avg_score from student";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                double avg = rs.getDouble("avg_score");
                if (rs.wasNull()) {
                    return 0; // 没有学生时，平均分记为0
                }
                return avg;
            }
        } catch (SQLException e) {
            System.out.println("统计平均分失败");
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int maxScore() {
        String sql = "select max(score) as max_score from student";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                int max = rs.getInt("max_score");
                if (rs.wasNull()) {
                    return 0; // 没有学生时记为0
                }
                return max;
            }
        } catch (SQLException e) {
            System.out.println("统计最高分失败");
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int minScore() {
        String sql = "select min(score) as min_score from student";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                int min = rs.getInt("min_score");
                if (rs.wasNull()) {
                    return 0; // 没有学生时记为0
                }
                return min;
            }
        } catch (SQLException e) {
            System.out.println("统计最低分失败");
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int countPassStudent() {
        String sql = "select count(*) as pass_count from student where score >= 60";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("pass_count");
            }
        } catch (SQLException e) {
            System.out.println("统计及格人数失败");
            e.printStackTrace();
        }

        return -1;
    }
}