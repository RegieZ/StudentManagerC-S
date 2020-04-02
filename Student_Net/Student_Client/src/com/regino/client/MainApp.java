package com.regino.client;

import com.regino.pojo.Student;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/*
    学员管理系统(C/S版)——客户端

    一. 业务功能：
        1.添加学员
        2.修改学员
        3.删除学员
        4.查询学员

    二. 代码流程说明：
        1.本客户端与服务器端实现"短连接"——每个功能当需要与服务器连接时，才建立连接，功能完毕，连接立即断开；
 */
public class MainApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("***************************************************");
            System.out.println("*\t1.添加学员\t2.修改学员\t3.删除学员\t4.查询学员\t5.退出\t*");
            System.out.println("***************************************************");
            int op = sc.nextInt();
            switch (op) {
                case 1://添加
                    addStudent(sc);
                    break;
                case 2://修改
                    updateStudent(sc);
                    break;
                case 3://删除
                    deleteStudent(sc);
                    break;
                case 4://查询
                    findStudent(sc);
                    break;
                case 5://退出
                    System.out.println("谢谢使用，再见！");
                    System.exit(0);
            }

        }
    }

    //1.添加学员
    private static void addStudent(Scanner sc) {
        //1.接收用户数据
        System.out.println("请输入学员信息：");
        System.out.println("姓名：");
        String name = sc.next();
        System.out.println("性別：");
        String sex = sc.next();
        System.out.println("年龄：");
        int age = sc.nextInt();


        //2.获取连接后的输出流
        Socket socket = getSocket();
        if (socket == null) {
            System.out.println("【错误】无法连接服务器！");
            return;
        }
        //3.创建输出流
        try (OutputStream netOut = socket.getOutputStream();
             InputStream netIn = socket.getInputStream();
        ) {
            //发送数据
            netOut.write(("[1]" + name + "," + sex + "," + age).getBytes());

            //接收反馈
            int b = netIn.read();
            //4.关闭连接
            socket.close();
            //判断反馈
            if (b == 0) {
                //5.完毕
                System.out.println("【成功】数据已保存！");
            } else {
                System.out.println("【失败】数据保存失败，请重试！");
            }
            return;
        } catch (IOException e) {
            System.out.println("【错误】保存失败，请重试！");
            return;
        }
    }

    //2.修改学员
    private static void updateStudent(Scanner sc) {
        //1.接收id
        System.out.println("请输入要修改的学员ID：");
        int id = sc.nextInt();

        //2.获取连接
        Socket socket = getSocket();
        //3.发送"查询"请求
        try {
            OutputStream netOut = socket.getOutputStream();
            InputStream netIn = socket.getInputStream();
            //标记："2"根据ID查询一条记录
            netOut.write(("[2]" + id).getBytes());
            //接收结果
            ObjectInputStream objIn = new ObjectInputStream(netIn);
            Object obj = objIn.readObject();
            objIn.close();

            if (obj == null) {
                System.out.println("【失败】无查询结果！");
                return;
            }
            if (!(obj instanceof Student)) {
                System.out.println("【失败】返回数据错误，请重试！");
                return;
            }
            //关闭此次连接
            socket.close();

            //向下转型
            Student stu = (Student) obj;
            System.out.println("【查询结果】");
            printStudent(stu);//打印

            //接收新数据
            System.out.println("请输入新姓名(保留原值请输入0)：");
            String newName = sc.next();
            System.out.println("请输入新性别(保留原值请输入0)：");
            String newSex = sc.next();
            System.out.println("请输入新年龄(保留原值请输入0)：");
            int newAge = sc.nextInt();

            if (!"0".equals(newName)) {
                stu.setName(newName);
            }
            if (!"0".equals(newSex)) {
                stu.setSex(newSex);
            }
            if (newAge != 0) {
                stu.setAge(newAge);
            }
            //再次连接
            socket = getSocket();
            //发送修改数据，格式：[3]....
            netOut = socket.getOutputStream();
            netOut.write(("[3]" + stu.getId() + "," +
                    stu.getName() + "," +
                    stu.getSex() + "," +
                    stu.getAge()).getBytes());
            //接收反馈
            netIn = socket.getInputStream();
            int b = netIn.read();
            if (b == 0) {
                System.out.println("【成功】数据已修改！");
            } else {
                System.out.println("【失败】数据修改失败，请重试！");
            }
            return;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    //3.删除学员
    private static void deleteStudent(Scanner sc) {
        System.out.println("请输入要删除的学员ID：");
        int id = sc.nextInt();
        //2.获取连接
        Socket socket = getSocket();
        //3.发送"查询"请求
        try {
            OutputStream netOut = socket.getOutputStream();
            InputStream netIn = socket.getInputStream();
            //标记："2"根据ID查询一条记录
            netOut.write(("[2]" + id).getBytes());

            ObjectInputStream objIn = new ObjectInputStream(netIn);
            //接收结果
            Object obj = objIn.readObject();
            if (obj == null) {
                System.out.println("【失败】无查询结果！");
                return;
            }
            if (!(obj instanceof Student)) {
                System.out.println("【失败】返回数据错误，请重试！");
                return;
            }
            //向下转型
            Student stu = (Student) obj;
            System.out.println("【查询结果】");
            printStudent(stu);//打印

            //关闭连接
            socket.close();

            //确认删除
            System.out.println("【确认】你确定删除这条记录吗？(y/n)：");
            String op = sc.next();
            if (!"y".equals(op)) {
                System.out.println("【取消】操作被取消！");
                return;
            }

            //再次连接
            socket = getSocket();
            //发送删除数据，格式：[5]id值....
            netOut = socket.getOutputStream();
            netOut.write(("[5]" + stu.getId()).getBytes());
            //接收反馈
            netIn = socket.getInputStream();
            int b = netIn.read();
            if (b == 0) {
                System.out.println("【成功】数据已删除！");
            } else {
                System.out.println("【失败】数据删除失败，请重试！");
            }
            return;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //4.查询学员
    private static void findStudent(Scanner sc) {
        //
        //1.获取连接
        Socket socket = getSocket();

        try {
            OutputStream netOut = socket.getOutputStream();
            //2.发送请求，格式：[4]
            netOut.write(("[4]").getBytes());

            ObjectInputStream objIn = new ObjectInputStream(
                    socket.getInputStream());
            //3.接收结果，一个序列化的ArrayList<Student>
            Object o = objIn.readObject();
            if (o == null) {
                System.out.println("【失败】查询失败，请重试！");
                return;
            }
            if (!(o instanceof ArrayList)) {
                System.out.println("【错误】返回数据错误，请重试！");
                return;
            }

            System.out.println("【查询结果】");
            ArrayList<Student> list = (ArrayList<Student>) o;
            printStudentList(list);
            //关闭连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //连接服务器
    private static Socket getSocket() {
        String ip = "127.0.0.1";
        int port = 8888;

        try {
            Socket socket = new Socket(ip, port);
            return socket;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //打印ArrayList<Student>的方法
    public static void printStudentList(ArrayList<Student> stuList) {
        System.out.println("--------------------------------------------------");
        System.out.println("编号\t\t姓名\t\t\t性别\t\t年龄");
        for (int i = 0; i < stuList.size(); i++) {
            Student p = stuList.get(i);
            System.out.println(p.getId() + "\t\t" +
                    p.getName() + "\t\t\t" +
                    p.getSex() + "\t\t" +
                    p.getAge());

        }
        System.out.println("--------------------------------------------------");
    }

    //打印Person的方法
    public static void printStudent(Student stu) {
        System.out.println("--------------------------------------------------");
        System.out.println("编号\t\t姓名\t\t性别\t\t\t年龄");
        System.out.println(stu.getId() + "\t\t" +
                stu.getName() + "\t\t\t" +
                stu.getSex() + "\t\t" +
                stu.getAge());
        System.out.println("--------------------------------------------------");
    }
}