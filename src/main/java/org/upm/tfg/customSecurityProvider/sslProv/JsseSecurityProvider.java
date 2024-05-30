package org.upm.tfg.customSecurityProvider.sslProv;

import org.apache.kafka.common.security.auth.SecurityProviderCreator;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.upm.tfg.customSecurityProvider.config.NamedGroupsConfig;

import java.security.*;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;


public class JsseSecurityProvider implements SecurityProviderCreator {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(JsseSecurityProvider.class.getName());

    @Override
    public void configure(Map<String, ?> config) {
        // Añade el proveedor BCJSSE
            Security.addProvider(new BouncyCastleJsseProvider());

        try {
            // Carga los grupos de algoritmos
            String[] allNames = NamedGroupsConfig.getAllNames();
            // Configura los grupos de algoritmos a utilizar por el sistema broker
            System.setProperty("jdk.tls.namedGroups", String.join(",", allNames));
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Se produjo un error al configurar el proveedor de seguridad", e);
        }
    }

    @Override
    public Provider getProvider() {
        // Añade el proveedor BCJSSE en caso de que no esté cargado
        if (Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleJsseProvider());
        }
        return Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
    }
}
