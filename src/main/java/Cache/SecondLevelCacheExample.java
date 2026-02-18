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
import java.util.List;

/**
 * 2차 캐시 (Second-Level Cache / Namespace-Level Cache) 예제
 *
 * 2차 캐시의 특징:
 *  - 매퍼 네임스페이스(Namespace) 범위로 동작합니다.
 *  - SqlSession 이 닫혀도 캐시가 유지됩니다.
 *  - 다른 SqlSession 이 같은 네임스페이스의 같은 쿼리를 실행하면 캐시를 재사용합니다.
 *  - 활성화 조건:
 *      1) mybatis-config.xml 에 <setting name="cacheEnabled" value="true"/> (기본값 true)
 *      2) 매퍼 XML 에 <cache/> 선언
 *      3) 결과 객체가 java.io.Serializable 구현
 *  - 캐시에 저장하려면 SqlSession 이 commit 또는 close 되어야 합니다.
 *  - 같은 네임스페이스에서 INSERT/UPDATE/DELETE 가 실행되면 캐시가 초기화됩니다.
 *
 * 실행: ./gradlew runSecondLevelCacheExample
 */
public class SecondLevelCacheExample {

    private static final Logger logger = LoggerFactory.getLogger(SecondLevelCacheExample.class);

    public static void main(String[] args) throws IOException {
        Reader reader = Resources.getResourceAsReader("Cache/mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

        logger.info("========================================");
        logger.info("   2차 캐시 (Second-Level Cache) 예제");
        logger.info("========================================");
        logger.info("※ SQL 로그가 출력되면 DB 조회, 'Cache Hit' 로그가 보이면 캐시 히트입니다.");

        // ── 1. 첫 번째 SqlSession: DB 조회 후 세션 종료 → 2차 캐시에 저장 ──
        logger.info("\n[1] SqlSession-1: 첫 번째 조회 → DB 쿼리 실행 후 close()");
        logger.info("    → close() 시점에 결과가 2차 캐시에 저장됨");
        try (SqlSession session1 = factory.openSession()) {
            FilmCacheMapper mapper = session1.getMapper(FilmCacheMapper.class);
            Film film = mapper.selectById((short) 1);
            logger.info("  SqlSession-1 조회 결과: {}", film);
        }
        // session1.close() 되면 2차 캐시에 결과 저장

        // ── 2. 두 번째 SqlSession: 2차 캐시 히트 ─────────────────────────
        logger.info("\n[2] SqlSession-2: 동일 쿼리 → 2차 캐시에서 반환 (SQL 로그 없음)");
        try (SqlSession session2 = factory.openSession()) {
            FilmCacheMapper mapper = session2.getMapper(FilmCacheMapper.class);
            Film film = mapper.selectById((short) 1);
            logger.info("  SqlSession-2 조회 결과: {}", film);
        }

        // ── 3. 목록 조회도 2차 캐시에 저장됨 ─────────────────────────────
        logger.info("\n[3] 목록 쿼리도 2차 캐시에 저장됩니다");
        logger.info("    SqlSession-3: selectByRating('G') 첫 번째 조회");
        try (SqlSession session3 = factory.openSession()) {
            FilmCacheMapper mapper = session3.getMapper(FilmCacheMapper.class);
            List<Film> films = mapper.selectByRating("G");
            logger.info("  SqlSession-3 결과: {}건", films.size());
        }

        logger.info("    SqlSession-4: selectByRating('G') 두 번째 조회 → 캐시 히트");
        try (SqlSession session4 = factory.openSession()) {
            FilmCacheMapper mapper = session4.getMapper(FilmCacheMapper.class);
            List<Film> films = mapper.selectByRating("G");
            logger.info("  SqlSession-4 결과: {}건 (캐시에서 반환)", films.size());
        }

        // ── 4. 2차 캐시는 직렬화/역직렬화 → 반환 객체는 서로 다른 인스턴스 ──
        logger.info("\n[4] 2차 캐시는 직렬화 방식 → 반환 객체는 서로 다른 인스턴스");
        logger.info("    (readOnly=true 설정 시 동일 참조 반환 가능하지만 변경 금지)");
        Film f1, f2;
        try (SqlSession s1 = factory.openSession()) {
            f1 = s1.getMapper(FilmCacheMapper.class).selectById((short) 2);
        }
        try (SqlSession s2 = factory.openSession()) {
            f2 = s2.getMapper(FilmCacheMapper.class).selectById((short) 2);
        }
        logger.info("  f1 == f2 (동일 참조): {} (false: 역직렬화로 새 객체 생성)", (f1 == f2));
        logger.info("  f1.equals(f2) (값 동등): {}", f1.toString().equals(f2.toString()));
    }
}
