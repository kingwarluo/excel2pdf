package com.kingwarluo.excel2pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.kingwarluo.excel2pdf.util.PdfUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    /**
     * 打开文件对话框
     */
    FileDialog openFileDialog;

    public TopPanel(MainFrame frame) {
        this.frame = frame;
        //初始化按钮
        jbImport = new JButton("选择文件");
        jbGenerate = new JButton("生成PDF");
        this.add(jbImport);
        this.add(jbGenerate);

        addEvents();
    }

    /**
     * @description:给按钮添加事件
     *
     * @author jianhua.luo
     * @date 2019/8/28
     */
    private void addEvents() {
        jbImport.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openFileDialog = new FileDialog(frame, "选择文件", FileDialog.LOAD);
                openFileDialog.setVisible(true);
                String dirName = openFileDialog.getDirectory();
                String fileName = openFileDialog.getFile();
                String filePath = dirName + fileName;
                System.out.println(filePath);
                String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
                System.out.println(suffix.equals("csv"));
                if(!suffix.equals("csv")){
                    JOptionPane.showMessageDialog(frame, "请选择Excel文件~",
                            "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                frame.getCenterPanel().readAndShowCsv(filePath);
            }
        });

        jbGenerate.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String targetPath = "D:/"+new SimpleDateFormat("yyMMddHHmmss").format(new Date()).toString() +"zsResult.pdf";
                try {
                    InputStream fis = this.getClass().getClassLoader().getResourceAsStream("VICSBOL.pdf");
                    PdfUtil pdf = new PdfUtil(fis);
                    pdf.shelterSecion();
                    pdf.setBarCodeImg();
                    pdf.setFieldValue();
                    byte[] bytes = pdf.savePdfFileToTargetPath(targetPath);
                    byte[] bytes2 = pdf.newPage();
                    PdfReader reader = new PdfReader(bytes);
                    PdfReader reader2 = new PdfReader(bytes2);
                    java.util.List<PdfReader> readerList = new ArrayList<>();
                    readerList.add(reader);
                    readerList.add(reader2);
                    pdf.createNewPage(targetPath, readerList);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
