package com.google.code.springcryptoutils.core.signature;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * The default implementation for verifying the authenticity of messages using
 * base64 encoded digital signatures when the public key is configured
 * in an underlying mapping using a logical name.
 *
 * @author Mirko Caserta (mirko.caserta@gmail.com)
 */
public class Base64EncodedVerifierWithChooserByPublicKeyIdImpl implements Base64EncodedVerifierWithChooserByPublicKeyId {

    private Map<String, Base64EncodedVerifier> cache = new HashMap<String, Base64EncodedVerifier>();

    private Map<String, PublicKey> publicKeyMap;

    private String algorithm = "SHA1withRSA";

    private String charsetName = "UTF-8";

    /**
     * The map of public keys where the map keys are logical names
     * which must match the publicKeyId parameter as specified in the
     * verify method.
     *
     * @param publicKeyMap the public key map
     * @see #verify(String, String, String)
     */
    public void setPublicKeyMap(Map<String, PublicKey> publicKeyMap) {
        this.publicKeyMap = publicKeyMap;
    }

    /**
     * The signature algorithm. The default is SHA1withRSA.
     *
     * @param algorithm the signature algorithm
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * The charset to use when converting a string into a
     * raw byte array representation. The default is UTF-8.
     *
     * @param charsetName the charset name (default: UTF-8)
     */
    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * Verifies the authenticity of a message using a base64 encoded digital signature.
     *
     * @param publicKeyId the logical name of the public key as configured
     *                    in the underlying mapping
     * @param message     the original message to verify
     * @param signature   the base64 encoded digital signature
     * @return true if the original message is verified by the digital signature
     * @see #setPublicKeyMap(java.util.Map)
     */
    public boolean verify(String publicKeyId, String message, String signature) {
        Base64EncodedVerifier verifier = cache.get(publicKeyId);

        if (verifier != null) {
            return verifier.verify(message, signature);
        }

        Base64EncodedVerifierImpl verifierImpl = new Base64EncodedVerifierImpl();
        verifierImpl.setAlgorithm(algorithm);
        verifierImpl.setCharsetName(charsetName);
        PublicKey publicKey = publicKeyMap.get(publicKeyId);

        if (publicKey == null) {
            throw new SignatureException("public key not found: publicKeyId=" + publicKeyId);
        }

        verifierImpl.setPublicKey(publicKey);
        cache.put(publicKeyId, verifierImpl);
        return verifierImpl.verify(message, signature);
    }

}
