package org.upm.tfg.customSecurityProvider.testHandshake;

import org.upm.tfg.customSecurityProvider.config.NamedGroupsConfig;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class TestHandshake_clientAuth {
    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastleJsseProvider());

        String[] groups = NamedGroupsConfig.getAllNames();
        int iterations = 101;

        try {
            SslContextSetUp_clientAuth sslContext_clientAuth = new SslContextSetUp_clientAuth();
            sslContext_clientAuth.configureAndRun(groups, iterations);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | NoSuchProviderException | CertificateException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
