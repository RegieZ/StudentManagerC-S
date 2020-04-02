package com.regino.server;

import com.regino.pojo.Student;

import java.io.*;
import java.util.ArrayList;

public class StudentDAO {
    // 相对路径从src开始
    private static File destPath = new File("Student_Server\\src\\com\\regino\\student.txt");
    private static File tempPath = new File("Student_Server\\src\\com\\regino\\temp.txt");

    public static void rewrite() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(tempPath));
        BufferedWriter bw = new BufferedWriter(new FileWriter(destPath));

        // 进行文件读取
        String row = null;
        while ((row = br.readLine()) != null) {
            bw.write(row);
            bw.flush();
            bw.newLine();
        }

        // 释放资源
        bw.close();
        br.close();

        // 删除temp文件
        tempPath.delete();
    }

    public static boolean deleteById(int id) {
        try {
            // 判断文本文件是否为空
            if (new FileInputStream(destPath).available() == 0) {
                return false;
            }

            // 修改后写入temp.txt
            BufferedReader br = new BufferedReader(new FileReader(destPath));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempPath));

            // 重新排id
            int newId = -2;

            // 进行文件读取
            String row = null;
            while ((row = br.readLine()) != null) {
                newId++;
                String[] rowArray = row.split("\t");

                // 跳过首行
                if (rowArray[0].equals("ID")) {
                    bw.write(row);
                    bw.flush();
                    bw.newLine();
                    continue;
                }

                // 跳过匹配ID
                if (id == Integer.valueOf(rowArray[0])) {
                    newId --;
                    continue;
                }

                // 覆盖其他的行
                bw.write(newId + "\t" + rowArray[1] + "\t" + rowArray[2] + "\t" + rowArray[3]);
                bw.flush();
                bw.newLine();
            }

            // 释放资源
            bw.close();
            br.close();

            // 从temp.txt写回来
            rewrite();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ArrayList<Student> findAll() {
        ArrayList<Student> arr = new ArrayList<Student>();
        try {
            // 判断文本文件是否为空
            if (new FileInputStream(destPath).available() == 0) {
                return arr;
            }

            BufferedReader br = new BufferedReader(new FileReader(destPath));

            // 进行文件读取
            String row = null;
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\t");
                String id = rowArray[0];
                String name = rowArray[1];
                String sex = rowArray[2];
                String age = rowArray[3];

                // 跳过首行
                if (id.equals("ID")) {
                    continue;
                }
                arr.add(new Student(Integer.valueOf(id), name, sex, Integer.valueOf(age)));
            }
            // 释放资源
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public static Student findById(int id) {
        Student s = new Student();
        try {
            // 判断文本文件是否为空
            if (new FileInputStream(destPath).available() == 0) {
                return s;
            }

            BufferedReader br = new BufferedReader(new FileReader(destPath));

            // 进行文件读取
            String row = null;
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\t");

                // 跳过首行
                if (rowArray[0].equals("ID")) {
                    continue;
                }

                // 匹配ID
                if (id == Integer.valueOf(rowArray[0])) {
                    s.setId(Integer.valueOf(rowArray[0]));
                    s.setName(rowArray[1]);
                    s.setSex(rowArray[2]);
                    s.setAge(Integer.valueOf(rowArray[3]));
                }
            }

            // 释放资源
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static boolean updateStudent(Student stu) {
        try {
            // 判断文本文件是否为空
            if (new FileInputStream(destPath).available() == 0) {
                return false;
            }

            // 修改后写入temp.txt
            BufferedReader br = new BufferedReader(new FileReader(destPath));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempPath));

            // 进行文件读取
            String row = null;
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\t");

                // 跳过首行
                if (rowArray[0].equals("ID")) {
                    bw.write(row);
                    bw.flush();
                    bw.newLine();
                    continue;
                }

                // 替换ID匹配的行
                if (stu.getId() == Integer.valueOf(rowArray[0])) {
                    bw.write(stu.getId() + "\t" +
                            stu.getName() + "\t" +
                            stu.getSex() + "\t" +
                            stu.getAge());
                    bw.flush();
                    bw.newLine();
                    continue;
                }

                // 直接覆盖其他的行
                bw.write(row);
                bw.flush();
                bw.newLine();
            }

            // 释放资源
            bw.close();
            br.close();

            // 从temp.txt写回来
            rewrite();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean addStudent(Student stu) {
        try {
            // 获取集合中的每一个Student对象
            BufferedWriter bw = new BufferedWriter(new FileWriter(destPath, true));

            // 判断文本文件是否为空
            if (new FileInputStream(destPath).available() == 0) {
                bw.write("ID\t");
                bw.write("学生姓名\t");
                bw.write("学生性别\t");
                bw.write("学生年龄\t");
                bw.newLine();
                bw.flush();
            }

            // 找到相应的id
            stu.setId(findAll().size());

            // 把Student信息存储到文本文件中
            int id = stu.getId();
            String name = stu.getName();
            String sex = stu.getSex();
            int age = stu.getAge();

            bw.write(id + "\t");
            bw.write(name + "\t");
            bw.write(sex + "\t");
            bw.write(age + "\t");
            bw.newLine();
            bw.flush();

            //释放资源
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}