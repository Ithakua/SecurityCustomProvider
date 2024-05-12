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
            // KEM Post Quantum
            String mlkem512 = "OQS_mlkem512";
            String mlkem768 = "OQS_mlkem768";
            String mlkem1024 = "OQS_mlkem1024";
            String Draft_mlkem768 = "DRAFT_mlkem768";
            String Draft_mlkem1024 = "DRAFT_mlkem1024";

            // KEM Clásicos
            String x25519 = "x25519";
            String x448 = "x448";
            String brainpoolP256r1tls13 = "brainpoolP256r1tls13";
            String brainpoolP384r1tls13 = "brainpoolP384r1tls13";
            String brainpoolP512r1tls13 = "brainpoolP512r1tls13";

            String namedGroups = String.join(",", mlkem512, mlkem768, mlkem1024, Draft_mlkem768, Draft_mlkem1024,
                    x25519, x448, brainpoolP256r1tls13, brainpoolP384r1tls13, brainpoolP512r1tls13);

            System.setProperty("jdk.tls.namedGroups", namedGroups);

        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Se produjo un error al configurar el proveedor de seguridad", e);
        }

    }

    @Override
    public Provider getProvider() {
        return Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
    }
}
