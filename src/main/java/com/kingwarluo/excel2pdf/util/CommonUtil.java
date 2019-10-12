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

    public static String SAVE_PDF_PATH = "D:/" + new SimpleDateFormat("yyMMddHHmmss").format(new Date()).toString() + "zsResult.pdf";

    public static String getRootPath() {
        String rootPath = System.getProperty("user.dir");
        //TODO 打包时，这层要去掉
        rootPath = rootPath.substring(0, rootPath.lastIndexOf("\\"));
        return rootPath;
    }
}
