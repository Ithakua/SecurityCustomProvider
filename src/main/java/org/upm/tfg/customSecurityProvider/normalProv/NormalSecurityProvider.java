package org.upm.tfg.customSecurityProvider.normalProv;

import org.apache.kafka.common.security.auth.SecurityProviderCreator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;
import java.util.Map;

public class NormalSecurityProvider implements SecurityProviderCreator {
    @Override
    public void configure(Map<String, ?> config) {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public Provider getProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        return Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
    }
}
