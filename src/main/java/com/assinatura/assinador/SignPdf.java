package com.assinatura.assinador;

import com.assinatura.dtos.PdfRequest;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SignPdf {
    static MyExternalSignatureContainer externalSignatureContainer;

    public static String gerar(PdfRequest request) throws Exception {
        Path srcPdfTempPath = Files.createTempFile(null, ".pdf");
        Path certificateTempPath = Files.createTempFile(null, ".cer");

        try {
            Files.write(srcPdfTempPath, Base64.getDecoder().decode(request.getSrcPdfBase64()));
            Files.write(certificateTempPath, Base64.getDecoder().decode(request.getCertificateBase64()));

            String destPdfPath = srcPdfTempPath.toString().replace(".pdf", "-signed.pdf");

            externalSignatureContainer = new MyExternalSignatureContainer(certificateTempPath.toString(), request.getUrl(), request.getBearerToken());

            signPdf(srcPdfTempPath.toString(), destPdfPath, certificateTempPath.toString(), request);

            return destPdfPath;
        } finally {
            Files.deleteIfExists(srcPdfTempPath);
            Files.deleteIfExists(certificateTempPath);
        }
    }

    private static void signPdf(String srcPdfPath, String destPdfPath, String certificatePath, PdfRequest request) throws Exception {
        PdfReader reader = new PdfReader(srcPdfPath);
        try (FileOutputStream os = new FileOutputStream(destPdfPath)) {
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties().useAppendMode());
            // Obter CN do certificado
            String signerName = externalSignatureContainer.getCertificateCommonName();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Rio_Branco"));
            String dateTime = sdf.format(new Date());

            TimeZone tz = TimeZone.getTimeZone("America/Rio_Branco");
            int offset = tz.getOffset(new Date().getTime()) / 3600000;

            String customText = String.format("Assinado digitalmente por\n%s\nData: %s UTC%s\n",
                    signerName, dateTime, offset);

            Rectangle rect = new Rectangle(request.getX(), request.getY(), request.getWidth(), request.getHeight());

            PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                    .setReuseAppearance(false)
                    .setPageRect(rect)
                    .setLayer2FontSize(request.getFontSize().floatValue())
                    .setPageNumber(1)
                    .setLayer2Text(customText)
                    .setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);

            MyExternalSignatureContainer externalSignatureContainer = new MyExternalSignatureContainer(certificatePath, request.getUrl(), request.getBearerToken());

            signer.signExternalContainer(externalSignatureContainer, 8192);
        }
    }
}
