package com.alanpeinejr.encryption;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EncryptionManagerTest {

    private EncryptionManager encryptionManager;

    @Before()
    public void before() {
        encryptionManager = new EncryptionManager();
    }

    @Test
    public void testEncrypt() {
        assertNotEquals("testString", encryptionManager.encrypt("testString"));
        assertNotEquals("", encryptionManager.encrypt("testString"));
    }

    @Test
    public void testDecrypt() {
        assertEquals("testString", encryptionManager.decrypt("dGVzdFN0cmluZw=="));
    }

}
