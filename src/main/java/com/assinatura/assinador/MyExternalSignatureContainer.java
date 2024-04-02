package com.assinatura.assinador;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.IExternalSignatureContainer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;

public class MyExternalSignatureContainer implements IExternalSignatureContainer{
    private byte[] signatureBytes;

    public MyExternalSignatureContainer(String signaturePath) throws IOException {
        // Lê o arquivo de assinatura P7S e o armazena em um array de bytes
        this.signatureBytes = Files.readAllBytes(new File(signaturePath).toPath());
    }

    @Override
    public byte[] sign(InputStream data) throws GeneralSecurityException {
        // Retorna a assinatura pré-gerada
        return signatureBytes;
    }

    @Override
    public void modifySigningDictionary(PdfDictionary signDic) {
        signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
        signDic.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
    }
}
