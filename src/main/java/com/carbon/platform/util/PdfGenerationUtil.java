package com.carbon.platform.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

public class PdfGenerationUtil {

    public static byte[] generateCertificatePdf(
            String companyName,
            String certificateId,
            Double co2Offset,
            String secureHash,
            String qrCodeUrl,
            LocalDateTime issuedAt,
            String recipientName,
            String offsetMethod,
            String projectLocation,
            String verificationStandard,
            String transactionHash,
            String issuerName) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.ITALIC);
        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font codeFont = new Font(Font.FontFamily.COURIER, 10, Font.NORMAL);

        // Header
        document.add(new Paragraph("CARBON OFFSET CERTIFICATE", titleFont));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("This is to certify that", subTitleFont));
        document.add(new Paragraph(recipientName, titleFont));
        document.add(new Paragraph("has successfully retired", bodyFont));
        document.add(new Paragraph(co2Offset + " Tonnes of CO2 Equivalents", titleFont));
        document.add(new Paragraph("Issued on: " + issuedAt.toLocalDate().toString(), bodyFont));
        document.add(new Paragraph("on the Carbon Credits Marketplace Platform.", bodyFont));
        document.add(new Paragraph("\n"));
        // Details table (simple paragraphs for brevity)
        document.add(new Paragraph("Certificate ID: " + certificateId, bodyFont));
        document.add(new Paragraph("Recipient Name: " + recipientName, bodyFont));
        document.add(new Paragraph("Offset Method: " + offsetMethod, bodyFont));
        document.add(new Paragraph("Project Location: " + projectLocation, bodyFont));
        document.add(new Paragraph("Verification Standard: " + verificationStandard, bodyFont));
        document.add(new Paragraph("Transaction Hash: " + transactionHash, codeFont));
        document.add(new Paragraph("Issuer: " + issuerName, bodyFont));
        document.add(new Paragraph("Verification Hash: " + secureHash, codeFont));
        document.add(new Paragraph("\n"));
        // QR code image
        byte[] qrCodeBytes = QrCodeUtil.generateQrCodeImage(qrCodeUrl, 150, 150);
        Image qrCodeImage = Image.getInstance(qrCodeBytes);
        document.add(qrCodeImage);
        // Signature area
        document.add(new Paragraph("\n\nSignature: ______________________", bodyFont));
        document.add(new Paragraph("Authorized Officer", bodyFont));
        document.close();
        return out.toByteArray();
    }
}
