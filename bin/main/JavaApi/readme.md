## JavaApi - Java API 활용 예제

guide.md **7. Java API 활용** 섹션에 대응하는 예제 패키지입니다.

### 학습 목표

| 주제 | 내용 |
|------|------|
| 애노테이션 기반 매핑 | `@Select`, `@Insert`, `@Update`, `@Delete` |
| 결과 매핑 | `@Results`, `@Result`, `@ResultMap` |
| 자동 키 생성 | `@Options(useGeneratedKeys=true)` |
| 동적 SQL | `@SelectProvider` + `SQL` 빌더 클래스 |
| Java 설정 | XML 없이 `Configuration` 객체로 SqlSessionFactory 구성 |

### 사용 테이블

- **category**: category_id, name, last_update (단순한 구조로 애노테이션 매핑에 적합)

### 패키지 구조

```
JavaApi/
├── entity/
│   └── Category.java              - category 엔티티
├── mapper/
│   ├── CategoryMapper.java        - @Select/@Insert/@Update/@Delete 매퍼
│   └── CategorySqlBuilder.java    - @SelectProvider 에서 사용하는 SQL 빌더
├── AnnotationMappingExample.java  - 애노테이션 기반 CRUD 예제
└── SqlBuilderExample.java         - SQL 클래스 및 @SelectProvider 예제
```

### 실행 방법

```bash
# 애노테이션 기반 CRUD 예제
./gradlew runAnnotationMappingExample

# SQL 빌더 클래스 예제
./gradlew runSqlBuilderExample
```

### 주요 애노테이션 정리

#### 기본 CRUD

```java
@Select("SELECT category_id, name FROM category WHERE category_id = #{id}")
Category selectById(Byte id);

@Insert("INSERT INTO category (name) VALUES (#{name})")
@Options(useGeneratedKeys = true, keyProperty = "categoryId")
int insert(Category category);

@Update("UPDATE category SET name = #{name} WHERE category_id = #{categoryId}")
int update(Category category);

@Delete("DELETE FROM category WHERE category_id = #{categoryId}")
int delete(Byte categoryId);
```

#### 결과 매핑 정의 및 재사용

```java
// @Results 로 정의
@Select("SELECT category_id, name FROM category WHERE category_id = #{id}")
@Results(id = "categoryResultMap", value = {
    @Result(id = true, column = "category_id", property = "categoryId"),
    @Result(column = "name", property = "name")
})
Category selectById(Byte id);

// @ResultMap 으로 재사용
@Select("SELECT category_id, name FROM category")
@ResultMap("categoryResultMap")
List<Category> selectAll();
```

#### @SelectProvider + SQL 빌더

```java
// 매퍼 인터페이스
@SelectProvider(type = CategorySqlBuilder.class, method = "buildSearchQuery")
List<Category> search(Map<String, Object> params);

// SQL 빌더 클래스
public class CategorySqlBuilder {
    public String buildSearchQuery(Map<String, Object> params) {
        return new SQL() {{
            SELECT("category_id, name");
            FROM("category");
            if (params.get("name") != null) {
                WHERE("name LIKE CONCAT('%', #{name}, '%')");
            }
            ORDER_BY("category_id");
        }}.toString();
    }
}
```

#### Java 코드로 SqlSessionFactory 구성

```java
PooledDataSource dataSource = new PooledDataSource();
dataSource.setDriver("com.mysql.cj.jdbc.Driver");
dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");

Configuration configuration = new Configuration(
    new Environment("dev", new JdbcTransactionFactory(), dataSource)
);
configuration.addMapper(CategoryMapper.class);

SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
```
