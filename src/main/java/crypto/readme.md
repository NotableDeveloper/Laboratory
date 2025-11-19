# Crypto 패키지

이 패키지는 AES 암호화 및 복호화 기능을 제공하는 유틸리티 클래스를 포함합니다.

## 구성 요소

### `CryptoUtil.java`

`CryptoUtil` 클래스는 문자열 데이터를 암호화하고 복호화하기 위한 정적 메서드를 제공합니다.

-   **암호화 알고리즘**: AES (Advanced Encryption Standard)
-   **운영 모드**: CBC (Cipher Block Chaining)
-   **패딩**: PKCS5Padding

#### 주요 메서드

-   `public static String encryptAES(String plainText)`:
    주어진 평문 문자열을 AES 알고리즘을 사용하여 암호화합니다. 암호화된 데이터는 Base64로 인코딩된 문자열로 반환됩니다.
-   `public static String decryptAES(String encodedText)`:
    주어진 Base64 인코딩된 암호문 문자열을 AES 알고리즘을 사용하여 복호화합니다. 복호화된 평문 문자열이 반환됩니다.

#### 보안 관련 참고 사항

-   **비밀 키**: 암호화 및 복호화에 사용되는 비밀 키는 `SECRET_KEY`라는 환경 변수에서 로드됩니다. 이 환경 변수가 설정되어 있지 않으면 암호화/복호화 작업이 실패할 수 있습니다.
-   **초기화 벡터 (IV)**: 이 구현은 모든 바이트가 0인 **고정된 초기화 벡터(IV)**를 사용합니다 (`FIXED_EMPTY_IV`). **이는 보안상 권장되지 않습니다.** 고정된 IV를 사용하면 암호화가 선택 평문 공격(chosen-plaintext attacks)과 같은 특정 유형의 공격에 취약해질 수 있습니다. 실제 프로덕션 환경에서는 각 암호화 작업마다 고유하고 예측 불가능한 IV를 생성하고 이를 암호문과 함께 저장하여 사용하는 것이 강력히 권장됩니다.

## 사용 방법

`CryptoUtil` 클래스의 `encryptAES` 및 `decryptAES` 메서드를 직접 호출하여 데이터를 암호화하거나 복호화할 수 있습니다.

**예시:**

```java
// 환경 변수 SECRET_KEY가 설정되어 있어야 합니다.
// 예: export SECRET_KEY="YourSecretKey123" (16, 24 또는 32바이트 길이)

String plainText = "Hello, World!";
String encryptedText = CryptoUtil.encryptAES(plainText);
System.out.println("암호화된 텍스트: " + encryptedText);

String decryptedText = CryptoUtil.decryptAES(encryptedText);
System.out.println("복호화된 텍스트: " + decryptedText);
```

**주의:** `SECRET_KEY` 환경 변수는 AES 알고리즘의 요구 사항에 따라 16, 24 또는 32바이트 길이여야 합니다.
