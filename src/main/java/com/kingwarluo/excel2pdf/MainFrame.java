package com.kingwarluo.excel2pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.kingwarluo.excel2pdf.util.CommonUtil;
import com.kingwarluo.excel2pdf.util.CsvUtil;
import com.kingwarluo.excel2pdf.util.PdfUtil;
import com.kingwarluo.excel2pdf.util.ExcelUtil;
import org.apache.commons.lang3.StringUtils;

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

/**
 * @description:主要界面
 *
 * @author jianhua.luo
 * @date 2019/8/28
 */
public class MainFrame extends JFrame {

    /**
     * 导入和生成pdf按钮
     */
    JButton jbImport, jbGenerate;

    /**
     * 打开文件对话框
     */
    FileDialog openFileDialog;

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
    }

    public MainFrame() {
        Box b1 = Box.createHorizontalBox();    //创建横向Box容器
        this.add(b1);    //将外层横向Box添加进窗体
        b1.add(Box.createVerticalStrut(200));    //添加高度为200的垂直框架
        jbImport = new JButton("选择文件");
        b1.add(jbImport);    //添加按钮选择文件
        b1.add(Box.createHorizontalStrut(40));    //添加长度为40的水平框架
        jbGenerate = new JButton("生成pdf");
        b1.add(jbGenerate);    //添加按钮生成pdf
        b1.add(Box.createHorizontalGlue());    //添加水平胶水

        // 设置窗体属性
        this.setTitle("csv转pdf工具");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100,100,400,200);
        this.setVisible(true);

        //读取配置文件到内存
        readExcel();
        //设置按钮事件
        addEvents(this);
    }

    /**
     * @description:读取配置文件
     *
     * @author jianhua.luo
     * @date 2019/9/24
     */
    private void readExcel() {
        try {
            ExcelUtil.readChargesToBill();
            ExcelUtil.readRelation();
            ExcelUtil.readSCAC();
            ExcelUtil.readShipFrom();

            System.out.println(ExcelUtil.chargesToBillMap);
            System.out.println(ExcelUtil.relationMap);
            System.out.println(ExcelUtil.scacMap);
            System.out.println(ExcelUtil.shipFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description:给按钮添加事件
     *
     * @author jianhua.luo
     * @date 2019/8/28
     */
    private void addEvents(MainFrame frame) {
        jbImport.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openFileDialog = new FileDialog(frame, "选择文件", FileDialog.LOAD);
                openFileDialog.setVisible(true);
                String dirName = openFileDialog.getDirectory();
                String fileName = openFileDialog.getFile();
                if(StringUtils.isBlank(dirName) || StringUtils.isBlank(fileName)) {
                    return;
                }
                String filePath = dirName + fileName;
                System.out.println(filePath);
                String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
                System.out.println(suffix.equals("csv"));
                if(!suffix.equals("csv")){
                    JOptionPane.showMessageDialog(frame, "请选择Excel文件~",
                            "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                frame.readAndShowCsv(filePath);
            }
        });

        jbGenerate.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String targetPath = "D:/"+new SimpleDateFormat("yyMMddHHmmss").format(new Date()).toString() +"zsResult.pdf";
                try {
                    InputStream fis = new FileInputStream(CommonUtil.getRootPath() + "/VICSBOL.pdf");
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


    /**
     * @description:读取文件并展示到table
     *
     * @author jianhua.luo
     * @date 2019/8/30
     */
    public void readAndShowCsv(String filePath) {
        try {
            CsvUtil.readCsvFile(filePath);
//            DefaultTableModel model = new DefaultTableModel(data, head);
//            jTable.setModel(model);
//            //设置表头宽度
//            for (int i = 0; i < head.length; i++) {
//                jTable.getColumnModel().getColumn(i).setPreferredWidth(200);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
