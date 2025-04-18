package com.pxx;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
//        System.out.println("Hello world!");
        mousePos();
    }

    public static void mousePos(){
        try {
            Robot robot = new Robot();
            while (true) {
                Point mouseLocation = MouseInfo.getPointerInfo().getLocation(); // 获取当前鼠标位置
                System.out.println("Mouse is at: " + mouseLocation.x + ", " + mouseLocation.y);

                robot.delay(1000); // 每秒检查一次位置
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}