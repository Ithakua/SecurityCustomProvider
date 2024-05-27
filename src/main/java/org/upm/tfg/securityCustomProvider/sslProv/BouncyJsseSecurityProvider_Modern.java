package org.upm.tfg.securityCustomProvider.sslProv;

import org.apache.kafka.common.security.auth.SecurityProviderCreator;
import org.bouncycastle.jsse.BCSSLParameters;
import org.bouncycastle.jsse.BCSSLSocket;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.tls.NamedGroup;
import org.upm.tfg.securityCustomProvider.config.NamedGroupNames;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


//Experimental BCJSSE Security Provider with the new configuration method **Not Tested**
public class BouncyJsseSecurityProvider_Modern implements SecurityProviderCreator {

    private static final Logger LOGGER = Logger.getLogger(BouncyJsseSecurityProvider.class.getName());

    @Override
    public void configure(Map<String, ?> config) {

            Security.addProvider(new BouncyCastleJsseProvider());

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS", Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME).getName());

            SecureRandom secureRandom = new SecureRandom();
            sslContext.init(null, null, secureRandom);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            SSLSocket socket = (SSLSocket) socketFactory.createSocket();

            BCSSLSocket bcSslSocket = (BCSSLSocket) socket;

            BCSSLParameters sslParams = new BCSSLParameters();

            String[] allNames = NamedGroupNames.getAllNames();

            sslParams.setNamedGroups(allNames);

            bcSslSocket.setParameters(sslParams);

        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Se produjo un error al configurar el proveedor de seguridad", e);
        }


    }

    @Override
    public Provider getProvider() {
        if (Security.getProvider(BouncyCastlePQCProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastlePQCProvider());
        }
        return Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
    }
}
