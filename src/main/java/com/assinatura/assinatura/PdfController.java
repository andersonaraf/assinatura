package com.assinatura.assinatura;

import com.assinatura.assinador.SignPdf;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;

@RestController
public class PdfController {

    @PostMapping("/download-signed-pdf")
    public ResponseEntity<Resource> downloadSignedPdf(@RequestBody PdfRequest request) throws Exception {
//        System.out.println("Nome: " + request.getNomeAssinante());
        String pdfPath = SignPdf.gerar(request.getSrcPdfBase64(),  request.getCertificateBase64(), request.getUrl(), request.getBearerToken(), request.getNomeAssinante());

        Resource file = new PathResource(pdfPath);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    // Classe interna para mapear o corpo da requisição JSON
    public static class PdfRequest {
        private String srcPdfBase64;
        private String certificateBase64; // Novo campo para o certificado
        private String url;
        private String bearerToken;

        private String nomeAssinante;

        // Getters e setters
        public String getSrcPdfBase64() {
            return srcPdfBase64;
        }

        public void setSrcPdfBase64(String srcPdfBase64) {
            this.srcPdfBase64 = srcPdfBase64;
        }

        public String getCertificateBase64() {
            return certificateBase64;
        } // Getter para o certificado

        public void setCertificateBase64(String certificateBase64) {
            this.certificateBase64 = certificateBase64;
        } // Setter para o certificado

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

        public String getNomeAssinante() {
            return nomeAssinante;
        }

        public void setNomeAssinante(String nomeAssinante) {
            this.nomeAssinante = nomeAssinante;
        }
    }
}
