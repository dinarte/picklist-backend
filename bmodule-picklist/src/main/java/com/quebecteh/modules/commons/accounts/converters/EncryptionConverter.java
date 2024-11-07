package com.quebecteh.modules.commons.accounts.converters;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * This class is a JPA attribute converter that transparently encrypts and decrypts sensitive data.
 * It uses AES encryption to secure data at rest in the database.
 * 
 * <p>Note: In production environments, store the encryption key securely
 * using a service such as AWS KMS, Azure Key Vault, or Spring Cloud Vault.
 * This example uses a hardcoded key, which should NOT be used in real applications.</p>
 */
@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "1234567890123456".getBytes(); // Example key; replace with a secure key

    /**
     * Converts the entity attribute to its encrypted database representation.
     *
     * @param attribute the entity attribute value to be encrypted
     * @return the encrypted representation of the attribute as a Base64 encoded string
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting attribute", e);
        }
    }

    /**
     * Converts the encrypted database value to its decrypted entity attribute representation.
     *
     * @param dbData the encrypted database value, stored as a Base64 encoded string
     * @return the decrypted attribute value
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting attribute", e);
        }
    }
}
