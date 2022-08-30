package com.alanpeinejr.encryption;

import java.util.Base64;

/**
 * Main class for handling "encryption"
 * Should use cipher, secretkey, ivParameters, but the tokens aren't being signed anyways and wire is assumed safe
 */
public class EncryptionManager implements IEncrypt, IDecrypt {

    @Override
    public String decrypt(String string) {
        return new String(Base64.getDecoder().decode(string));
    }

    @Override
    public String encrypt(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }
}
