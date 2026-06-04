## Cache - 캐시 예제

guide.md **6. 캐시** 섹션에 대응하는 예제 패키지입니다.

### 1차 캐시 vs 2차 캐시 비교

| 항목 | 1차 캐시 | 2차 캐시 |
|------|----------|----------|
| 범위 | SqlSession | 네임스페이스(Mapper) |
| 기본 활성화 | 항상 ON | `cacheEnabled=true` + `<cache/>` 필요 |
| 수명 | SqlSession 종료 시 소멸 | SqlSessionFactory 종료 전까지 유지 |
| 직렬화 | 불필요 | 엔티티가 `Serializable` 구현 필요 |
| 반환 객체 | 동일 인스턴스 | 역직렬화된 새 인스턴스 |
| 캐시 저장 시점 | 즉시 | SqlSession **close/commit** 후 |

### 사용 테이블

- **film**: film_id, title, rating, length (단순 조회 반복으로 캐시 효과 확인)

### 패키지 구조

```
Cache/
├── entity/
│   └── Film.java                   - Serializable 구현 (2차 캐시 필수)
├── mapper/
│   ├── FilmCacheMapper.java        - 매퍼 인터페이스
│   └── FilmCacheMapper.xml         - <cache/> 선언 포함
├── mybatis-config.xml              - cacheEnabled=true 설정
├── FirstLevelCacheExample.java     - 1차 캐시 동작 확인
└── SecondLevelCacheExample.java    - 2차 캐시 동작 확인
```

### 실행 방법

```bash
# 1차 캐시 예제
./gradlew runFirstLevelCacheExample

# 2차 캐시 예제
./gradlew runSecondLevelCacheExample
```

### 핵심 설정

#### `mybatis-config.xml`

```xml
<setting name="cacheEnabled" value="true"/>
```

#### `FilmCacheMapper.xml`

```xml
<!-- 기본 LRU 캐시 활성화 -->
<cache/>

<!-- 세부 조정 예시 -->
<cache
    eviction="FIFO"
    flushInterval="60000"
    size="512"
    readOnly="true"/>
```

#### 2차 캐시 활성화 체크리스트

- [ ] `mybatis-config.xml` : `cacheEnabled=true`
- [ ] 매퍼 XML : `<cache/>` 선언
- [ ] 엔티티 클래스 : `implements Serializable`
- [ ] SqlSession : 조회 후 반드시 `close()` 또는 `commit()` 호출
