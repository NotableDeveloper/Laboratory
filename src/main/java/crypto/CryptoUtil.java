package crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtil {
    private static String ALGORITHMS = "AES";
    private static String PADDING = "AES/CBC/PKCS5Padding";
    private static final String KEY = System.getenv("SECRET_KEY");
    private static final IvParameterSpec FIXED_EMPTY_IV = new IvParameterSpec(new byte[16]);

    public static String encryptAES(String plainText) {
        String encoded_string = "";
        Cipher cipher = null;
        byte[] keyArr = null;
        byte[] encodedArr = null;
        SecretKeySpec keySpec = null;

        try {
            if(!(plainText == null || plainText.trim().length() == 0)) {
                keyArr = KEY.getBytes();
                keySpec = new SecretKeySpec(keyArr, ALGORITHMS);
                cipher = Cipher.getInstance(PADDING);
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, FIXED_EMPTY_IV);
                encodedArr = cipher.doFinal(plainText.getBytes());
                encoded_string = new String(Base64.getEncoder().encode(encodedArr));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return encoded_string;
    }

    public static String decryptAES(String encodedText) {
        String decoded_string = "";
        Cipher cipher = null;
        byte[] keyArr = null;
        byte[] decodedArr = null;
        SecretKeySpec keySpec = null;

        try {
            if(!(encodedText == null || encodedText.trim().length() == 0)) {
                keyArr = KEY.getBytes();
                keySpec = new SecretKeySpec(keyArr, ALGORITHMS);
                cipher = Cipher.getInstance(PADDING);
                cipher.init(Cipher.DECRYPT_MODE, keySpec, FIXED_EMPTY_IV);
                decodedArr = Base64.getDecoder().decode(encodedText.getBytes());
                decoded_string = new String(cipher.doFinal(decodedArr));
            }

        }catch(Exception e) {
            e.printStackTrace();
        }

        return decoded_string;
    }
}
