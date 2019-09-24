package com.kingwarluo.excel2pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.kingwarluo.excel2pdf.util.PdfUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TopPanel extends JPanel {

    /**
     * 主窗体
     */
    MainFrame frame;

    /**
     * 导入和生成pdf按钮
     */
    JButton jbImport, jbGenerate;

    public TopPanel(MainFrame frame) {
        this.frame = frame;
//        this.setLayout(new BorderLayout());
        Dimension preferredSize = new Dimension(400,100);
        //初始化按钮
        jbImport = new JButton("选择文件");
        jbImport.setPreferredSize(preferredSize);
        jbImport.setHorizontalAlignment(SwingConstants.CENTER);
        jbImport.setVerticalAlignment(SwingConstants.CENTER);
        jbGenerate = new JButton("生成PDF");
        jbGenerate.setPreferredSize(preferredSize);
        jbGenerate.setHorizontalAlignment(SwingConstants.CENTER);
        jbGenerate.setVerticalAlignment(SwingConstants.CENTER);
        this.add(jbImport);
        this.add(jbGenerate);

    }



}
