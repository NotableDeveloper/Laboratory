package crypto;

public class CryptoUtilTest {
    public static void main(String[] args) {
        String originalText = "abcdef";

        // 암호화
        String encryptedText = CryptoUtil.encryptAES(originalText);
        System.out.println("Encrypted: " + encryptedText);

        // 복호화
        String decryptedText = CryptoUtil.decryptAES(encryptedText);
        System.out.println("Decrypted: " + decryptedText);

        // 검증
        if (originalText.equals(decryptedText)) {
            System.out.println("Success: 복호화 결과가 원본과 일치합니다.");
        } else {
            System.out.println("Failure: 복호화 결과가 원본과 다릅니다.");
        }
    }
}