package com.assinatura.assinador;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Base64;

public class SignPdf {
    public static void signPdf(String srcPdfPath, String destPdfPath, String signaturePath) {
        try {
            PdfReader reader = new PdfReader(srcPdfPath);
            PdfSigner signer = new PdfSigner(reader, new FileOutputStream(destPdfPath), new StampingProperties().useAppendMode());

            PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                    .setPageRect(new Rectangle(280, 65, 80, 80))
                    .setPageNumber(1);
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);

            MyExternalSignatureContainer externalSignatureContainer = new MyExternalSignatureContainer(signaturePath);

            // Adiciona a assinatura ao documento
            signer.signExternalContainer(externalSignatureContainer, 8192);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String gerar(String srcPdfBase64, String signatureBase64) throws IOException {
        // Decodifica o PDF de origem e a assinatura do Base64 e os salva como arquivos temporários
        Path srcPdfTempPath = Files.createTempFile(null, ".pdf");
        Path signatureTempPath = Files.createTempFile(null, ".p7s");

        byte[] srcPdfBytes = Base64.getDecoder().decode(srcPdfBase64);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

        Files.write(srcPdfTempPath, srcPdfBytes);
        Files.write(signatureTempPath, signatureBytes);

        // Gera um nome de arquivo aleatório para o PDF de destino
        Path destPdfTempPath = Files.createTempFile(null, ".pdf");

        // Chama signPdf com os caminhos dos arquivos
        signPdf(srcPdfTempPath.toString(), destPdfTempPath.toString(), signatureTempPath.toString());

        // Codifica o PDF assinado de volta para Base64 para retornar
        byte[] destPdfBytes = Files.readAllBytes(destPdfTempPath);
        String destPdfBase64 = Base64.getEncoder().encodeToString(destPdfBytes);

//        // Limpa os arquivos temporários
        Files.delete(srcPdfTempPath);
        Files.delete(signatureTempPath);

        return destPdfTempPath.toString();
    }
}
