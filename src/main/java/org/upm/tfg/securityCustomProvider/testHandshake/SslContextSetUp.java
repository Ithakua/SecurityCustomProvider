package org.upm.tfg.securityCustomProvider.testHandshake;

import org.bouncycastle.jsse.BCSSLParameters;
import org.bouncycastle.jsse.BCSSLSocket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.FileWriter;
import java.io.PrintWriter;

public class SslContextSetUp {

    long handshakeTime;

    public void configureAndRun(String[] groups, int iterations) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, NoSuchProviderException {
        // Ruta al archivo del certificado de la CA
        String caCertPath = System.getProperty("user.dir") + "/../post-quantum-support-kafka/certificates/ca/ca.crt";
        String host = "localhost";
        int port = 9093;

        // Genera el archivo .csv para guardar los resultados en la carpeta testing del proyecto Kafka
        try (PrintWriter writer = new PrintWriter(new FileWriter(System.getProperty("user.dir") + "/../post-quantum-support-kafka/testing/handshake_times.csv"))) {
            writer.print("N");
            for (String group : groups) {
                writer.print("," + group);
            }
            writer.println();

            for (int i = 0; i < iterations; i++) {
                // Primera iteración no se guarda para mejorar las mediciones
                if (i > 0) {
                    writer.print(i);
                    for (String group : groups) {
                        long handshakeTime = performHandshake(caCertPath, host, port, group);
                        writer.printf(",%.6f", handshakeTime / 1_000_000.0);
                    }
                    writer.println();
                } else {
                    // Solo realizar el handshake para calentar la conexión
                    for (String group : groups) {
                        performHandshake(caCertPath, host, port, group);
                    }
                }
            }
        }
    }

    private long performHandshake(String caCertPath, String host, int port, String group) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, NoSuchProviderException {
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

        try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket()) {
            // Configurar el socket
            socket.connect(new InetSocketAddress(host, port));

            // Configurar el socket para usar el grupo de claves especificado
            if (socket instanceof BCSSLSocket) {
                BCSSLSocket bcSslSocket = (BCSSLSocket) socket;
                BCSSLParameters bcsslParameters = new BCSSLParameters();
                bcsslParameters.setNamedGroups(new String[]{group});
                bcSslSocket.setParameters(bcsslParameters);
            }

            // Medir el tiempo de handshake
            long startTime = System.nanoTime();
            socket.startHandshake();
            long endTime = System.nanoTime();
            handshakeTime = endTime - startTime;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return handshakeTime;
    }
}
