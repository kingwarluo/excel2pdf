package com.kingwarluo.excel2pdf;

import com.itextpdf.text.DocumentException;
import com.kingwarluo.excel2pdf.common.Constants;
import com.kingwarluo.excel2pdf.util.CommonUtil;
import com.kingwarluo.excel2pdf.util.CsvUtil;
import com.kingwarluo.excel2pdf.util.PdfUtil;
import com.kingwarluo.excel2pdf.util.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.JOptionPane;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static String FILE_PREFIX_CSV = "csv";

    /**
     * 需要生成条形码的字段
     */
    private static String needCodeBar = "boln,pro,p3bol,p3po,p3co,p3pro";

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
        System.out.println("资源加载完成");
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

            @Override
            public void actionPerformed(ActionEvent e) {
                openFileDialog = new FileDialog(frame, "选择文件", FileDialog.LOAD);
                openFileDialog.setVisible(true);
                String dirName = openFileDialog.getDirectory();
                String fileName = openFileDialog.getFile();
                if(StringUtils.isBlank(dirName) || StringUtils.isBlank(fileName)) {
                    return;
                }
                String filePath = dirName + fileName;
                String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
                if(!suffix.equals(FILE_PREFIX_CSV)){
                    JOptionPane.showMessageDialog(frame, "请选择Excel文件~",
                            "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                //读取填充数据信息
                CsvUtil.readCsvFile(filePath);
            }
        });

        jbGenerate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //1、读取要生成的数据列表
                List<Map<String, Object>> csvDataList = CsvUtil.csvDataList;
                if(csvDataList == null || csvDataList.size() == 0){
                    JOptionPane.showMessageDialog(frame, "请选择Excel文件~",
                            "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                File file = new File(CommonUtil.getRootPath() + "/config/VICSBOL.pdf");
                generatePDF(csvDataList, file);
                JOptionPane.showMessageDialog(frame, "生成成功！",
                        "信息", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        });
    }

    /**
     * 填充pdf
     * @param csvDataList   选择的文件数据集合
     * @param file           pdf模板文件
     */
    public void generatePDF(List<Map<String, Object>> csvDataList, File file) {
        //1、遍历csv数据列表，根据配置关系表填入pdf模板
        for (Map<String, Object> dataMap : csvDataList) {
            //1、读取relation配置文件
            List<Map<String, String>> relationList = null;
            try {
                relationList = ExcelUtil.ensureRelationListNotNull();
                //新建一个Map,保存当前pdf文件所指向的配置文件记录，key文件名， value文件对应的数据
                Map<String, Map<String, String>> configRecordMap = new HashMap<>(3);
                PdfUtil pdf = new PdfUtil(new FileInputStream(file));

                String shipFromCode = "";
                String bolNo = "";
                for (Map<String, String> relationMap : relationList) {
                    String key = relationMap.get(Constants.RELATION_FIELD_NAME);
                    if("pkgstot".equals(key) || "wgttot".equals(key)) {
                        fillTotalData(pdf, key, dataMap);
                    } else {
                        String csvData = getCsvData(relationMap.get(Constants.RELATION_CSV_HEADER), dataMap);
                        if(Constants.SHIP_FROM_CODE.equals(relationMap.get(Constants.RELATION_CSV_HEADER))) {
                            shipFromCode = csvData;
                        }
                        if(Constants.BOL_NO.equals(relationMap.get(Constants.RELATION_CSV_HEADER))) {
                            bolNo = csvData;
                        }
                        fillPDFField(relationMap, pdf, configRecordMap, csvData);
                    }
                }
                byte[] bytes = pdf.getTemplateBytes();
                String savePath = CommonUtil.savePdfPath(shipFromCode, bolNo);
                System.out.println("保存路径为：" + savePath);
                pdf.mergeMultiToOnePdf(savePath, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 需要计算的值
     * @param pdf
     * @param key
     * @param dataMap
     */
    private void fillTotalData(PdfUtil pdf, String key, Map<String, Object> dataMap) {
        String head = "";
        if("pkgstot".equals(key)) {
            head = "pkgs";
        } else if("wgttot".equals(key)) {
            head = "wgt";
        }
        Integer result = new Integer(0);
        for (int i = 1; i <= 8; i++) {
            Map<String, String> relationMap = ExcelUtil.relationMap.get(head + i);
            if(relationMap == null || relationMap.size() == 0) {
                continue;
            }
            String relationCsvHead = relationMap.get(Constants.RELATION_CSV_HEADER);
            if(StringUtils.isBlank(relationCsvHead)) {
                continue;
            }
            String csvData = (String) dataMap.get(relationCsvHead);
            if(StringUtils.isNotBlank(csvData)) {
                Integer value = Integer.parseInt(csvData);
                result += value;
            }
        }
        pdf.setTextValue(key, String.valueOf(result));
    }

    /**
     * 根据relation配置文件的csv header字段，获取数据
     * 注：csv header用‘,’隔开时，需获取多个值，用空格隔开
     * @param csvHeaders
     * @param dataMap
     * @return
     */
    public String getCsvData(String csvHeaders, Map<String, Object> dataMap) {
        StringBuilder headerDataSB = new StringBuilder();
        if(StringUtils.isNotBlank(csvHeaders)) {
            String[] csvHeaderArr = csvHeaders.split(",");
            for (int i = 0; i < csvHeaderArr.length; i++) {
                String csvData = (String) dataMap.get(csvHeaderArr[i]);
                if(StringUtils.isNotBlank(csvData)) {
                    if(headerDataSB.length() == 0) {
                        headerDataSB.append(csvData);
                    } else {
                        headerDataSB.append(" ").append(csvData);
                    }
                }
            }
        }
        return headerDataSB.toString();
    }

    /**
     * 填充每一个字段属性
     *
     * @param relationMap
     * @param pdf
     * @param configRecordMap
     * @param csvData
     *
     * 2、读取csv header，看是否有值
     *    2.1 csv header无值，根据default value，default value有值就写入pdf模板，无值就不写
     *       2.1.1 deafult value为字段默认值，有些需要特殊处理的值，today:默认当前日期
     *       2.1.2 default value无值，查看relation filename是否有值
     *          2.1.2.1 relation filename无值，则不填写该字段
     *          2.1.2.2 relation filename有值，则从relation filename文件读取relation field字段，读取值填入pdf模板
     *    2.2 csv header有值，从csv文件中读取值
     *       2.2.1 若csv header从csv文件读取到值，则查看relation filename是否有值，
     *          2.2.1.1 若relation filename有值，读取relation filename对应文件的relation field字段，读取该行数据作为relation filename文件的数据
     *          2.2.1.2 若relation filename无值，则向pdf模板写入从csv文件读取的值
     *       2.2.2 若csv header从csv文件没读取到值，则查看default value是否有值，default value有值就写入pdf模板，无值就不写
     */
    public void fillPDFField(Map<String, String> relationMap, PdfUtil pdf, Map<String, Map<String, String>> configRecordMap, String csvData) {
        String key = relationMap.get(Constants.RELATION_FIELD_NAME);
        String fieldType = relationMap.get(Constants.RELATION_FIELD_PROPERTY);
        String csvHeader = relationMap.get(Constants.RELATION_CSV_HEADER);
        if(StringUtils.isBlank(csvHeader)) {
            String defaultValue = relationMap.get(Constants.RELATION_DEFAULT_VALUE);
            if(StringUtils.isNotBlank(defaultValue)) {
                setDefaultValue(defaultValue, key, pdf);
            } else {
                String value = readValueFromConfig(relationMap.get(Constants.RELATION_RELATION_FILENAME), relationMap.get(Constants.RELATION_RELATION_FIELD), csvData, configRecordMap);
                if(StringUtils.isNotBlank(value)) {
                    pdf.setTextValue(key, value);
                }
            }
        } else {
            if(StringUtils.isNotBlank(csvData)) {
                if(StringUtils.isNotBlank(relationMap.get(Constants.RELATION_RELATION_FILENAME))) {
                    String value = readValueFromConfig(relationMap.get(Constants.RELATION_RELATION_FILENAME), relationMap.get(Constants.RELATION_RELATION_FIELD), csvData, configRecordMap);
                    if(StringUtils.isNotBlank(value)) {
                        pdf.setTextValue(key, value);
                    }
                } else {
                    pdf.setTextValueWithType(key, csvData, fieldType);
                    if(needCodeBar.contains(key)) {
                        //如果csv有值，生成条形码
                        generateCodeBar(pdf, key, csvData);
                    }
                }
            } else {
                String defaultValue = relationMap.get(Constants.RELATION_DEFAULT_VALUE);
                setDefaultValue(defaultValue, key, pdf);
            }
        }
    }

    /**
     * 生成条形码
     * @param pdf
     * @param key
     * @param csvData
     */
    private void generateCodeBar(PdfUtil pdf, String key, String csvData) {
        if("boln".equals(key)) {
            pdf.setBarCodeImg(csvData, CommonUtil.NUM_128, 360, 670, 35);
        } else if("pro".equals(key)) {
            pdf.setBarCodeImg(csvData, CommonUtil.NUM_39, 360, 570, 35);
        } else if ("p3bol".equals(key)){
            pdf.setBarCodeImg(csvData, CommonUtil.NUM_128, 360, 570, 35);
        } else if("p3po".equals(key)) {
            pdf.setBarCodeImg(csvData, CommonUtil.NUM_128, 360, 670, 35);
        } else if("p3co".equals(key)) {
            pdf.setBarCodeImg(csvData, CommonUtil.NUM_128, 360, 770, 35);
        } else if("p3pro".equals(key)) {
            pdf.setBarCodeImg(csvData, CommonUtil.NUM_128, 360, 870, 35);
        }
    }

    /**
     * 设置默认值
     * @param defaultValue
     * @param key
     * @param pdf
     */
    public void setDefaultValue(String defaultValue, String key, PdfUtil pdf) {
        if(StringUtils.isBlank(defaultValue)) {
            return;
        }
        if(defaultValue.equals(Constants.RELATION_DEFAULT_VALUE_TODAY)) {
            pdf.setTextValue(key, new SimpleDateFormat("MM/dd/yyyy").format(new Date()).toString());
        } else {
            pdf.setTextValue(key, defaultValue);
        }
    }

    /**
     * 从配置文件读取值
     *
     * @param filename      配置文件名称
     * @param fieldname     配置文件头head的值
     * @param configRecordMap
     */
    public String readValueFromConfig(String filename, String fieldname, String csvData, Map<String, Map<String, String>> configRecordMap) {
        if(StringUtils.isBlank(filename)) {
            return null;
        }
        Map<String, String> configDataMap = configRecordMap.get(filename);
        if(configDataMap == null || configDataMap.size() == 0) {
            configDataMap = ExcelUtil.getDataMap(filename, csvData);
            putConfigRecordMap(configRecordMap, filename, configDataMap);
        }
        return configDataMap.get(fieldname);
    }

    /**
     * 保存某个pdf对应配置文件名的具体某条数据
     * @param configRecordMap
     * @param filename
     * @param recordMap
     */
    public void putConfigRecordMap(Map<String, Map<String, String>> configRecordMap, String filename, Map<String, String> recordMap) {
        if(!configRecordMap.containsKey(filename)) {
            configRecordMap.put(filename, recordMap);
        }
    }

}
