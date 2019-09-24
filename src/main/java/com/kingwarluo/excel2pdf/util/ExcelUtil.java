package com.kingwarluo.excel2pdf.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @description:excel操作类
 *
 * @author jianhua.luo
 * @date 2019/9/24
 */
public class ExcelUtil {

    public static Map<String, Map<String, String>> chargesToBillMap = null;
    public static Map<String, Map<String, String>> relationMap = null;
    public static Map<String, Map<String, String>> scacMap = null;
    public static Map<String, Map<String, String>> shipFromMap = null;

    private static final String chargesToBillFile = "/ChargesBillTo.xlsx";
    private static final String[] chargesToBillArray = {"Code", "Name", "Address", "City", "State", "Zip"};
    public static void readChargesToBill() throws IOException {
        chargesToBillMap = readExcelFile(chargesToBillFile, chargesToBillArray, "Code");
    }

    private static final String relationFile = "/relation.xlsx";
    private static final String[] relationArray = {"csv key", "pdf key"};
    public static void readRelation() throws IOException {
        relationMap = readExcelFile(relationFile, relationArray, "csv key");
    }

    private static final String scacFile = "/SCAC.xlsx";
    private static final String[] scacArray = {"SCAC", "Carrier Name"};
    public static void readSCAC() throws IOException {
        scacMap = readExcelFile(scacFile, scacArray, "SCAC");
    }

    private static final String shipFromFile = "/Ship From.xlsx";
    private static final String[] shipFromArray = {"Supplier Code", "Shipfrom_Code", "Warehouse", "Name", "Address", "City", "State", "Zip", "Contact", "Phone_No", "NMFC", "Emails", "Note"};
    public static void readShipFrom() throws IOException {
        shipFromMap = readExcelFile(shipFromFile, shipFromArray, "Shipfrom_Code");
    }

    private static Map<String, Map<String, String>> readExcelFile(String file, String[] columns, String keyColumn) throws IOException {
        InputStream fis = new FileInputStream(CommonUtil.getRootPath() + file);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);

        Map<String, Map<String, String>> resultMap = new HashMap<>();
        int rowSize = sheet.getLastRowNum() + 1;
        System.out.println(rowSize);
        for (int i = 1; i < rowSize; i++) {//遍历行，从1开始，略过第一行（0）
            Row row = sheet.getRow(i);
            if (row == null) {//略过空行
                continue;
            }
            Map<String, String> rowMap = new HashMap();
            int cellSize = columns.length;//行中有多少个单元格，也就是有多少列
            for (int j = 0; j < cellSize; j++) {
                Cell cell = row.getCell(j);
                String key = columns[j];
                String value = null;
                if (cell != null) {
                    value = cell.toString();
                }
                rowMap.put(key, value);
            }
            if(StringUtils.isNotBlank(rowMap.get(keyColumn))) {
                resultMap.put(rowMap.get(keyColumn), rowMap);
            }
        }
        return resultMap;
    }
}
