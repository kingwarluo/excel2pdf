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

    public static void main(String[] args) {
        try {
            readChargesToBill();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Map<String, Map<String, String>> scacMap = null;
    public static Map<String, Map<String, String>> shipFromMap = null;

    public static final String chargesToBillFile = "/ChargesBillTo.xlsx";
    public static final List<String> chargesToBillHeadList = new ArrayList<>();
    /**
     * chargesToBill文件对应Map
     */
    public static Map<String, Map<String, String>> chargesToBillMap = null;
    public static void readChargesToBill() throws IOException {
        chargesToBillMap = readExcelFile(chargesToBillFile, chargesToBillHeadList, 0);
    }

    public static final String relationFile = "/relation.xlsx";
    /**
     * 头部列表
     */
    public static final List<String> relationHeadList = new ArrayList<>();
    /**
     * 键值对，<key是pdf模板的fieldname, value是Map<头, 值>>
     */
    public static Map<String, Map<String, String>> relationMap = null;
    public static void readRelation() throws IOException {
        relationMap = readExcelFile(relationFile, relationHeadList, 1);
    }
    public static Map<String, Map<String, String>> ensureRelationMapNotNull() throws IOException {
        if(relationMap == null || relationMap.isEmpty()) {
            readRelation();
        }
        return relationMap;
    }

    public static final String scacFile = "/SCAC.xlsx";
    public static final List<String> scacHeadList = new ArrayList<>();
    public static void readSCAC() throws IOException {
        scacMap = readExcelFile(scacFile, scacHeadList, 0);
    }

    public static final String shipFromFile = "/Ship From.xlsx";
    public static final List<String> shipFromHeadList = new ArrayList<>();
    public static void readShipFrom() throws IOException {
        shipFromMap = readExcelFile(shipFromFile, shipFromHeadList, 0);
    }

    public static Map<String, Map<String, String>> readExcelFile(String file, List<String> columns, int startRow) throws IOException {
        InputStream fis = new FileInputStream(CommonUtil.getRootPath() + file);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);

        Map<String, Map<String, String>> resultMap = new HashMap<>();
        int rowSize = sheet.getLastRowNum() + 1;
        System.out.println(rowSize);
        for (int i = startRow; i < rowSize; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if (i == 0) {
                readHeader(row, columns);
            } else {
                Map<String, String> rowMap = new HashMap();
                ////行中有多少个单元格，也就是有多少列
                int cellSize = columns.size();
                for (int j = 0; j < cellSize; j++) {
                    Cell cell = row.getCell(j);
                    String key = columns.get(j);
                    String value = null;
                    if (cell != null) {
                        value = cell.toString();
                    }
                    rowMap.put(key, value);
                }
                if(StringUtils.isNotBlank(rowMap.get(columns.get(0)))) {
                    resultMap.put(rowMap.get(columns.get(0)), rowMap);
                }
            }
        }
        return resultMap;
    }

    /**
     * 读取第一行，作为配置表表头
     * @param row
     * @param columns
     */
    public static void readHeader(Row row, List<String> columns) {
        int columnNum = row.getLastCellNum();
        for (int i = 0; i < columnNum; i++) {
            Cell cell = row.getCell(i);
            if(cell == null) {
                break;
            }
            columns.add(cell.toString());
        }
    }

    /**
     * 根据文件名获取表头列表
     * @param filename
     * @return
     */
    public static List<String> getHeadList(String filename) {
        List<String> configHeadList = null;
        if(chargesToBillFile.equals(filename)) {
            configHeadList = chargesToBillHeadList;
        } else if(shipFromFile.equals(filename)) {
            configHeadList = shipFromHeadList;
        } else if(scacFile.equals(filename)) {
            configHeadList = scacHeadList;
        }
        return configHeadList;
    }

    /**
     * 根据文件名获取数据Map
     * @param filename
     * @return
     */
    public static Map<String, String> getDataMap(String filename, String fieldname) {
        Map<String, Map<String, String>> configFileDataMap = null;
        if(chargesToBillFile.equals(filename)) {
            configFileDataMap = chargesToBillMap;
        } else if(shipFromFile.equals(filename)) {
            configFileDataMap = shipFromMap;
        } else if(scacFile.equals(filename)) {
            configFileDataMap = scacMap;
        }
        Map<String, String> configDataMap = configFileDataMap.get(fieldname);
        return configDataMap;
    }

}
