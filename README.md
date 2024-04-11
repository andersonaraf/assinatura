# Módulo de Assinatura Eletrônica para Spring Boot

Este módulo permite a incorporação de assinaturas eletrônicas em documentos PDF, utilizando certificados digitais. É perfeito para aplicações que requerem assinaturas digitalmente verificáveis, como contratos, acordos e outros documentos legais.

## Funcionalidades

- Assinatura de documentos PDF usando certificados digitais.
- Personalização da aparência da assinatura no documento.
- Suporte a múltiplas assinaturas em um único documento.

## Requisitos

- JDK 1.8 ou superior.
- Spring Boot 3.2.4.

## Configuração

Para usar este módulo, inclua as seguintes informações no seu documento ou aplicação:

- `srcPdfBase64`: String base64 do documento PDF a ser assinado.
- `certificateBase64`: String base64 do certificado digital usado para assinar o documento.
- `url`: URL do serviço de assinatura.
- `nomeAssinante`: Nome do assinante a ser exibido no documento.

### Informações de Layout da Assinatura

Para personalizar a aparência da assinatura no documento, configure os seguintes parâmetros:

- `x`: Posição X da assinatura no documento (em pixels).
- `y`: Posição Y da assinatura no documento (em pixels).
- `width`: Largura da assinatura (em pixels).
- `height`: Altura da assinatura (em pixels).
- `fontSize`: Tamanho da fonte do nome do assinante (em pixels).

## Instalação

1. Clone o repositório do módulo para o seu projeto.
2. Inclua o módulo como uma dependência no seu arquivo `pom.xml` ou `build.gradle`.
3. Configure as propriedades necessárias (conforme mencionado acima) no seu arquivo de configuração `application.properties` ou `application.yml`.

## Exemplo de Uso com `curl`

Para assinar um documento PDF utilizando nossa API, você pode usar o seguinte comando `curl` para enviar uma requisição POST. Este exemplo demonstra como passar os parâmetros necessários, incluindo o documento PDF e o certificado em base64, além de informações sobre o assinante e o layout da assinatura.

```bash
curl -X POST "https://url.do.servico.de.assinatura/assinarDocumento" \
     -H "Content-Type: application/json" \
     -d '{
           "srcPdfBase64": "PDF_em_Base64_aqui",
           "certificateBase64": "Certificado_em_Base64_aqui",
           "url": "https://url.do.servico.de.assinatura",
           "nomeAssinante": "Nome do Assinante",
           "x": 100,
           "y": 150,
           "width": 200,
           "height": 50,
           "fontSize": 12
         }'
