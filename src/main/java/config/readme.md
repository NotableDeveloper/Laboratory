# 설정 관리

이 문서는 설정 관리 패키지의 설계 및 사용법을 설명합니다.

## 개요

`config` 패키지는 외부 YAML 파일로부터 애플리케이션 설정을 로드하고 관리하는 역할을 합니다. 이는 애플리케이션 전반에 걸쳐 설정 속성에 접근하는 중앙 집중식의 타입 안전한 방법을 제공합니다.

시스템은 두 가지 주요 설정 유형으로 나뉩니다:

1.  **시스템 설정 (`SystemConfig`)**: 핵심 시스템 구성 요소, 내부 서비스 및 데이터베이스 연결에 대한 설정을 포함합니다.
2.  **사이트 설정 (`SiteConfig`)**: 외부 서비스 및 타사 통합에 대한 설정을 포함합니다.

## 핵심 구성 요소

### `Configuration.java`

모든 설정에 접근하기 위한 주요 진입점입니다. 싱글톤으로 구현되어 있으며, 첫 접근 시 설정을 로드합니다.

**사용법:**

```java
// 전체 시스템 설정 가져오기
SystemConfig systemConfig = Configuration.getSystemConfig();

// 전체 사이트 설정 가져오기
SiteConfig siteConfig = Configuration.getSiteConfig();

// 특정 설정 객체 가져오기 (예: 데이터베이스 설정)
Database dbConfig = systemConfig.getConfig(Database.class);

// 특정 외부 서비스 설정 가져오기
FirstExternalService firstServiceConfig = siteConfig.getConfig(FirstExternalService.class);
```

### `ConfigReader` 및 `StreamYamlConfigReader`

-   **`ConfigReader.java`**: 설정 리더를 위한 계약을 정의하는 인터페이스입니다.
-   **`StreamYamlConfigReader.java`**: YAML 파일에서 설정을 읽는 주요 구현체입니다. **SnakeYAML** 라이브러리를 사용하여 YAML 문서를 파싱하고, **Java Reflection**을 활용하여 해당 설정 객체를 동적으로 인스턴스화하고 채웁니다.

## 작동 방식

1.  **초기화**: `Configuration` 싱글톤은 `getInstance()`에 대한 첫 호출 시 초기화됩니다.
2.  **환경 변수**: 생성자는 설정 파일을 찾기 위해 두 가지 환경 변수를 읽습니다:
    -   `SYSTEM_CONFIG_FILE_PATH`: 시스템 설정용 YAML 파일의 절대 경로.
    -   `SITE_CONFIG_FILE_PATH`: 사이트/외부 서비스 설정용 YAML 파일의 절대 경로.
3.  **파싱**: `StreamYamlConfigReader`는 이 YAML 파일들을 읽고 파싱하는 데 사용됩니다.
4.  **객체 매핑**: 리더는 YAML 파일의 데이터를 `SystemConfig` 및 `SiteConfig`로 시작하는 Java 객체 계층 구조로 매핑합니다. YAML 파일의 구조는 해당 Java 클래스의 필드 구조와 **일치해야 합니다**.

### YAML 구조 예시

다음 `SystemConfig` 구조가 주어졌을 때:

```java
public class SystemConfig {
    private Database database;
    // ... 기타 필드
}

public class Database {
    private String host;
    private int port;
    private String username;
    private String password;
    // ... 기타 필드
}
```

해당하는 `system-config.yml`은 다음과 같아야 합니다:

```yaml
database:
  host: "localhost"
  port: 5432
  username: "admin"
  password: "secure_password"
# ... 기타 설정
```

파서는 Java 클래스에 정의된 모든 필드가 YAML 파일에 존재하는지 확인하여 잘못된 설정을 방지합니다.

## 설정 확장

새로운 설정 속성 그룹을 추가하려면:

1.  **POJO 생성**: 새로운 속성을 나타내는 필드를 가진 새 Java 클래스를 정의합니다.
    ```java
    // 예: config/system/new_service/ 에
    public class NewServiceConfig {
        private String apiUrl;
        private int timeout;
        // getter 및 setter...
    }
    ```
2.  **루트 설정에 추가**: `SystemConfig.java` 또는 `SiteConfig.java`에 새 타입의 필드를 추가합니다.
    ```java
    // SystemConfig.java 에
    public class SystemConfig {
        // ... 기존 필드
        private NewServiceConfig newServiceConfig;
    }
    ```
3.  **YAML 파일 업데이트**: YAML 설정 파일에 해당 구조를 추가합니다.
    ```yaml
    # system-config.yml 에
    # ... 기존 설정
    newServiceConfig:
      apiUrl: "https://api.example.com/v1"
      timeout: 5000
    ```

그러면 설정은 자동으로 로드되어 `getConfig` 메서드를 통해 사용할 수 있게 됩니다.