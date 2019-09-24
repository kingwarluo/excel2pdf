package com.kingwarluo.excel2pdf.util;

public class CommonUtil {

    public static String getRootPath() {
        String rootPath = System.getProperty("user.dir");
        //TODO 打包时，这层要去掉
        rootPath = rootPath.substring(0, rootPath.lastIndexOf("\\"));
        return rootPath;
    }
}
