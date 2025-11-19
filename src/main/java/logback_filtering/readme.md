# Logback 필터링 예제

이 패키지는 Java 애플리케이션을 위한 인기 있는 로깅 프레임워크인 Logback 내에서 사용자 정의 필터링 및 데이터 정화를 구현하는 방법을 보여주는 예제를 제공합니다. 이 예제들은 로그에서 민감한 정보를 처리하는 두 가지 주요 접근 방식을 보여줍니다: 로그 이벤트를 완전히 거부하는 방법과 로그 메시지 내에서 민감한 데이터를 마스킹/정화하는 방법입니다.

## 구성 요소

### `Client.java`

이 클래스는 Logback 필터링의 작동 방식을 보여주는 진입점 역할을 합니다. `User` 객체를 생성하고 로깅하며, 일부 객체는 전화번호와 같은 민감한 정보를 포함합니다. 이 클라이언트의 출력은 제공된 필터 및 컨버터를 사용하도록 Logback 구성(`logback.xml`)이 어떻게 설정되었는지에 따라 달라집니다.

### `User.java`

사용자를 나타내는 간단한 POJO(Plain Old Java Object)입니다. `name`, `phoneNumber`, `email` 필드를 포함합니다. `lombok.Builder`, `lombok.Getter`, `lombok.ToString` 어노테이션은 편의를 위해 사용되었으며, `ToString`은 `Client`가 `User` 객체를 직접 로깅하므로 특히 관련이 있습니다.

### `PhoneNumberFilter.java`

이 클래스는 `ch.qos.logback.core.filter.Filter<ILoggingEvent>`를 확장하는 사용자 정의 Logback `Filter`입니다. 특정 민감한 패턴을 포함하는 로그 이벤트가 더 이상 처리되지 않도록 방지하는 것이 목적입니다.

-   **기능**: 로그 메시지 내에서 한국 휴대폰 번호(`010\d{8}`)를 감지하기 위해 정규 표현식을 사용합니다.
-   **결정 로직**:
    -   전화번호가 감지되면 `FilterReply.DENY`를 반환하여 로그 이벤트가 완전히 삭제됩니다.
    -   그렇지 않으면 `FilterReply.NEUTRAL`을 반환하여 이벤트가 후속 필터 또는 어펜더로 전달되도록 합니다.

이 필터는 민감한 내용으로 인해 특정 로그 메시지가 어떤 로그 출력에도 나타나지 않아야 할 때 유용합니다.

### `UserDataConverter.java`

이 클래스는 로그 메시지 내용을 수정할 수 있는 사용자 정의 Logback `CompositeConverter`를 구현합니다. 로그 이벤트를 거부하는 대신, 이 컨버터는 민감한 데이터를 제거하여 로그를 정화합니다.

-   **기능**: 정규 표현식을 사용하여 다음을 식별하고 제거합니다:
    -   한국 휴대폰 번호(`010\d{8}`).
    -   한국 이름(두 개 이상의 한글 문자 시퀀스 `[가-힣]{2,}`).
-   **사용법**: 이 컨버터는 Logback 패턴 레이아웃(예: `logback.xml`)에 통합되어 로그 메시지가 어펜더에 기록되기 전에 민감한 정보를 자동으로 마스킹할 수 있습니다.

이 컨버터는 로그 이벤트 자체는 중요하지만, 개인 정보 보호 또는 보안상의 이유로 특정 정보가 수정되어야 하는 시나리오에 이상적입니다.

## 사용 방법

이러한 필터와 컨버터를 사용하려면 일반적으로 `logback.xml` 파일에서 구성해야 합니다.

**예시 `logback.xml` 스니펫 (개념적):**

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="logback_filtering.PhoneNumberFilter" /> <!-- 전화번호 필터 적용 -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %replace(%msg){'010\d{8}', ''}%n</pattern>
            <!-- 또는 사용자 정의 컨버터를 사용하려면: -->
            <!-- <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %customUserDataConverter{%msg}%n</pattern> -->
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>

    <!-- 패턴에서 사용자 정의 컨버터를 사용하는 경우 정의 -->
    <!-- <conversionRule conversionWord="customUserDataConverter" converterClass="logback_filtering.UserDataConverter" /> -->
</configuration>
```

**참고**: `logback.xml` 파일은 `src/main/resources/logback.xml` 및 `build/resources/main/logback.xml`에 있습니다. 사용자 정의 필터 및 컨버터를 활성화하고 테스트하려면 이 파일을 수정해야 합니다.