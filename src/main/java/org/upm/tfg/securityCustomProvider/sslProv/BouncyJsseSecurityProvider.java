package org.upm.tfg.securityCustomProvider.sslProv;

import org.apache.kafka.common.security.auth.SecurityProviderCreator;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import java.security.Provider;
import java.security.Security;
import java.util.Map;

public class BouncyJsseSecurityProvider implements SecurityProviderCreator {

    @Override
    public void configure(Map<String, ?> config) {
        // Specify config if necessary
    }

    @Override
    public Provider getProvider() {
        if (Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleJsseProvider());
        }
        return Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
    }
}
