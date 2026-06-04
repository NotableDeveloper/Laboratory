# MyBatis Configuration (mybatis-config.xml) Guide

MyBatis의 `mybatis-config.xml` 파일은 MyBatis 애플리케이션의 핵심 설정을 정의하는 역할을 합니다. 이 파일은 데이터베이스 연결, 트랜잭션 관리, 매퍼 등록, 타입 별칭 정의 등 다양한 MyBatis 동작 방식을 구성합니다.

## 주요 태그 및 설명

`mybatis-config.xml` 파일은 `<configuration>` 루트 태그 아래에 여러 하위 태그들로 구성됩니다.

### 1. `<configuration>`

모든 MyBatis 설정 파일의 루트 요소입니다.

### 2. `<properties>`

외부 `.properties` 파일에서 속성 값을 로드하거나, XML 내부에 직접 속성을 정의할 수 있습니다. 이를 통해 데이터베이스 연결 정보와 같은 변경될 수 있는 값들을 코드와 분리하여 관리할 수 있습니다.

```xml
<properties resource="config/jdbc.properties">
  <property name="username" value="dev_user"/>
</properties>
```

-   `resource`: 클래스패스에 있는 `.properties` 파일의 경로를 지정합니다.
-   `url`: 파일 시스템 경로를 지정합니다.

### 3. `<settings>`

MyBatis의 런타임 동작 방식을 미세 조정하는 다양한 설정을 포함합니다.

주요 설정 예시:

-   `cacheEnabled` (true/false): 전역 캐시를 사용할지 여부를 설정합니다.
-   `lazyLoadingEnabled` (true/false): 지연 로딩(lazy loading)을 사용할지 여부를 설정합니다.
-   `mapUnderscoreToCamelCase` (true/false): 데이터베이스 컬럼명(스네이크 케이스)을 자바 객체의 프로퍼티명(카멜 케이스)으로 자동 매핑할지 여부를 설정합니다.
-   `logImpl`: MyBatis가 사용할 로깅 구현체를 지정합니다 (예: `SLF4J`, `LOG4J2`, `STDOUT_LOGGING`).

```xml
<settings>
    <setting name="mapUnderscoreToCamelCase" value="true"/>
    <setting name="cacheEnabled" value="true"/>
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```

### 4. `<typeAliases>`

긴 자바 클래스 이름을 짧은 별칭으로 대체하여 XML 설정 파일의 가독성을 높입니다.

```xml
<typeAliases>
    <typeAlias alias="Author" type="com.example.model.Author"/>
    <package name="com.example.model"/> <!-- 패키지 내 모든 클래스에 대해 자동으로 별칭 생성 -->
</typeAliases>
```

-   `typeAlias`: 특정 클래스에 대한 별칭을 정의합니다.
-   `package`: 지정된 패키지 내의 모든 클래스에 대해 클래스 이름을 소문자로 한 별칭을 자동으로 부여합니다.

### 5. `<environments>`

데이터베이스 환경 설정을 정의합니다. 여러 개의 환경을 정의하고 `default` 속성으로 기본 환경을 지정할 수 있습니다.

```xml
<environments default="development">
    <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="url" value="jdbc:mysql://localhost:3306/sakila"/>
            <property name="username" value="simple-user"/>
            <property name="password" value="q1w2e3r4!"/>
        </dataSource>
    </environment>
</environments>
```

-   `environment`: 각 데이터베이스 환경을 정의합니다. `id` 속성으로 환경을 식별합니다.

#### `<transactionManager>`

트랜잭션 관리 방식을 정의합니다.

-   `type="JDBC"`: JDBC 커밋/롤백 기능을 직접 사용합니다.
-   `type="MANAGED"`: 컨테이너(예: Spring)가 트랜잭션을 관리하도록 합니다.

#### `<dataSource>`

데이터베이스 연결 방식을 정의합니다.

-   `type="POOLED"`: MyBatis 자체의 JDBC 커넥션 풀을 사용합니다.
-   `type="UNPOOLED"`: 매번 요청 시 새로운 커넥션을 엽니다.
-   `type="JNDI"`: JNDI 컨텍스트에서 데이터소스를 얻어옵니다.
-   `property`: 데이터소스의 드라이버, URL, 사용자명, 비밀번호 등 구체적인 연결 정보를 설정합니다.

### 6. `<mappers>`

SQL 매퍼 파일(XML) 또는 매퍼 인터페이스(Java)를 등록합니다. MyBatis가 SQL을 찾고 실행할 수 있도록 합니다.

```xml
<mappers>
    <mapper resource="org/mybatis/example/BlogMapper.xml"/>
    <mapper class="com.example.mapper.ConnectionTestMapper"/>
    <package name="com.example.mapper"/> <!-- 패키지 내 모든 매퍼 인터페이스를 등록 -->
</mappers>
```

-   `mapper resource`: 클래스패스에 있는 매퍼 XML 파일의 경로를 지정합니다.
-   `mapper url`: 파일 시스템 경로를 통해 매퍼 XML 파일을 지정합니다.
-   `mapper class`: 매퍼 인터페이스 클래스의 풀네임을 지정합니다. 인터페이스와 동일한 위치에 매퍼 XML 파일이 있다면 자동으로 로드됩니다.
-   `package`: 지정된 패키지 내의 모든 매퍼 인터페이스를 자동으로 등록합니다. 매퍼 XML 파일은 인터페이스와 동일한 이름으로 동일한 디렉토리에 있어야 합니다.
