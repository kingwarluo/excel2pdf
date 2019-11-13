package com.kingwarluo.excel2pdf.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author jianhua.luo
 * @description:操作pdf工具类
 * @date 2019/8/30
 */
public class PdfUtil {

    PdfReader reader;

    private PdfStamper ps;

    private ByteArrayOutputStream bos = new ByteArrayOutputStream();

    private AcroFields fields;

    private BaseFont baseFont;

    private BaseFont boldFont;

    public PdfUtil(InputStream stream) {
        try {
            reader = new PdfReader(stream);
            ps = new PdfStamper(reader, bos);
            fields = ps.getAcroFields();
            baseFont = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            boldFont = BaseFont.createFont("Helvetica-Bold", "Cp1252", BaseFont.NOT_EMBEDDED);
//            fields.setExtraMargin(10f, 0f);
        } catch (Exception e) {
            System.out.println("加载pdf模板失败");
            e.printStackTrace();
        }
    }

    /**
     * 设置文本框值
     * @param name
     * @param value
     * @throws IOException
     * @throws DocumentException
     */
    public void setTextValue(String name, String value) {
        try {
            fields.setFieldProperty(name, "textsize", 8.5f, null);
            if("terms1".equals(name) || "terms2".equals(name) || "see#0".equals(name) || "property".equals(name)) {
                return;
            }
            if("pageof".equals(name)) {
                value = "1";
            }
            fields.setField(name, value);
        } catch (IOException e) {
        } catch (DocumentException e) {
        }
    }

    /**
     * 根据字段类别设置字段值
     * @param name
     * @param value
     * @param fieldType
     */
    public void setTextValueWithType(String name, String value, String fieldType) {
        if(fieldType.equals("boolean")) {
            if("true".equalsIgnoreCase(value)) {
                try {
                    fields.setFieldProperty(name, "textfont", baseFont, null);
                    fields.setField(name, "√");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            setTextValue(name, value);
        }
    }

    /**
     * 添加条形码
     * @param vaNumber
     * @param format
     * @param x
     * @param y
     * @param scalePercent
     * @throws IOException
     * @throws DocumentException
     */
    public void setBarCodeImg(String vaNumber, int format, int x, int y, int scalePercent) {
        try {
            BufferedImage bufferedImage = null;
            if (format == CommonUtil.NUM_39) {
                bufferedImage = BarCodeUtils.getBarCode39(vaNumber);
            } else {
                bufferedImage = BarCodeUtils.getBarCode128(vaNumber);
            }
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", bao);
            Image img = Image.getInstance(bao.toByteArray());
            //居中显示
            img.setAlignment(1);
            //显示位置，根据需要调整
            img.setAbsolutePosition(x, y);
            //显示为原条形码图片大小的比例，百分比
            img.scalePercent(scalePercent);
            PdfContentByte canvas = ps.getOverContent(1);
            canvas.addImage(img);
        } catch (Exception e) {

        }
    }

    /**
     * 填充模板第一页
     *
     */
    public byte[] getTemplateBytes() {
        try {
            /* 必须要调用这个，否则文档不会生成的 */
            ps.setFormFlattening(true);
            ps.close();
            return bos.toByteArray();
        } catch (Exception e) {
            System.out.println("生成流失败");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将多个pdf合并成一个
     *
     * @param savePdfPath
     * @param bytesArr
     * @throws DocumentException
     * @throws IOException
     */
    public void mergeMultiToOnePdf(String savePdfPath, byte[]... bytesArr) throws DocumentException, IOException {
        List<PdfReader> readerList = new ArrayList<PdfReader>();
        for (int i = 0; i < bytesArr.length; i++) {
            PdfReader reader = new PdfReader(bytesArr[i]);
            readerList.add(reader);
        }
        int totalPages = 0;
        for (PdfReader reader : readerList) {
            totalPages += reader.getNumberOfPages();
        }

        //确保文件路径存在
        File file = new File(savePdfPath);
        File fileParent = file.getParentFile();
        if(!fileParent.exists()){
            fileParent.mkdirs();
        }

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(savePdfPath));
        document.open();
        PdfContentByte cb = writer.getDirectContent();

        int pageOfCurrentReaderPDF = 0;
        Iterator<PdfReader> iteratorPDFReader = readerList.iterator();

        // Loop through the PDF files and add to the output.
        while (iteratorPDFReader.hasNext()) {
            PdfReader pdfReader = iteratorPDFReader.next();

            // Create a new page in the target for each source page.
            while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                document.newPage();//创建新的一页
                pageOfCurrentReaderPDF++;
                PdfImportedPage page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
                cb.addTemplate(page, 0, 0);
            }
            pageOfCurrentReaderPDF = 0;
        }
        document.close();
        writer.close();
    }
}
