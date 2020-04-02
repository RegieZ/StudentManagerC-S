package com.regino.server;

import com.regino.pojo.Student;
import com.regino.server.StudentDAO;
import org.junit.Test;

public class TestStudentDAO {
    @Test
    public void add() {
        System.out.println(StudentDAO.addStudent(new Student(0, "测试1", "测试2", 10)));
        System.out.println(StudentDAO.addStudent(new Student(1, "测试1", "测试2", 10)));
    }

    @Test
    public void findAll() {
        System.out.println(StudentDAO.findAll());
    }

    @Test
    public void findByID() {
        System.out.println(StudentDAO.findById(0));
        System.out.println(StudentDAO.findById(2));
        System.out.println(StudentDAO.findById(1));
    }

    @Test
    public void updateStudent() {
        System.out.println(StudentDAO.updateStudent(new Student(0, "修改1", "修改2", 10)));
    }

    @Test
    public void delete(){
        System.out.println(StudentDAO.deleteById(0));
        System.out.println(StudentDAO.deleteById(1));
    }
}
