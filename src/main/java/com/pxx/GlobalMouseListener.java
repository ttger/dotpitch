package com.pxx;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WinEventProc;

import javax.swing.*;
import java.awt.*;

public class GlobalMouseListener extends JFrame {

    // 定义事件常量
    private static final int EVENT_SYSTEM_CAPTURE_START = 0x0009; // 鼠标捕获开始
    private static final int EVENT_SYSTEM_CAPTURE_END = 0x000A;   // 鼠标捕获结束

    // 定义钩子标志常量
    private static final int WIN_EVENT_OUT_OF_CONTEXT = 0x0000; // 全局钩子

    private static JLabel label;

    public static void main(String[] args) {
        // 使用事件调度线程创建并显示窗口
        SwingUtilities.invokeLater(() -> {
            GlobalMouseListener frame = new GlobalMouseListener();
            frame.setVisible(true); // 显示窗口
            frame.setResizable(false);
            frame.setAlwaysOnTop(true);
        });

        //鼠标监听
        startListener();
    }

    private static void startListener() {
        // 获取 User32 实例
        User32 user32 = User32.INSTANCE;

        // 设置全局事件钩子
        WinNT.HANDLE hWinEventHook = user32.SetWinEventHook(
                EVENT_SYSTEM_CAPTURE_START, // 事件最小值：鼠标捕获开始
                EVENT_SYSTEM_CAPTURE_END,   // 事件最大值：鼠标捕获结束
                Kernel32.INSTANCE.GetModuleHandle(null), // 当前模块句柄
                new WinEventProcCallback(), // 回调函数
                0, // 进程ID（0 表示所有进程）
                0, // 线程ID（0 表示所有线程）
                WIN_EVENT_OUT_OF_CONTEXT // 钩子标志
        );

        if (hWinEventHook == null) {
            System.err.println("无法设置事件钩子");
            return;
        }

        System.out.println("事件钩子已设置，开始监听鼠标点击...");

        // 消息循环
        WinUser.MSG msg = new WinUser.MSG();
        while (user32.GetMessage(msg, null, 0, 0) != 0) {
            user32.TranslateMessage(msg);
            user32.DispatchMessage(msg);
        }

        // 卸载钩子
        user32.UnhookWinEvent(hWinEventHook);
    }

    // 事件回调函数
    private static class WinEventProcCallback implements WinEventProc {
        public static int px = 0;
        public static int py = 0;

        @Override
        public void callback(WinNT.HANDLE hWinEventHook, WinDef.DWORD event, WinDef.HWND hwnd,
                             WinDef.LONG idObject, WinDef.LONG idChild, WinDef.DWORD dwEventThread, WinDef.DWORD dwordEventTime) {

            try {
                // 获取鼠标位置
                WinDef.POINT point = new WinDef.POINT();
                User32.INSTANCE.GetCursorPos(point);
                int px1 = point.x;
                int py1 = point.y;
                System.out.println("鼠标坐标: (" + px1 + ", " + py1 + ")");

                //计算坐标距离
                double c = Math.sqrt(((px1 - px) * (px1 - px)) + ((py1 - py) * (py1 - py))) * 100 / 290;
                label.setText(String.valueOf(((int)c)));
                System.out.println("距离：" + (int) c);

                px = px1;
                py = py1;
            } catch (Exception e) {
                System.out.println("计算错误：" + e.getMessage());
            }
        }
    }


    // 构造函数
    public GlobalMouseListener() {
        // 设置窗口标题
        setTitle("我的窗口");

        // 设置窗口大小
        setSize(150, 60);

        // 窗口位置
        setLocation(1800, 10);

        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建并添加组件
        initComponents();
    }

    // 初始化组件
    private void initComponents() {
        // 创建一个标签，用于显示字符串
        label = new JLabel("点距计算", SwingConstants.CENTER);

        // 设置标签字体
        label.setFont(new Font("Serif", Font.BOLD, 18));

        // 将标签添加到窗口的内容面板
        add(label);
    }

}