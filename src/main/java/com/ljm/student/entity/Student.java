package com.ljm.student.entity;

public class Student {
    private String id;
    private String name;
    private int age;
    private int score;

    public Student(String id, String name, int age, int score) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "学号：" + id + "，姓名：" + name + "，年龄：" + age + "，成绩：" + score;
    }
}