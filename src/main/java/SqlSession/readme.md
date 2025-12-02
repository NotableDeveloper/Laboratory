# SqlSession 팩토리 예제 (SqlSession Factory Examples)

이 디렉토리에는 MyBatis의 `SqlSessionFactory`를 빌드하고 `SqlSession`을 얻는 두 가지 주요 방법을 보여주는 예제 코드가 포함되어 있습니다. 두 예제 모두 데이터베이스 연결을 실제로 확인하기 위해 간단한 쿼리를 실행합니다.

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
