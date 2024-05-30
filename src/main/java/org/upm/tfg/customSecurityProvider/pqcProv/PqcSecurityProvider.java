package org.upm.tfg.customSecurityProvider.pqcProv;

import java.security.Provider;
import java.security.Security;
import java.util.Map;

import org.apache.kafka.common.security.auth.SecurityProviderCreator;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

public class PqcSecurityProvider implements SecurityProviderCreator {

    @Override
    public void configure(Map<String, ?> config) {
        Security.addProvider(new BouncyCastlePQCProvider());
    }

    @Override
    public Provider getProvider() {
        if (Security.getProvider(BouncyCastlePQCProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastlePQCProvider());
        }
        return Security.getProvider(BouncyCastlePQCProvider.PROVIDER_NAME);
    }
}
