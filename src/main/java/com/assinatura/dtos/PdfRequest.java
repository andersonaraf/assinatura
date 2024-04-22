 package com.assinatura.dtos;
public class PdfRequest {
    private String srcPdfBase64;
    private String certificateBase64;
    private String url;
    private String bearerToken;
    private String nomeAssinante;
    private Integer x, y;
    private Integer width, height;
    private Integer fontSize;

    public String getSrcPdfBase64() {
        return srcPdfBase64;
    }

    public void setSrcPdfBase64(String srcPdfBase64) {
        this.srcPdfBase64 = srcPdfBase64;
    }

    public String getCertificateBase64() {
        return certificateBase64;
    }

    public void setCertificateBase64(String certificateBase64) {
        this.certificateBase64 = certificateBase64;
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

    public String getNomeAssinante() {
        return nomeAssinante;
    }

    public void setNomeAssinante(String nomeAssinante) {
        this.nomeAssinante = nomeAssinante;
    }

    public Integer getX() {
        return (x != null) ? x : 250;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return (y != null) ? y : 65;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return (width != null) ? width : 120;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return (height != null) ? height : 90;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getFontSize() {
        return (fontSize != null) ? fontSize : 12;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }
}
