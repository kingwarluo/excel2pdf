package com.kingwarluo.excel2pdf;

import com.kingwarluo.excel2pdf.util.CsvUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;

public class CenterPanel extends JPanel {

    /**
     * 滚动面板
     */
    JScrollPane jScrollPane;

    /**
     * 表格
     */
    JTable jTable;

    /**
     * 表格头
     */
    public static Object[] head = {"PO_Num", "Discription", "Quantity", "ShipToName", "Weight", "Company",
            "ShipTo Address1", "ShipTo Address2", "ShipTo City", "ShipTo State", "ShipTo Postal Code",
            "ShipTo Day Phone", "Payment_bill_to", "Shipfrom_Code", "BOL_No", "Customer Order Number",
            "Carrier", "NMFC", "Insert Date/Time", "Shipping Code", "Pallet_count", "Class", "Special Insturction", "PRO"};

    /**
     * 表格数据
     */
    Object[][] data = {};

    public CenterPanel(JFrame frame) {
        super(new BorderLayout());
        jScrollPane = new JScrollPane();
        jTable = new JTable();
        DefaultTableModel model = new DefaultTableModel(head, 0);
        jTable.setModel(model);
        //设置不可拖动，表头不可拖拽，
        jTable.getTableHeader().setReorderingAllowed(false);
        //表头，不可在界面重新拖拽大小
        jTable.getTableHeader().setResizingAllowed(false);
        //不自适应宽度，即超出时显示滚动条，很重要
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //设置表头宽度
        for (int i = 0; i < head.length; i++) {
            jTable.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        //设置行高
        jTable.setRowHeight(50);
        //设置滚动条
        jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jTable);
        this.add(jScrollPane);
    }

    /**
     * @description:读取文件并展示到table
     *
     * @author jianhua.luo
     * @date 2019/8/30
     */
    public void readAndShowCsv(String filePath) {
        try {
            data = CsvUtil.readCsvFile(filePath);
            DefaultTableModel model = new DefaultTableModel(data, head);
            jTable.setModel(model);
            //设置表头宽度
            for (int i = 0; i < head.length; i++) {
                jTable.getColumnModel().getColumn(i).setPreferredWidth(200);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
