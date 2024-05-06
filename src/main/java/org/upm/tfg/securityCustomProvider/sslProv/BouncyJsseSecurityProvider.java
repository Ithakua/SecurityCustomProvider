package org.upm.tfg.securityCustomProvider.sslProv;

import org.apache.kafka.common.security.auth.SecurityProviderCreator;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.bouncycastle.tls.NamedGroup;

import java.security.*;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;


public class BouncyJsseSecurityProvider implements SecurityProviderCreator {

    private static final Logger LOGGER = Logger.getLogger(BouncyJsseSecurityProvider.class.getName());

    @Override
    public void configure(Map<String, ?> config) {

        if (Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleJsseProvider());
        }

        try {
//            // Crear un SSLContext con BCJSSE
//            SSLContext sslContext = SSLContext.getInstance("TLS", Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME).getName());
//
//
//            // Configurar el SSLContext con los parámetros necesarios
//            SecureRandom secureRandom = new SecureRandom();
//            sslContext.init(null, null, secureRandom);
//
//
//            // Obtener un socket factory del SSLContext
//            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
//
//            // Crear un socket con el socket factory
//            SSLSocket socket = (SSLSocket) socketFactory.createSocket();
//
//            // Obtener una instancia de BCSSLSocket
//            BCSSLSocket bcSslSocket = (BCSSLSocket) socket;
//
//            // Crear los parámetros SSL
//            BCSSLParameters sslParams = new BCSSLParameters();

            // Establecer el grupo de nombres deseado
//        String namedGroup1 = String.valueOf(NamedGroup.OQS_mlkem512);
//        String namedGroup2 = String.valueOf(NamedGroup.OQS_mlkem768);
//        String namedGroup3 = String.valueOf(NamedGroup.OQS_mlkem1024);
//        String namedGroup4 = String.valueOf(NamedGroup.DRAFT_mlkem768);
//        String namedGroup5 = String.valueOf(NamedGroup.DRAFT_mlkem1024);

        // Establecer el grupo de nombres deseado
            String prueba = NamedGroup.OQS_mlkem512;
            String mlkem512 = "OQS_mlkem512";
            String mlkem768 = "OQS_mlkem768";
            String mlkem1024 = "OQS_mlkem1024";
            String Draft_mlkem768 = "DRAFT_mlkem768";
            String Draft_mlkem1024 = "DRAFT_mlkem1024";

        // Construir la cadena de grupos de nombres
            String namedGroups = String.join(",", mlkem512, mlkem768, mlkem1024, Draft_mlkem768, Draft_mlkem1024);

            // Establecer la propiedad del sistema jdk.tls.namedGroups
            System.setProperty("jdk.tls.namedGroups", namedGroups);
            //System.setProperty("jdk.tls.namedGroups", "OQS_mlkem768, OQS_mlkem512, OQS_mlkem1024, DRAFT_mlkem768, DRAFT_mlkem1024");

//            //sslParams.setNamedGroups(new String[]{namedGroup});
//            sslParams.setNamedGroups(new String[]{namedGroup1, namedGroup2, namedGroup3, namedGroup4, namedGroup5});
//
//
//            // Establecer los parámetros SSL en el socket BCSSLSocket
//            bcSslSocket.setParameters(sslParams);

        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Se produjo un error al configurar el proveedor de seguridad", e);
        }



    }

    @Override
    public Provider getProvider() {
        return Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
    }
}
