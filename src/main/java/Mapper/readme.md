# MyBatis Mapper 구현 예제

이 패키지는 MyBatis의 **Mapper Interface** 방식을 사용하여 데이터베이스와 상호작용하는 방법을 보여줍니다.

## 1. 구현 구성 요소

### 1) Entity (데이터 객체)
- `entity.Actor`: 데이터베이스의 `actor` 테이블과 매핑되는 Java 객체(POJO)입니다.
- 테이블의 컬럼(`actor_id`, `first_name` 등)과 클래스의 필드를 일치시켜 데이터를 담습니다.

### 2) Mapper Interface
- `Mapper.ActorMapper`: SQL 쿼리를 정의하는 인터페이스입니다.
- 별도의 구현 클래스(`implements`)를 작성하지 않고, 인터페이스의 메소드에 `@Select`와 같은 어노테이션을 사용하여 SQL을 명시합니다.

```java
public interface ActorMapper {
    @Select("SELECT * FROM actor WHERE actor_id = #{id}")
    Actor selectActorById(Short id);
}
```

### 3) 실행 설정 (Configuration)
- `Mapper.ActorMapperExample`: MyBatis 설정을 초기화하고 Mapper를 실행하는 예제입니다.
- `configuration.addMapper(ActorMapper.class)`를 통해 MyBatis에 인터페이스를 등록해야 합니다.

---

## 2. 동작 원리 (MyBatis Mapper Proxy)

MyBatis의 가장 강력한 기능 중 하나는 **Mapper Proxy**입니다.

1.  **인터페이스 정의**: 개발자는 메소드와 SQL만 정의한 인터페이스를 작성합니다.
2.  **프록시 생성**: 애플리케이션 시작 시(또는 `getMapper` 호출 시), MyBatis는 Java Dynamic Proxy를 사용하여 `ActorMapper` 인터페이스의 **구현체(Proxy 객체)를 자동으로 생성**합니다.
3.  **메소드 가로채기**: 코드에서 `mapper.selectActorById(1)`을 호출하면, 실제로는 MyBatis가 생성한 프록시 객체의 메소드가 실행됩니다.
4.  **SQL 실행**: 프록시 객체는 어노테이션(또는 XML)에 정의된 SQL(`SELECT ...`)을 찾아 파라미터(`#{id}`)를 바인딩하고, JDBC를 통해 DB에 실행합니다.
5.  **결과 매핑**: DB에서 반환된 `ResultSet`을 `Actor` 객체로 변환하여 반환합니다.

## 3. 주요 특징

- **SQL 코드 분리**: Java 코드 내에 SQL이 혼재하는 것을 어노테이션이나 XML로 깔끔하게 관리할 수 있습니다.
- **타입 안정성**: 문자열로 쿼리를 실행하는 기존 JDBC 방식보다 메소드 호출 방식을 사용하므로 컴파일 시점에 타입 체크가 가능합니다.
- **생산성**: 반복적인 JDBC 코딩(Connection 생성, PreparedStatement 처리, ResultSet 파싱 등)을 MyBatis가 대신 처리해 줍니다.
