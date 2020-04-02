package com.regino.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
    学员管理系统(C/S版)——服务器端

    一.业务功能：
        1).接收客户端连接；
        2).开启线程(见ServerThread类)
 */
public class MainApp {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8888)) {
            while (true) {
                System.out.println("等待客户端连接...");
                Socket socket = server.accept();
                //开启线程
                new ServerThread(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}