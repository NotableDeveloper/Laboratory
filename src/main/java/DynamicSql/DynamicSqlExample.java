package DynamicSql;

import DynamicSql.entity.Film;
import DynamicSql.entity.FilmSearchCriteria;
import DynamicSql.mapper.FilmDynamicMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 동적 SQL 예제
 *
 * MyBatis의 동적 SQL 태그들을 실제 film 테이블 조회/수정에 적용합니다.
 *
 * 주요 학습 포인트:
 *  - <if>: 조건부 SQL 조각 포함/제외
 *  - <where>: AND/OR 앞뒤 처리를 자동화하는 WHERE 절 생성
 *  - <choose>/<when>/<otherwise>: switch-case 형태의 조건 분기
 *  - <set>: 부분 UPDATE를 위한 SET 절 자동 구성
 *  - <foreach>: 컬렉션을 IN 절로 변환
 *  - <trim>: prefix/suffix를 직접 제어하는 범용 태그
 *
 * 실행: ./gradlew runDynamicSqlExample
 */
public class DynamicSqlExample {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSqlExample.class);

    public static void main(String[] args) throws IOException {
        Reader reader = Resources.getResourceAsReader("DynamicSql/mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

        logger.info("========================================");
        logger.info("   동적 SQL 예제");
        logger.info("========================================");

        try (SqlSession session = factory.openSession()) {
            FilmDynamicMapper mapper = session.getMapper(FilmDynamicMapper.class);

            // ── 1. <where> + <if>: 조건 없이 전체 조회 ───────────────────
            logger.info("\n[1-1] <where> + <if>: 조건 없음 → WHERE 절 자체가 생략됨");
            FilmSearchCriteria empty = new FilmSearchCriteria();
            List<Film> allFilms = mapper.searchWithIf(empty);
            logger.info("  결과 {}건 (조건 없음, 전체에서 LIMIT 10)", allFilms.size());

            // ── 2. <where> + <if>: 단일 조건 ─────────────────────────────
            logger.info("\n[1-2] <where> + <if>: rating='PG' 조건만 적용");
            FilmSearchCriteria byRating = new FilmSearchCriteria();
            byRating.setRating("PG");
            List<Film> pgFilms = mapper.searchWithIf(byRating);
            logger.info("  PG 등급 영화: {}건", pgFilms.size());
            pgFilms.stream().limit(3).forEach(f -> logger.info("  {}", f));

            // ── 3. <where> + <if>: 다중 조건 ─────────────────────────────
            logger.info("\n[1-3] <where> + <if>: rating='PG' AND length 90~120분 복합 조건");
            FilmSearchCriteria multiCriteria = new FilmSearchCriteria();
            multiCriteria.setRating("PG");
            multiCriteria.setMinLength((short) 90);
            multiCriteria.setMaxLength((short) 120);
            List<Film> filtered = mapper.searchWithIf(multiCriteria);
            logger.info("  결과: {}건", filtered.size());
            filtered.stream().limit(5).forEach(f -> logger.info("  {}", f));

            // ── 4. <choose>/<when>/<otherwise>: 정렬 전략 선택 ────────────
            logger.info("\n[2] <choose>/<when>/<otherwise>: sortBy 값에 따라 ORDER BY 변경");

            FilmSearchCriteria sortByTitle = new FilmSearchCriteria();
            sortByTitle.setRating("G");
            sortByTitle.setSortBy("title");
            logger.info("  sortBy='title' → ORDER BY title ASC");
            mapper.searchWithChoose(sortByTitle)
                    .stream().limit(3)
                    .forEach(f -> logger.info("  {}", f));

            FilmSearchCriteria sortByLength = new FilmSearchCriteria();
            sortByLength.setRating("G");
            sortByLength.setSortBy("length");
            logger.info("  sortBy='length' → ORDER BY length DESC");
            mapper.searchWithChoose(sortByLength)
                    .stream().limit(3)
                    .forEach(f -> logger.info("  {}", f));

            FilmSearchCriteria noSort = new FilmSearchCriteria();
            noSort.setRating("G");
            // sortBy 를 설정하지 않음 → <otherwise> 가 적용됨
            logger.info("  sortBy=null → <otherwise> 적용 → ORDER BY film_id ASC");
            mapper.searchWithChoose(noSort)
                    .stream().limit(3)
                    .forEach(f -> logger.info("  {}", f));

            // ── 5. <set> + <if>: 부분 업데이트 ───────────────────────────
            logger.info("\n[3] <set> + <if>: null이 아닌 필드만 UPDATE");
            // film_id=1 의 일부 필드만 변경
            Film partialUpdate = new Film();
            partialUpdate.setFilmId((short) 1);
            partialUpdate.setRentalRate(new BigDecimal("5.99")); // 이 필드만 변경
            // title, description, length, rating 은 null → UPDATE 제외

            int updated = mapper.updatePartial(partialUpdate);
            session.commit();
            logger.info("  영향받은 행: {} (rental_rate 만 5.99로 변경)", updated);

            // 원복
            partialUpdate.setRentalRate(new BigDecimal("0.99"));
            mapper.updatePartial(partialUpdate);
            session.commit();
            logger.info("  원복 완료 (rental_rate → 0.99)");

            // ── 6. <foreach>: IN 절 처리 ──────────────────────────────────
            logger.info("\n[4] <foreach>: List<Short>를 IN 절로 변환");
            List<Short> ids = Arrays.asList((short) 1, (short) 5, (short) 10, (short) 50, (short) 100);
            logger.info("  조회할 film_id 목록: {}", ids);
            List<Film> byIds = mapper.selectByIds(ids);
            logger.info("  결과: {}건", byIds.size());
            byIds.forEach(f -> logger.info("  {}", f));

            // ── 7. <trim>: 커스텀 WHERE 절 구성 ──────────────────────────
            logger.info("\n[5] <trim>: prefix='WHERE' prefixOverrides='AND |OR '");
            FilmSearchCriteria trimCriteria = new FilmSearchCriteria();
            trimCriteria.setTitle("LOVE");
            trimCriteria.setMinLength((short) 100);
            logger.info("  title 에 'LOVE' 포함 AND length >= 100");
            List<Film> trimResult = mapper.searchWithTrim(trimCriteria);
            logger.info("  결과: {}건", trimResult.size());
            trimResult.stream().limit(3).forEach(f -> logger.info("  {}", f));
        }
    }
}
