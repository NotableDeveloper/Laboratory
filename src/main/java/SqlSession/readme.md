# SqlSession 팩토리 예제 (SqlSession Factory Examples)

이 디렉토리에는 MyBatis의 `SqlSessionFactory`를 빌드하고 `SqlSession`을 얻는 두 가지 주요 방법과 생명 주기를 다루는 예제 코드들이 포함되어 있습니다. 예제에서는 데이터베이스 연결을 실제로 확인하기 위해 간단한 쿼리를 실행합니다.

## 1. `BuildSqlSessionWithXML.java` (XML 기반 설정)

이 예제는 `src/main/resources/mybatis-config.xml` 파일에 정의된 설정을 사용하여 `SqlSessionFactory`를 빌드합니다.

### 주요 특징:
- `mybatis-config.xml`에서 데이터베이스 연결 정보(드라이버, URL, 사용자명, 비밀번호) 및 트랜잭션 관리자를 설정합니다.
- `<mappers>` 섹션을 통해 매퍼 인터페이스(`ConnectionTestMapper.java`)를 등록합니다.
- `SqlSessionFactoryBuilder`를 사용하여 XML 설정 파일로부터 `SqlSessionFactory`를 생성합니다.
- 생성된 `SqlSessionFactory`로부터 `SqlSession`을 열고 `ConnectionTestMapper`를 사용하여 `SELECT 1` 쿼리를 실행하여 데이터베이스 연결을 검증합니다.

### 실행 방법:
프로젝트 루트 디렉토리에서 다음 Gradle 명령어를 실행합니다:
```bash
gradlew runXmlSqlSessionExample
```

## 2. `BuildSqlSessionWithJava.java` (Java 코드 기반 설정)

이 예제는 XML 설정 파일 없이 순수 Java 코드를 사용하여 `SqlSessionFactory`를 구성하고 빌드합니다.

### 주요 특징:
- `PooledDataSource`, `JdbcTransactionFactory`, `Environment`, `Configuration` 객체를 Java 코드 내에서 직접 생성하고 구성합니다.
- `Configuration` 객체에 `ConnectionTestMapper.class`를 직접 추가하여 매퍼를 등록합니다.
- `SqlSessionFactoryBuilder`를 사용하여 Java `Configuration` 객체로부터 `SqlSessionFactory`를 생성합니다.
- 마찬가지로 `SqlSession`을 열고 `ConnectionTestMapper`를 사용하여 데이터베이스 연결을 검증합니다.

### 실행 방법:
프로젝트 루트 디렉토리에서 다음 Gradle 명령어를 실행합니다:
```bash
gradlew runJavaSqlSessionExample
```

## 3. `ConnectionTestMapper.java`

이 인터페이스는 두 예제에서 데이터베이스 연결 상태를 확인하기 위해 사용되는 간단한 MyBatis 매퍼입니다. `@Select("SELECT 1")` 애노테이션을 사용하여 `SELECT 1` 쿼리를 직접 정의합니다.

---

**참고:** 이 예제를 실행하려면 MySQL 데이터베이스가 실행 중이어야 하며, `mybatis-config.xml` 또는 `BuildSqlSessionWithJava.java`에 설정된 `sakila` 데이터베이스 및 `simple-user`/`q1w2e3r4!` 계정이 올바르게 구성되어 있어야 합니다.

## 4. SqlSessionFactoryBuilder, SqlSessionFactory, SqlSession 생명 주기 (Lifecycle)

MyBatis의 핵심 컴포넌트인 `SqlSessionFactoryBuilder`, `SqlSessionFactory`, `SqlSession`은 각각 다른 생명 주기와 역할을 가집니다. 이를 이해하는 것은 MyBatis 애플리케이션의 성능 최적화와 올바른 리소스 관리에 중요합니다.

### SqlSessionFactoryBuilder

- **역할**: `SqlSessionFactory` 인스턴스를 빌드하는 일회성 객체입니다. `SqlSessionFactory`는 XML 설정 파일이나 Java 코드 기반 설정을 통해 빌드될 수 있습니다.
- **생명 주기**: `SqlSessionFactoryBuilder`는 애플리케이션 시작 시 `SqlSessionFactory`를 생성하는 데 단 한 번만 사용됩니다. `SqlSessionFactory`를 생성하고 나면 더 이상 필요 없으므로, 해당 객체는 즉시 가비지 컬렉션의 대상이 됩니다. 즉, **Transient (임시) 스코프**를 가집니다.
- **주의사항**: 애플리케이션 실행 중에 여러 개의 `SqlSessionFactoryBuilder` 인스턴스를 생성할 필요는 없습니다.

### SqlSessionFactory

- **역할**: `SqlSession` 인스턴스를 생성하는 팩토리 객체입니다. 데이터베이스 커넥션 풀 및 트랜잭션 관리와 같은 모든 MyBatis의 영속성 계층 환경에 대한 정보를 가지고 있습니다.
- **생명 주기**: `SqlSessionFactory`는 애플리케이션 전체에서 유일하게 존재해야 하는 **Singleton (싱글톤) 스코프**를 가집니다. 애플리케이션 시작 시 한 번 생성되면, 애플리케이션이 종료될 때까지 계속 사용되어야 합니다. 여러 스레드에서 안전하게 공유될 수 있도록 설계되었습니다.
- **주의사항**: `SqlSessionFactory`를 여러 개 생성하는 것은 비효율적이며, 예상치 못한 문제를 야기할 수 있습니다.

### SqlSession

- **역할**: 데이터베이스와의 단일 상호작용(예: SQL 쿼리 실행, 트랜잭션 처리)을 나타내는 객체입니다. JDBC `Connection` 객체에 대한 래퍼(Wrapper) 역할을 합니다.
- **생명 주기**: `SqlSession`은 짧은 수명의 객체로, 특정 요청(Request)이나 작업 단위에 따라 생성되고 소멸되는 **Request (요청) 스코프** 또는 **Transient (임시) 스코프**를 가집니다. 각 데이터베이스 작업이 시작될 때 새로운 `SqlSession`을 열고, 작업이 완료되면 반드시 닫아야 합니다. `try-with-resources` 구문을 사용하여 자동으로 닫히도록 하는 것이 권장됩니다.
- **주의사항**: `SqlSession`은 스레드에 안전하지 않으므로, 여러 스레드에서 공유해서는 안 됩니다. 또한, 사용 후 반드시 닫지 않으면 데이터베이스 커넥션 누수와 같은 심각한 리소스 문제를 발생시킬 수 있습니다.

---

### 예제 코드: `SqlSessionLifecycle.java`

`SqlSessionLifecycle.java` 파일은 위에서 설명한 세 가지 컴포넌트의 생명 주기를 실제 코드로 보여주는 예제입니다. 이 파일을 통해 각 객체가 언제 생성되고, 어떻게 사용되며, 언제 소멸되어야 하는지 확인할 수 있습니다.

### 실행 방법:
프로젝트 루트 디렉토리에서 다음 Gradle 명령어를 실행합니다:
```bash
gradlew runSqlSessionLifecycleExample
```