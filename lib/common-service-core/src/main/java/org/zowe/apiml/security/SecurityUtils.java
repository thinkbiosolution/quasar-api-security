/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.security;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.zowe.apiml.message.log.ApimlLogger;
import org.zowe.apiml.message.yaml.YamlMessageServiceInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;

@Slf4j
@UtilityClass
public class SecurityUtils {

    private ApimlLogger apimlLog = ApimlLogger.of(SecurityUtils.class, YamlMessageServiceInstance.getInstance());

    public static final String SAFKEYRING = "safkeyring";

    /**
     * Loads secret key from keystore or key ring, if keystore URL starts with {@value #SAFKEYRING}
     *
     * @param config - {@link HttpsConfig} with mandatory filled fields: keyStore, keyStoreType, keyStorePassword, keyPassword,
     *               and optional filled: keyAlias and trustStore
     * @return {@link PrivateKey} or {@link javax.crypto.SecretKey} from keystore or key ring
     */
    public static Key loadKey(HttpsConfig config) {
        if (StringUtils.isNotEmpty(config.getKeyStore())) {
            try {
                KeyStore ks = loadKeyStore(config);
                char[] keyPasswordInChars = config.getKeyPassword();
                final Key key;
                if (config.getKeyAlias() != null) {
                    key = ks.getKey(config.getKeyAlias(), keyPasswordInChars);
                } else {
                    throw new KeyStoreException("No key alias provided.");
                }
                return key;
            } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException
                | UnrecoverableKeyException e) {
                apimlLog.log("org.zowe.apiml.common.errorLoadingSecretKey", e.getMessage());
                throw new HttpsConfigError(e.getMessage(), e,
                    HttpsConfigError.ErrorCode.HTTP_CLIENT_INITIALIZATION_FAILED, config);
            }
        }
        return null;
    }

    /**
     * Return certificate chain of certificate using to sing of HTTPS communication (certificate stored in keystore,
     * under keyAlias, both values are determinated via config)
     *
     * @param config defines path to KeyStore and key alias
     * @return chain of certificates loaded from Keystore to alias keyAlias from HttpsConfig
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     */
    public static Certificate[] loadCertificateChain(HttpsConfig config) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        if (StringUtils.isNotEmpty(config.getKeyStore())) {
            KeyStore ks = loadKeyStore(config);
            return ks.getCertificateChain(config.getKeyAlias());
        }
        return new Certificate[0];
    }

    /**
     * Method load certification chain from KeyStore for KeyAlias stored in the HttpsConfig. Then it takes public keys
     * and convert them into Base64 code and return them as a Set.
     *
     * @param config defines path to KeyStore and key alias
     * @return Base64 codes of all public certificates determinated for KeyAlias in KeyStore
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     */
    public static Set<String> loadCertificateChainBase64(HttpsConfig config) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        final Set<String> out = new HashSet<>();
        for (Certificate certificate : loadCertificateChain(config)) {
            final byte[] certificateEncoded = certificate.getPublicKey().getEncoded();
            final String base64 = Base64.getEncoder().encodeToString(certificateEncoded);
            out.add(base64);
        }
        return out;
    }

    /**
     * Loads public key from keystore or key ring, if keystore URL starts with {@value #SAFKEYRING}
     *
     * @param config - {@link HttpsConfig} with mandatory filled fields: keyStore, keyStoreType, keyStorePassword, keyPassword,
     *               and optional filled: keyAlias and trustStore
     * @return {@link PublicKey} from keystore or key ring
     */
    public static PublicKey loadPublicKey(HttpsConfig config) {
        if (StringUtils.isNotEmpty(config.getKeyStore())) {
            try {
                KeyStore ks = loadKeyStore(config);
                Certificate cert = null;
                if (config.getKeyAlias() != null) {
                    cert = ks.getCertificate(config.getKeyAlias());
                } else {
                    for (Enumeration<String> e = ks.aliases(); e.hasMoreElements(); ) {
                        String alias = e.nextElement();
                        cert = ks.getCertificate(alias);
                        if (cert != null) {
                            break;
                        }
                    }
                }
                if (cert != null) {
                    return cert.getPublicKey();
                }
            } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException e) {
                apimlLog.log("org.zowe.apiml.common.errorLoadingPublicKey", e.getMessage());
                throw new HttpsConfigError(e.getMessage(), e,
                    HttpsConfigError.ErrorCode.HTTP_CLIENT_INITIALIZATION_FAILED, config);
            }
        }
        return null;
    }

    /**
     * Finds a private key by public key in keystore or key ring, if keystore URL starts with {@value #SAFKEYRING}
     *
     * @param config    {@link HttpsConfig} with mandatory filled fields: keyStore, keyStoreType, keyStorePassword, keyPassword,
     *                  and optional filled: keyAlias and trustStore
     * @param publicKey in byte[]
     * @return {@link PrivateKey} from keystore or key ring
     */
    public static Key findPrivateKeyByPublic(HttpsConfig config, byte[] publicKey) {
        if (StringUtils.isNotEmpty(config.getKeyStore())) {
            try {
                KeyStore ks = loadKeyStore(config);
                char[] keyPasswordInChars = config.getKeyPassword();
                Key key = null;
                for (Enumeration<String> e = ks.aliases(); e.hasMoreElements(); ) {
                    String alias = e.nextElement();
                    Certificate cert = ks.getCertificate(alias);
                    if (Arrays.equals(cert.getPublicKey().getEncoded(), publicKey)) {
                        key = ks.getKey(alias, keyPasswordInChars);
                        if (key != null) {
                            break;
                        }
                    }
                }
                return key;
            } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | UnrecoverableKeyException e) {
                apimlLog.log("org.zowe.apiml.common.errorLoadingSecretKey", e.getMessage());
                throw new HttpsConfigError("Error loading secret key: " + e.getMessage(), e,
                    HttpsConfigError.ErrorCode.HTTP_CLIENT_INITIALIZATION_FAILED, config);
            }
        }
        return null;
    }

    /**
     * Loads keystore or key ring, if keystore URL starts with {@value #SAFKEYRING}, from specified location
     *
     * @param config {@link HttpsConfig} with mandatory filled fields: keyStore, keyStoreType, keyStorePassword,
     *               and optional filled: trustStore
     * @return the new {@link KeyStore} or key ring as {@link KeyStore}
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    public static KeyStore loadKeyStore(HttpsConfig config) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(config.getKeyStoreType());
        InputStream inputStream;
        if (config.getKeyStore().startsWith(SAFKEYRING)) {
            URL url = keyRingUrl(config.getKeyStore(), config.getTrustStore());
            inputStream = url.openStream();
        } else {
            File keyStoreFile = new File(config.getKeyStore());
            inputStream = new FileInputStream(keyStoreFile);
        }
        ks.load(inputStream, config.getKeyStorePassword());
        return ks;
    }

    /**
     * Creates an {@link URL} to key ring location
     *
     * @param uri        - key ring location
     * @param trustStore - truststore location
     * @return the new {@link URL} with 2 slashes instead of 4
     * @throws MalformedURLException throws in case of incorrect key ring format
     */
    public static URL keyRingUrl(String uri, String trustStore) throws MalformedURLException {
        if (!uri.startsWith(SAFKEYRING + "://")) {
            throw new MalformedURLException("Incorrect key ring format: " + trustStore
                + ". Make sure you use format safkeyring:////userId/keyRing");
        }
        return new URL(replaceFourSlashes(uri));
    }

    /**
     * Replaces 4 slashes on 2 in URI
     *
     * @param storeUri - URI as {@link String}
     * @return same URI, but with 2 slashes, or null, if {@code storeUri} is null
     */
    public static String replaceFourSlashes(String storeUri) {
        return storeUri == null ? null : storeUri.replaceFirst("////", "//");
    }

    /**
     * Creates a pair of {@link PrivateKey} and {@link PublicKey} keys
     *
     * @param algorithm - key algorithm
     * @param keySize   - key size
     * @return the new {@link KeyPair}
     */
    public static KeyPair generateKeyPair(String algorithm, int keySize) {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
            kpg.initialize(keySize);
            kp = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            log.debug("An error occurred while generating keypair: {}", e.getMessage());
        }
        return kp;
    }
}
