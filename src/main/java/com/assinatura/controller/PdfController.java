package com.assinatura.controller;

import com.assinatura.dtos.PdfRequest;
import com.assinatura.service.PdfService;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/download-signed-pdf")
    public ResponseEntity<Resource> downloadSignedPdf(@RequestBody PdfRequest request) throws Exception {

        String pdfPath = pdfService.generateSignedPdf(request);

        Resource file = new PathResource(pdfPath);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
