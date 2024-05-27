package org.upm.tfg.securityCustomProvider.config;

import org.bouncycastle.tls.NamedGroup;

public class NamedGroupNames{

    public static String[] getAllNames() {

        //PQ-KEM Algorithms
        String mlkem512 = NamedGroup.getName(NamedGroup.OQS_mlkem512);
        String mlkem768 = NamedGroup.getName(NamedGroup.OQS_mlkem768);
        String mlkem1024 = NamedGroup.getName(NamedGroup.OQS_mlkem1024);

        //Classic KEM Algorithms
        String x448 = NamedGroup.getName(NamedGroup.x448);
        String x25519 = NamedGroup.getName(NamedGroup.x25519);

        String secp256r1 = NamedGroup.getName(NamedGroup.secp256r1);
        String secp384r1 = NamedGroup.getName(NamedGroup.secp384r1);
        String secp521r1 = NamedGroup.getName(NamedGroup.secp521r1);

        return new String[]{mlkem512, mlkem768, mlkem1024, x448, x25519, secp256r1, secp384r1, secp521r1};

    }
}