package com.kingwarluo.excel2pdf.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:公共配置项
 *
 * @author jianhua.luo
 * @date 2019/10/12
 */
public class CommonUtil {

    /**
     * 数字 39
     */
    public static int NUM_39 = 39;

    /**
     * 数字 128
     */
    public static int NUM_128 = 128;

    /**
     * 获取根路径，不包含项目名
     * @return
     */
    public static String getRootPath() {
        String rootPath = System.getProperty("user.dir");
        //TODO 打包时，这层要去掉
        rootPath = rootPath.substring(0, rootPath.lastIndexOf("\\"));
        return rootPath;
    }

    /**
     * 保存pdf路径
     * @param shipFromCode
     * @param bolNo
     * @return
     */
    public static String savePdfPath(String shipFromCode, String bolNo) {
        StringBuilder sb = new StringBuilder(getRootPath());
        sb.append("/pdf/")
                .append(shipFromCode)
                .append("_")
                .append(new SimpleDateFormat("yyMMddHHmmss").format(new Date()).toString())
                .append("_")
                .append(bolNo)
                .append(".pdf");
        return sb.toString();
    }
}
