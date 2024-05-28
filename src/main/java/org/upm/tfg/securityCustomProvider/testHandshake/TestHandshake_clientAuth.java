package org.upm.tfg.securityCustomProvider.testHandshake;

import org.upm.tfg.securityCustomProvider.config.NamedGroupNames;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class TestHandshake_clientAuth {
    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastleJsseProvider());

        String[] groups = NamedGroupNames.getAllNames();
        int iterations = 101;

        try {
            SslContextSetUp_clientAuth sslContext_clientAuth = new SslContextSetUp_clientAuth();
            sslContext_clientAuth.configureAndRun(groups, iterations);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | NoSuchProviderException | CertificateException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
