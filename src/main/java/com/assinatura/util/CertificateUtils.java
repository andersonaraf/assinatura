package com.assinatura.util;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.util.Store;

import java.security.cert.X509Certificate;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import java.util.Collection;


public class CertificateUtils {
    public static X509Certificate extractCertificateFromP7s(byte[] p7sData) throws Exception {
        CMSSignedData signedData = new CMSSignedData(p7sData);
        Store<X509CertificateHolder> certStore = signedData.getCertificates();
        Collection<X509CertificateHolder> certCollection = certStore.getMatches(null);

        if (!certCollection.isEmpty()) {
            X509CertificateHolder certHolder = certCollection.iterator().next();
            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
        }
        throw new IllegalArgumentException("No certificate found in the PKCS#7 data.");
    }

    public static String extractCNFromCertificate(X509Certificate certificate) {
        try {
            // O Subject DN do certificado
            String subjectDN = certificate.getSubjectX500Principal().getName();
            LdapName ln = new LdapName(subjectDN);
            for (Rdn rdn : ln.getRdns()) {
                if (rdn.getType().equalsIgnoreCase("CN")) {
                    return rdn.getValue().toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Para simplificação, lidar com erros mais adequadamente conforme necessário
        }
        return null; // Retorna null se não houver CN ou se ocorrer um erro
    }
}
