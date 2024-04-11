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
import java.util.TimeZone;

public class SignPdf {
    private String url;
    private String bearerToken;

    public static String gerar(String srcPdfBase64, String certificateBase64, String url, String bearerToken,
                               String nomeAssinante, Integer x, Integer y, Integer width, Integer height, Integer fontSize) throws Exception {
        Path srcPdfTempPath = Files.createTempFile(null, ".pdf");
        Path signatureTempPath = Files.createTempFile(null, ".p7s");
        Path certificateTempPath = Files.createTempFile(null, ".cer"); // Para o certificado

        //setar as posições das para construir assinatura

        byte[] srcPdfBytes = Base64.getDecoder().decode(srcPdfBase64);
        byte[] certificateBytes = Base64.getDecoder().decode(certificateBase64);

        Files.write(srcPdfTempPath, srcPdfBytes);
        Files.write(certificateTempPath, certificateBytes);

        String destPdfPath = srcPdfTempPath.toString().replace(".pdf", "-signed.pdf");

        signPdf(srcPdfTempPath.toString(), destPdfPath, certificateTempPath.toString(), url,
                bearerToken, nomeAssinante, x, y, width, height, fontSize);

        // Após a assinatura, você pode optar por codificar o PDF assinado de volta para Base64,
        // retornar o caminho do arquivo, ou até mesmo o arquivo diretamente, dependendo do seu requisito
//        System.out.printf(destPdfPath);
        return destPdfPath; // Retorna o caminho do PDF assinado
    }

    private static void signPdf(String srcPdfPath, String destPdfPath, String certificatePath,
                                String url, String bearerToken, String nomeAssinante,
                                Integer x, Integer y, Integer width, Integer height, Integer fontSize) throws Exception {
        PdfReader reader = new PdfReader(srcPdfPath);
        try (FileOutputStream os = new FileOutputStream(destPdfPath)) {
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties().useAppendMode());


            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Rio_Branco"));
            String dataHoraBrasil = sdf.format(new Date());

            // Obtendo o offset do fuso horário de Rio Branco em relação ao UTC
            TimeZone tz = TimeZone.getTimeZone("America/Rio_Branco");
            int offset = tz.getOffset(new Date().getTime()) / 3600000; // Converte de milissegundos para horas

            // Personalizar o texto da camada 2 da aparência da assinatura
            String textoPersonalizado = "Assinado digitalmente por\n" +
                    nomeAssinante + "\n" +
                    "Data: " + dataHoraBrasil + " UTC" + offset + "\n";


            PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                    .setReuseAppearance(false)
                    .setPageRect(new Rectangle(x, y, width, height))
                    .setLayer2FontSize(fontSize)
                    .setPageNumber(1)
                    .setLayer2Text(textoPersonalizado)  // Adicionando o texto personalizado
                    .setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);

            MyExternalSignatureContainer externalSignatureContainer = new MyExternalSignatureContainer(certificatePath, url, bearerToken);
            signer.signExternalContainer(externalSignatureContainer, 8192);
        }
    }
}
