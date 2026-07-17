package by.shakhau.core.user.repository.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
public class EncryptStringConverter implements AttributeConverter<String, String> {

    private static final String CRYPTO_STANDARD = "AES";
    private static final String ALGORITHM = CRYPTO_STANDARD + "/ECB/PKCS5Padding";

    private final SecretKeySpec secretKey;

    public EncryptStringConverter(@Value("${crypto.card-secret-key}") String secretKeyValue) {
        if (secretKeyValue.length() != 32) {
            throw new IllegalArgumentException(
                    "Key length must be 32 symbols. Current length is %d symbols".formatted(secretKeyValue.length()));
        }

        secretKey = new SecretKeySpec(secretKeyValue.getBytes(), CRYPTO_STANDARD);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Field encryption error", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException("Field decryption error", e);
        }
    }
}
