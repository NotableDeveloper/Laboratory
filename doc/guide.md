### MyBatis 학습 로드맵

#### 1. MyBatis 기본 개념 이해
- **MyBatis란 무엇인가?**
    - 기존의 JDBC 프로그래밍과 비교했을 때의 장점 파악
    - 퍼시스턴스 프레임워크로서의 역할 이해

#### 2. MyBatis 시작하기
- **`SqlSessionFactory` 빌드**
    - XML 설정 파일을 이용한 `SqlSessionFactory` 생성 방법
    - Java 코드를 이용한 `SqlSessionFactory` 생성 방법
- **`SqlSession`의 역할과 생명주기**
    - `SqlSession`을 통한 데이터베이스 연동
    - `SqlSession`의 스코프(Scope) 및 생명주기(Lifecycle) 관리의 중요성
- **매퍼(Mapper) 인터페이스**
    - 명명공간(Namespace)의 개념과 중요성
    - 매퍼 인터페이스와 XML 매퍼의 연동 방법

#### 3. 핵심 설정 파일 (mybatis-config.xml) 분석
- **`properties`**: 외부 설정 파일(.properties) 연동
- **`settings`**: MyBatis의 주요 동작 설정 (캐시, 지연 로딩 등)
- **`typeAliases`**: 자주 사용하는 클래스에 대한 별칭 설정
- **`typeHandlers`**: 자바 데이터 타입과 JDBC 데이터 타입 간의 변환 규칙 정의
- **`objectFactory`**: 조회 결과를 담을 객체 생성 방법 제어
- **`plugins`**: MyBatis의 동작을 가로채는 인터셉터(Interceptor) 구현
- **`environments`**: 개발, 운영 등 다양한 데이터베이스 환경 설정
    - `transactionManager`: 트랜잭션 관리 방식 설정 (JDBC, MANAGED)
    - `dataSource`: 데이터베이스 커넥션 풀 설정 (UNPOOLED, POOLED, JNDI)
- **`mappers`**: SQL 구문이 정의된 매퍼 파일 등록

#### 4. SQL 매핑 (Mapper XML)
- **기본 CRUD 작업**
    - `select`: 데이터 조회
    - `insert`, `update`, `delete`: 데이터 변경
- **파라미터와 결과 매핑**
    - `parameterType`: SQL 구문에 전달될 파라미터의 타입 지정
    - `resultType`: 조회 결과를 자동으로 매핑할 객체 타입 지정
    - `resultMap`: 복잡한 조회 결과를 객체에 매핑하는 방법
        - `<id>`, `<result>`: 기본 컬럼 매핑
        - `<association>`: 1:1 관계 매핑
        - `<collection>`: 1:N 관계 매핑
        - `<discriminator>`: 조건에 따라 다른 매핑 적용
- **재사용 가능한 SQL**
    - `<sql>`: 공통 SQL 구문을 정의하고 재사용

#### 5. 동적 SQL
- 조건에 따라 SQL 구문을 동적으로 생성하는 방법
    - `<if>`
    - `<choose>`, `<when>`, `<otherwise>`
    - `<trim>`, `<where>`, `<set>`
    - `<foreach>`: 컬렉션(List, Array)을 이용한 IN 조건 등 처리

#### 6. 캐시
- **1차 캐시 (로컬 캐시)**: `SqlSession` 레벨의 캐시 이해
- **2차 캐시**: 네임스페이스 레벨의 캐시 설정 및 활용
    - `<cache>`: 캐시 활성화 및 정책 설정
    - `<cache-ref>`: 다른 네임스페이스의 캐시 공유

#### 7. Java API 활용
- `SqlSessionFactoryBuilder`, `SqlSessionFactory`, `SqlSession`의 주요 API
- **애노테이션(Annotation) 기반 매핑**
    - `@Select`, `@Insert`, `@Update`, `@Delete` 등 애노테이션을 이용한 SQL 작성
- `SqlBuilder` 클래스를 이용한 동적 SQL 생성

#### 8. 로깅
- MyBatis 실행 SQL 및 결과 로깅 설정 (Log4j, SLF4J 등 연동)
