package com.kingwarluo.excel2pdf;

import com.kingwarluo.excel2pdf.util.CsvUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class CenterPanel extends JPanel {

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
        jTable = new JTable();
        DefaultTableModel model = new DefaultTableModel(head, 0);
        jTable.setModel(model);
        this.add(jTable.getTableHeader(), BorderLayout.NORTH);
        this.add(jTable, BorderLayout.CENTER);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
