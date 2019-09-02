package com.kingwarluo.excel2pdf.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * @description:读取excel文件工具类
 *
 * @author jianhua.luo
 * @date 2019/8/28
 */
public class CsvUtil {

    private static final int headLength = 23;

    public static Object[][] readCsvFile(String filePath) throws IOException {
        Reader reader = new FileReader(filePath);
        CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
        List<CSVRecord> records = parser.getRecords();

        Object[][] recordList = new Object[records.size()][];
        int row = 0;
        for (CSVRecord csvRecord : records) {
            Object[] data = new Object[23];
            for (int i = 0; i < headLength; i++) {
                data[i] = csvRecord.get(i);
            }
            recordList[row++] = data;
        }
        reader.close();
        return recordList;
    }


}
