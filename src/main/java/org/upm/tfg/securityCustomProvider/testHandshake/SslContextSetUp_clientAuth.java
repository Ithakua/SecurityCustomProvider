package org.upm.tfg.securityCustomProvider.testHandshake;

import org.bouncycastle.jsse.BCSSLParameters;
import org.bouncycastle.jsse.BCSSLSocket;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.FileWriter;
import java.io.PrintWriter;

public class SslContextSetUp_clientAuth {

    long handshakeTime;

    public void configureAndRun(String[] groups, int iterations) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, NoSuchProviderException, UnrecoverableKeyException {
        // Ruta al archivo del certificado de la CA
        String caCertPath = System.getProperty("user.dir") + "/../post-quantum-support-kafka/certificates/ca/ca.crt";
        // Rutas a los archivos del cliente
        String clientCertPath = System.getProperty("user.dir") + "/../post-quantum-support-kafka/certificates/clients/client-signed.pem";
        String clientKeyPath = System.getProperty("user.dir") + "/../post-quantum-support-kafka/certificates/clients/client-key.pem";
        String clientKeystorePath = System.getProperty("user.dir") + "/../post-quantum-support-kafka/certificates/clients/client.keystore.pkcs12";

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
                        long handshakeTime = performHandshake(caCertPath, clientCertPath, clientKeyPath, clientKeystorePath, host, port, group);
                        writer.printf(",%.6f", handshakeTime / 1_000_000.0);
                    }
                    writer.println();
                } else {
                    for (String group : groups) {
                        performHandshake(caCertPath, clientCertPath, clientKeyPath, clientKeystorePath, host, port, group);
                    }
                }
            }
        }
    }

    private long performHandshake(String caCertPath, String clientCertPath, String clientKeyPath, String clientKeystorePath, String host, int port, String group) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, NoSuchProviderException, UnrecoverableKeyException {
        // Crear un KeyStore para la CA
        KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        caKeyStore.load(null, null);

        // Cargar el certificado de la CA
        try (FileInputStream fis = new FileInputStream(caCertPath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate) cf.generateCertificate(fis);
            caKeyStore.setCertificateEntry("caCert", caCert);
        }

        // Carga la KeyStore para el cliente
        KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(clientKeystorePath)) {
            clientKeyStore.load(fis, "123456".toCharArray());
        }

        // Configurar el SSLContext para usar BouncyCastle JSSE Provider
        SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");

        // Crear un TrustManager que confíe en el certificado de la CA
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKeyStore);

        // Crear un KeyManager para el cliente
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, "123456".toCharArray()); // Asume que la contraseña es 'password', cámbiala según sea necesario

        // Inicializar el SSLContext con nuestro TrustManager y KeyManager
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        // Crear una fábrica de sockets SSL
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket()) {
            // Configurar el socket
            socket.connect(new InetSocketAddress(host, port));

            // Configurar el socket para usar el grupo de algoritmos especificado
            if (socket instanceof BCSSLSocket) {
                BCSSLSocket bcSslSocket = (BCSSLSocket) socket;
                BCSSLParameters bcsslParameters = new BCSSLParameters();
                bcsslParameters.setNamedGroups(new String[]{group});
                bcSslSocket.setParameters(bcsslParameters);
            }

            // Mide el tiempo de handshake
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
