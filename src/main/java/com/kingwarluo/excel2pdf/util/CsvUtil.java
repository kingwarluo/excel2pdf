package com.kingwarluo.excel2pdf.util;

import com.kingwarluo.excel2pdf.base.CsvReader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @description:读取csv文件工具类
 *
 * @author jianhua.luo
 * @date 2019/8/28
 */
public class CsvUtil {

    public static void main(String[] args) {
        readCsvFile("D:\\IdeaProjects\\8-4-PRO.csv");
    }

    /**
     * csv表头信息
     */
    public static List<String> csvHeadList = new ArrayList<>();

    /**
     * csv数据列 List(Map<头， 数据>)
     */
    public static List<Map<String, Object>> csvDataList = new ArrayList<>();

    /**
     * 读取csv数据
     * @param filePath
     * @return
     * @throws IOException
     */
    public static void readCsvFile(String filePath) {
        clear();
        try {
            CsvReader reader = new CsvReader(filePath, ',', Charset.forName("GBK"));
            int row = 0;

            while (reader.readRecord()) {
                if(row == 0) {
                    readHead(reader.getValues());
                } else {
                    Map<String, Object> dataMap = readData(reader.getValues());
                    csvDataList.add(dataMap);
                }
                row++;
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 读取csv头部信息
     * @param headers
     */
    private static void readHead(String[] headers) {
        csvHeadList = Arrays.asList(headers);
    }

    /**
     * 读取csv内容信息
     * @param dataArr
     */
    private static Map<String, Object> readData(String[] dataArr) {
        Map<String, Object> dataMap = new HashMap<>();
        for (int i = 0; i < dataArr.length; i++) {
            dataMap.put(csvHeadList.get(i), dataArr[i]);
        }
        return dataMap;
    }

    /**
     * 清空信息
     */
    public static void clear() {
        csvHeadList = new ArrayList<>();
        csvDataList = new ArrayList<>();
    }

}
