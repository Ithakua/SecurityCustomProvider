package org.upm.tfg.securityCustomProvider.testHandshake;

import org.upm.tfg.securityCustomProvider.config.NamedGroupNames;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class TestHandshake {
    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastleJsseProvider());

        String[] groups = NamedGroupNames.getAllNames();
        int iterations = 100;

        try {
            SslContextSetUp sslContext = new SslContextSetUp();
            sslContext.configureAndRun(groups, iterations);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | NoSuchProviderException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }
}
