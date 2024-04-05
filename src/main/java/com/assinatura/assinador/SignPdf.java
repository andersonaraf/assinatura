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

import java.text.SimpleDateFormat;
import java.util.Date;

public class SignPdf {
    private String url;
    private String bearerToken;

    public static String gerar(String srcPdfBase64, String certificateBase64, String url, String bearerToken, String nomeAssinante) throws Exception {
        Path srcPdfTempPath = Files.createTempFile(null, ".pdf");
        Path signatureTempPath = Files.createTempFile(null, ".p7s");
        Path certificateTempPath = Files.createTempFile(null, ".cer"); // Para o certificado

        byte[] srcPdfBytes = Base64.getDecoder().decode(srcPdfBase64);
        byte[] certificateBytes = Base64.getDecoder().decode(certificateBase64);

        Files.write(srcPdfTempPath, srcPdfBytes);
        Files.write(certificateTempPath, certificateBytes);

        String destPdfPath = srcPdfTempPath.toString().replace(".pdf", "-signed.pdf");

        signPdf(srcPdfTempPath.toString(), destPdfPath, certificateTempPath.toString(), url, bearerToken, nomeAssinante);

        // Após a assinatura, você pode optar por codificar o PDF assinado de volta para Base64,
        // retornar o caminho do arquivo, ou até mesmo o arquivo diretamente, dependendo do seu requisito
//        System.out.printf(destPdfPath);
        return destPdfPath; // Retorna o caminho do PDF assinado
    }

    private static void signPdf(String srcPdfPath, String destPdfPath, String certificatePath, String url, String bearerToken, String nomeAssinante) throws Exception {
        PdfReader reader = new PdfReader(srcPdfPath);
        try (FileOutputStream os = new FileOutputStream(destPdfPath)) {
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties().useAppendMode());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dataHoraBrasil = sdf.format(new Date());

            // Personalizar o texto da camada 2 da aparência da assinatura
            String textoPersonalizado = "Assinado digitalmente por\n" +
                    nomeAssinante + "\n" +
                    "Data: " + dataHoraBrasil + "\n";

            PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                    .setReuseAppearance(false)
                    .setPageRect(new Rectangle(280, 65, 160, 80))
                    .setPageNumber(1)
                    .setLayer2Text(textoPersonalizado)  // Adicionando o texto personalizado
                    .setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);

            MyExternalSignatureContainer externalSignatureContainer = new MyExternalSignatureContainer(certificatePath, url, bearerToken);
            signer.signExternalContainer(externalSignatureContainer, 8192);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }
}
