package com.kingwarluo.excel2pdf;

import javax.swing.*;
import java.awt.*;

/**
 * @description:主要界面
 *
 * @author jianhua.luo
 * @date 2019/8/28
 */
public class MainFrame extends JFrame {

    private TopPanel topPanel;
    private CenterPanel centerPanel;

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
    }

    public MainFrame() {
        //初始化面板
        topPanel = new TopPanel(this);
        centerPanel = new CenterPanel(this);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        // 设置窗体属性
        this.setTitle("网格布局案例");
        //设置窗口大小
        this.setSize(1200, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public TopPanel getTopPanel() {
        return topPanel;
    }

    public CenterPanel getCenterPanel() {
        return centerPanel;
    }

}
