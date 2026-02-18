package Cache;

import Cache.entity.Film;
import Cache.mapper.FilmCacheMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * 1차 캐시 (Local Cache / SqlSession-Level Cache) 예제
 *
 * 1차 캐시의 특징:
 *  - SqlSession 범위로 동작합니다.
 *  - 같은 SqlSession 에서 동일한 SQL + 동일한 파라미터로 조회하면
 *    두 번째부터는 DB 쿼리를 실행하지 않고 캐시에서 반환합니다.
 *  - SqlSession 이 닫히면 캐시도 함께 소멸합니다.
 *  - session.clearCache() 를 호출하면 캐시를 명시적으로 초기화할 수 있습니다.
 *  - 같은 SqlSession 에서 INSERT/UPDATE/DELETE 가 실행되면 캐시가 자동으로 초기화됩니다.
 *  - 별도 설정 없이 항상 활성화되어 있습니다.
 *
 * 실행: ./gradlew runFirstLevelCacheExample
 */
public class FirstLevelCacheExample {

    private static final Logger logger = LoggerFactory.getLogger(FirstLevelCacheExample.class);

    public static void main(String[] args) throws IOException {
        Reader reader = Resources.getResourceAsReader("Cache/mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

        logger.info("========================================");
        logger.info("   1차 캐시 (Local Cache) 예제");
        logger.info("========================================");
        logger.info("※ SQL 로그가 출력되면 DB 조회, 출력되지 않으면 캐시 히트입니다.");

        try (SqlSession session = factory.openSession()) {
            FilmCacheMapper mapper = session.getMapper(FilmCacheMapper.class);

            // ── 1. 첫 번째 조회: DB 조회 발생 ────────────────────────────
            logger.info("\n[1] 첫 번째 조회 → DB 쿼리 실행됨 (SQL 로그 확인)");
            Film film1 = mapper.selectById((short) 1);
            logger.info("  결과: {}", film1);

            // ── 2. 두 번째 조회: 1차 캐시 히트 ───────────────────────────
            logger.info("\n[2] 두 번째 조회 (같은 SqlSession, 같은 파라미터)");
            logger.info("    → 1차 캐시에서 반환 (SQL 로그 없음)");
            Film film2 = mapper.selectById((short) 1);
            logger.info("  결과: {}", film2);
            logger.info("  동일 객체 참조 여부 (film1 == film2): {}", (film1 == film2));

            // ── 3. 캐시 초기화 후 재조회 ──────────────────────────────────
            logger.info("\n[3] session.clearCache() 호출 후 재조회 → DB 쿼리 재실행");
            session.clearCache();
            Film film3 = mapper.selectById((short) 1);
            logger.info("  결과: {}", film3);
            logger.info("  동일 객체 참조 여부 (film1 == film3): {} (캐시 초기화 후 새 객체)", (film1 == film3));

            // ── 4. 다른 파라미터: 각각 DB 조회 ───────────────────────────
            logger.info("\n[4] 다른 film_id 조회 → 파라미터가 다르므로 별개의 캐시 키");
            Film filmA = mapper.selectById((short) 2);
            Film filmB = mapper.selectById((short) 2); // 두 번째는 캐시 히트
            logger.info("  film_id=2 두 번 조회 → 동일 객체: {}", (filmA == filmB));
        }
        // SqlSession 이 닫히면 1차 캐시도 소멸

        logger.info("\n[5] 새 SqlSession 을 열면 이전 1차 캐시는 사용 불가");
        logger.info("    (같은 데이터를 조회하려면 다시 DB 쿼리 실행 필요)");
        try (SqlSession newSession = factory.openSession()) {
            FilmCacheMapper mapper = newSession.getMapper(FilmCacheMapper.class);
            Film filmInNewSession = mapper.selectById((short) 1);
            logger.info("  새 세션에서 조회: {} → SQL 로그 확인", filmInNewSession);
        }
    }
}
