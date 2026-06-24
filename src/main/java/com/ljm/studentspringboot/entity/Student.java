package com.ljm.studentspringboot.entity;

public class Student {

    private String id;
    private String name;
    private Integer age;
    private Integer score;

    public Student() {
    }

    public Student(String id, String name, Integer age, Integer score) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}