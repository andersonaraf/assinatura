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
    public ResponseEntity<Resource> downloadSignedPdf(@RequestBody PdfRequest request) throws IOException {
        String pdfPath = SignPdf.gerar(request.getSrcPdfBase64(), request.getSignatureBase64());

        Resource file = new PathResource(pdfPath);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    // Classe interna para mapear o corpo da requisição JSON
    public static class PdfRequest {
        private String srcPdfBase64;
        private String signatureBase64;

        // Getters e setters
        public String getSrcPdfBase64() { return srcPdfBase64; }
        public void setSrcPdfBase64(String srcPdfBase64) { this.srcPdfBase64 = srcPdfBase64; }

        public String getSignatureBase64() { return signatureBase64; }
        public void setSignatureBase64(String signatureBase64) { this.signatureBase64 = signatureBase64; }
    }
}
