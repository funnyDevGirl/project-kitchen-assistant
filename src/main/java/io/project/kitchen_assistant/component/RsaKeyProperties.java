package io.project.kitchen_assistant.component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "rsa")
@Setter
@Getter
public class RsaKeyProperties {

    private String publicKey;
    private String privateKey;

    private RSAPublicKey rsaPublicKey;
    private RSAPrivateKey rsaPrivateKey;

    @PostConstruct
    public void init() throws Exception {
        this.rsaPublicKey = getPublicKeyFromPem(this.publicKey);
        this.rsaPrivateKey = getPrivateKeyFromPem(this.privateKey);
    }

    private RSAPublicKey getPublicKeyFromPem(String pem) throws Exception {
        String publicKeyPEM = cleanPemString(pem);

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private RSAPrivateKey getPrivateKeyFromPem(String pem) throws Exception {
        String privateKeyPEM = cleanPemString(pem);

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private String cleanPemString(String pem) {
        return pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace(" ", "")
                .replace("\t", "")
                .replace("\n", "")
                .replace("\r", "");
    }
}
