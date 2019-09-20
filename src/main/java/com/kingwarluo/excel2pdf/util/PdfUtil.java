package com.kingwarluo.excel2pdf.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * @description:操作pdf工具类
 *
 * @author jianhua.luo
 * @date 2019/8/30
 */
public class PdfUtil {

    PdfReader reader;

    private PdfStamper ps;

    private ByteArrayOutputStream bos = new ByteArrayOutputStream();

    private AcroFields fields;

    public PdfUtil(InputStream stream) throws IOException, DocumentException {
        try {
            reader = new PdfReader(stream);//读取
            int totalPage = reader.getNumberOfPages();
            System.out.println("totalPage:" + totalPage);
            ps = new PdfStamper(reader, bos);
            fields = ps.getAcroFields();
        } catch(Exception e) {
            System.out.println("加载pdf模板失败");
            e.printStackTrace();
        }
    }

    /**
     * 遮挡location层
     */
    public void shelterSecion() {
        PdfContentByte canvas = ps.getOverContent(1);
        canvas.saveState();
        canvas.setColorFill(BaseColor.WHITE);
        canvas.rectangle(240, 640, 84, 14);
        canvas.fill();
        canvas.restoreState();
    }

    public void setFieldValue() throws IOException, DocumentException {
        fields.setField("stname", "adfasdfasdfasdfasdfadfadfsdfasdfsdfaadfasdfasdfasdfasdfadfadfsdfasdfsdfasdfasdfasdfasdfasdfsdfasdfasdfasdfasdf");
    }

    /**
     * 添加条形码
     * @throws IOException
     * @throws DocumentException
     */
    public void setBarCodeImg() throws IOException, DocumentException {
        BufferedImage bufferedImage = BarCodeUtils.getBarCode("12345679");
        ByteArrayOutputStream bao= new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", bao);
        Image img = Image.getInstance(bao.toByteArray());
        img.setAlignment(1);                         //居中显示
        img.setAbsolutePosition(330,  680);//显示位置，根据需要调整
        img.scalePercent(60);                          //显示为原条形码图片大小的比例，百分比
        PdfContentByte canvas = ps.getOverContent(1);
        canvas.addImage(img);
    }

    public void createNewPage(String target, java.util.List<PdfReader> readerList) throws DocumentException, IOException {
        String finalPath = "D:/"+new SimpleDateFormat("yyMMddHHmmss").format(new Date()).toString() +"zsResult.pdf";
        int totalPages = 0;
        for (PdfReader reader : readerList) {
            totalPages += reader.getNumberOfPages();
        }

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(finalPath));
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

    public byte[] newPage() throws IOException, DocumentException {
        Document document = new Document();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        document.open();
        document.newPage();
        BaseFont bfChinese = BaseFont.createFont("c://windows//fonts//BSSYM7.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font FontChinese18 = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
        Paragraph blankRow1 = new Paragraph(24f," ",FontChinese18);
        document.add(blankRow1);
        document.add(new Paragraph("1231231312"));
        document.close();
        return bos.toByteArray();
    }

    /**
     * 保存pdf到指定位置
     * @param target
     */
    public byte[] savePdfFileToTargetPath(String target) {
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
}
