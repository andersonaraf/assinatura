package com.assinatura.service;

import com.assinatura.assinador.SignPdf;
import com.assinatura.dtos.PdfRequest;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

    public String generateSignedPdf(PdfRequest request) throws Exception {
        return SignPdf.gerar(request);
    }
}
