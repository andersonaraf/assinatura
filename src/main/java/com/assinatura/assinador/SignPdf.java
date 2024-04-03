package com.assinatura.assinador;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Base64;

public class SignPdf {

    public static String gerar(String srcPdfBase64, String signatureBase64, String certificateBase64) throws Exception {
        Path srcPdfTempPath = Files.createTempFile(null, ".pdf");
        Path signatureTempPath = Files.createTempFile(null, ".p7s");
        Path certificateTempPath = Files.createTempFile(null, ".cer"); // Para o certificado

        byte[] srcPdfBytes = Base64.getDecoder().decode(srcPdfBase64);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        byte[] certificateBytes = Base64.getDecoder().decode(certificateBase64);

        Files.write(srcPdfTempPath, srcPdfBytes);
        Files.write(signatureTempPath, signatureBytes);
        Files.write(certificateTempPath, certificateBytes);

        String destPdfPath = srcPdfTempPath.toString().replace(".pdf", "-signed.pdf");

        signPdf(srcPdfTempPath.toString(), destPdfPath, signatureTempPath.toString(), certificateTempPath.toString());

        // Após a assinatura, você pode optar por codificar o PDF assinado de volta para Base64,
        // retornar o caminho do arquivo, ou até mesmo o arquivo diretamente, dependendo do seu requisito
        System.out.printf(destPdfPath);
        return destPdfPath; // Retorna o caminho do PDF assinado
    }

    private static void signPdf(String srcPdfPath, String destPdfPath, String signaturePath, String certificatePath) throws Exception {
        PdfReader reader = new PdfReader(srcPdfPath);
        try (FileOutputStream os = new FileOutputStream(destPdfPath)) {
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties().useAppendMode());

            PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                    .setReason("Anderson Araújo Ferreira")
                    .setLocation("Local")
                    .setReuseAppearance(false)
                    .setPageRect(new Rectangle(100, 100, 200, 100))
                    .setPageNumber(1);
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);

            MyExternalSignatureContainer externalSignatureContainer = new MyExternalSignatureContainer(signaturePath, certificatePath);
            signer.signExternalContainer(externalSignatureContainer, 8192);
        }
    }
}
