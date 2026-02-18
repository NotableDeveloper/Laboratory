## DynamicSql - 동적 SQL 예제

guide.md **5. 동적 SQL** 섹션에 대응하는 예제 패키지입니다.

### 학습 목표

| 태그 | 역할 |
|------|------|
| `<if>` | 조건이 참일 때만 SQL 구문 포함 |
| `<where>` | AND/OR 자동 처리 + 비어 있으면 WHERE 생략 |
| `<choose>/<when>/<otherwise>` | switch-case 형태의 조건 분기 |
| `<set>` | 부분 UPDATE를 위한 SET 절 (마지막 쉼표 자동 제거) |
| `<foreach>` | List/Array를 IN 절로 변환 |
| `<trim>` | prefix/suffix를 직접 제어하는 범용 태그 |

### 사용 테이블

- **film**: title, rating, length 컬럼으로 다양한 동적 조건 검색

### 패키지 구조

```
DynamicSql/
├── entity/
│   ├── Film.java                - film 엔티티
│   └── FilmSearchCriteria.java  - 검색 조건 파라미터 객체
├── mapper/
│   ├── FilmDynamicMapper.java   - 매퍼 인터페이스
│   └── FilmDynamicMapper.xml    - 동적 SQL 매핑 XML
├── mybatis-config.xml           - DynamicSql 전용 MyBatis 설정
└── DynamicSqlExample.java       - 예제 실행 클래스
```

### 실행 방법

```bash
./gradlew runDynamicSqlExample
```

### 핵심 XML 패턴

#### `<where>` + `<if>`

```xml
<select id="searchWithIf" ...>
    SELECT ... FROM film
    <where>
        <if test="rating != null and rating != ''">
            AND rating = #{rating}
        </if>
        <if test="minLength != null">
            AND length >= #{minLength}
        </if>
    </where>
</select>
```

#### `<choose>` / `<when>` / `<otherwise>`

```xml
ORDER BY
<choose>
    <when test="sortBy == 'title'">title ASC</when>
    <when test="sortBy == 'length'">length DESC</when>
    <otherwise>film_id ASC</otherwise>
</choose>
```

#### `<set>` + `<if>` (부분 UPDATE)

```xml
<update id="updatePartial" ...>
    UPDATE film
    <set>
        <if test="title != null">title = #{title},</if>
        <if test="rentalRate != null">rental_rate = #{rentalRate},</if>
    </set>
    WHERE film_id = #{filmId}
</update>
```

#### `<foreach>` (IN 절)

```xml
<select id="selectByIds" ...>
    SELECT ... FROM film
    WHERE film_id IN
    <foreach collection="ids" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
</select>
```

#### `<trim>`

```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
    <if test="title != null">AND title LIKE #{title}</if>
    <if test="rating != null">AND rating = #{rating}</if>
</trim>
```

> **`<trim>` vs `<where>`**: `<where>` 는 "WHERE" 접두사와 "AND/OR" 제거가 고정된 편의 태그이고,
> `<trim>` 은 prefix/prefixOverrides/suffix/suffixOverrides 를 모두 직접 지정할 수 있는 범용 태그입니다.
