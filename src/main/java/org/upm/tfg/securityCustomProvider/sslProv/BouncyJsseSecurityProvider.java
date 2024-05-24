package org.upm.tfg.securityCustomProvider.sslProv;

import org.apache.kafka.common.security.auth.SecurityProviderCreator;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.upm.tfg.securityCustomProvider.config.NamedGroupNames;

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
            String[] allNames = NamedGroupNames.getAllNames();
            System.setProperty("jdk.tls.namedGroups", String.join(",", allNames));
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Se produjo un error al configurar el proveedor de seguridad", e);
        }
    }

    @Override
    public Provider getProvider() {
        return Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
    }
}
