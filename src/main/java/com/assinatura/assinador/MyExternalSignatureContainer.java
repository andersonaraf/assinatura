package com.assinatura.assinador;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignatureContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class MyExternalSignatureContainer implements IExternalSignatureContainer {
    private byte[] signatureBytes;
    private X509Certificate certificate;

    public MyExternalSignatureContainer(String signaturePath, String certificatePath) throws Exception {
        // Carrega a assinatura PKCS#7 (.p7s) como um array de bytes
        this.signatureBytes = Files.readAllBytes(new File(signaturePath).toPath());

        // Carrega o certificado X.509 a partir do caminho fornecido
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(certificatePath)) {
            this.certificate = (X509Certificate) factory.generateCertificate(fis);
        }
    }

    @Override
    public byte[] sign(InputStream data) throws GeneralSecurityException {
        try {
            MessageDigest messageDigest = DigestAlgorithms.getMessageDigest("SHA256", "BC");
            byte[] hash = DigestAlgorithms.digest(data, messageDigest);
            byte[] p7sSignature = signPKCS7API(hash);
            return p7sSignature;
        } catch (IOException | InterruptedException e) {
            throw new GeneralSecurityException("Falha ao assinar o PDF", e);
        }
    }

    private byte[] signPKCS7API(byte[] hash) throws IOException, InterruptedException, GeneralSecurityException {
        // Converta o hash para Base64, pois é o formato comumente esperado pelas APIs
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        // Prepare a chamada da API
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://assinatura-api.staging.iti.br/externo/v2/assinarPKCS7"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer AT-6847-PwKPEF24Tg4ej2Pd8iZwaj-ZUESkBF79")
                .POST(BodyPublishers.ofString("{\"hashBase64\":\"" + hashBase64 + "\"}"))
                .build();

        // Envie a requisição e obtenha a resposta
        HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            // A resposta é o pacote PKCS#7 contendo a assinatura
            // Você pode salvar este byte array como um arquivo .p7s se necessário
            return response.body();
        } else {
            // Trate outros códigos de status e erros conforme necessário
            throw new IOException("Erro na chamada da API: Status " + response.statusCode());
        }
    }

    // Um método auxiliar para extrair a assinatura da resposta da API (depende do formato da sua resposta)
    private String extractSignatureFromResponse(String responseBody) {
        // Implemente a lógica para extrair a assinatura base64 da resposta da API
        return responseBody; // Placeholder
    }

    @Override
    public void modifySigningDictionary(PdfDictionary signDic) {
        // Configura o dicionário de assinatura para indicar o tipo de assinatura
        signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
        signDic.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
    }

    public X509Certificate getCertificate() {
        // Fornece acesso ao certificado carregado
        return this.certificate;
    }
}
