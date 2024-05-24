package org.upm.tfg.securityCustomProvider.testHandshake;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.BCSSLSocket;
import org.bouncycastle.jsse.BCSSLParameters;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class TestHandshake {
    public static void main(String[] args) {
        // Añadir BouncyCastle como proveedor de seguridad
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastleJsseProvider());

        // Ruta al archivo del certificado de la CA
        String caCertPath = System.getProperty("user.home") + "/Documents/Kafka/SandBox_Kafka/post-quantum-support-kafka/certificates/ca/ca.crt";

        // Dirección del servidor y puerto
        String host = "localhost";
        int port = 9093;

        try {
            // Crear un KeyStore vacío
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            // Cargar el certificado de la CA
            try (FileInputStream fis = new FileInputStream(caCertPath)) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate caCert = (X509Certificate) cf.generateCertificate(fis);
                keyStore.setCertificateEntry("caCert", caCert);
            }

            // Configurar el SSLContext para usar BouncyCastle
            SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");

            // Crear un TrustManager que confíe en el certificado de la CA
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Inicializar el SSLContext con nuestro TrustManager
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

            // Crear una fábrica de sockets SSL
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Crear un socket SSL y medir el tiempo de handshake
            try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket()) {
                // Configurar el socket
                socket.connect(new InetSocketAddress(host, port));

                // Configurar el socket para usar el grupo X448
                if (socket instanceof BCSSLSocket) {
                    BCSSLSocket bcSslSocket = (BCSSLSocket) socket;
                    BCSSLParameters bcsslParameters = new BCSSLParameters();
                    bcsslParameters.setNamedGroups(new String[]{"OQS_mlkem1024"});
                    bcSslSocket.setParameters(bcsslParameters);
                }

                long startTime = System.nanoTime();
                socket.startHandshake();
                long endTime = System.nanoTime();
                long handshakeTime = endTime - startTime;

                System.out.println("Tiempo de handshake: " + (handshakeTime / 1_000_000) + " ms");
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
}
