package com.carbon.platform.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import java.io.ByteArrayOutputStream;

public class PdfGenerationUtil {

    public static byte[] generateCertificatePdf(String companyName, String certificateId, Double co2Offset, String secureHash, String qrCodeUrl) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.ITALIC);
        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font codeFont = new Font(Font.FontFamily.COURIER, 10, Font.NORMAL);

        document.add(new Paragraph("CARBON OFFSET CERTIFICATE", titleFont));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("This is to certify that", subTitleFont));
        document.add(new Paragraph(companyName, titleFont));
        document.add(new Paragraph("has successfully retired", bodyFont));
        document.add(new Paragraph(co2Offset + " Tonnes of CO2 Equivalents", titleFont));
        document.add(new Paragraph("on the Carbon Credits Marketplace Platform.", bodyFont));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Certificate ID: " + certificateId, bodyFont));
        document.add(new Paragraph("Verification Hash: " + secureHash, codeFont));
        document.add(new Paragraph("\n"));

        // Generate and embed QR code
        byte[] qrCodeBytes = QrCodeUtil.generateQrCodeImage(qrCodeUrl, 150, 150);
        Image qrCodeImage = Image.getInstance(qrCodeBytes);
        document.add(qrCodeImage);

        document.close();
        return out.toByteArray();
    }
}
