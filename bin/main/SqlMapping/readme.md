## SqlMapping - SQL 매핑 예제

guide.md **4. SQL 매핑** 섹션에 대응하는 예제 패키지입니다.

### 학습 목표

| 기능 | 설명 |
|------|------|
| `<sql>` + `<include>` | 공통 SQL 구문 추출 및 재사용 |
| `<resultMap>` | 컬럼-프로퍼티 명시적 매핑 |
| `useGeneratedKeys` | INSERT 후 자동 생성 PK 획득 |
| `<association>` | 1:1 관계 중첩 객체 매핑 |
| `<collection>` | 1:N 관계 리스트 매핑 |
| `<discriminator>` | 컬럼 값에 따른 조건부 Java 타입 선택 |

### 사용 테이블

- **film**: 기본 CRUD 대상 (film_id, title, rating, length 등)
- **language**: film 과 1:1 association (language_id, name)
- **actor + film_actor**: actor 와 1:N collection (배우 → 출연 영화 목록)

### 패키지 구조

```
SqlMapping/
├── entity/
│   ├── Film.java            - film 테이블 엔티티
│   ├── Language.java        - language 테이블 엔티티
│   ├── FilmDetail.java      - film + language (association 결과)
│   ├── ActorWithFilms.java  - actor + List<Film> (collection 결과)
│   ├── FilmSummary.java     - discriminator 기반 클래스
│   ├── FamilyFilm.java      - G/PG 등급 영화 (FilmSummary 서브클래스)
│   └── RestrictedFilm.java  - PG-13/R/NC-17 등급 영화 (FilmSummary 서브클래스)
├── mapper/
│   ├── FilmMapper.java      - 매퍼 인터페이스
│   └── FilmMapper.xml       - SQL 매핑 XML
├── mybatis-config.xml       - SqlMapping 전용 MyBatis 설정
├── CrudExample.java         - SELECT/INSERT/UPDATE/DELETE 예제
└── ResultMapExample.java    - association/collection/discriminator 예제
```

### 실행 방법

```bash
# 기본 CRUD 예제
./gradlew runSqlMappingCrudExample

# ResultMap 심화 예제
./gradlew runSqlMappingResultMapExample
```

### 핵심 XML 패턴

#### `<sql>` 공통 컬럼 정의 및 `<include>` 재사용

```xml
<sql id="filmBaseColumns">
    film_id, title, description, ...
</sql>

<select id="selectAll" resultMap="filmMap">
    SELECT <include refid="filmBaseColumns"/>
    FROM film LIMIT 10
</select>
```

#### `<association>` - 1:1 관계

```xml
<resultMap id="filmDetailMap" type="FilmDetail">
    <id column="film_id" property="filmId"/>
    <association property="language" javaType="Language" columnPrefix="lang_">
        <id column="language_id" property="languageId"/>
        <result column="name" property="name"/>
    </association>
</resultMap>
```

#### `<collection>` - 1:N 관계

```xml
<resultMap id="actorWithFilmsMap" type="ActorWithFilms">
    <id column="actor_id" property="actorId"/>
    <collection property="films" ofType="Film">
        <id column="film_id" property="filmId"/>
        <result column="title" property="title"/>
    </collection>
</resultMap>
```

#### `<discriminator>` - 조건부 타입 선택

```xml
<resultMap id="filmSummaryMap" type="FilmSummary">
    <id column="film_id" property="filmId"/>
    <result column="rating" property="rating"/>
    <discriminator javaType="String" column="rating">
        <case value="G"  resultType="FamilyFilm"/>
        <case value="R"  resultType="RestrictedFilm"/>
    </discriminator>
</resultMap>
```
