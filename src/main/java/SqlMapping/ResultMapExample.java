package SqlMapping;

import SqlMapping.entity.ActorWithFilms;
import SqlMapping.entity.FilmDetail;
import SqlMapping.entity.FilmSummary;
import SqlMapping.mapper.FilmMapper;
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
 * SQL 매핑 - ResultMap 심화 예제
 *
 * 이 예제는 MyBatis resultMap 의 고급 기능을 보여줍니다:
 *  - <association>: 1:1 관계 (film + language)
 *  - <collection>:  1:N 관계 (actor + 출연 영화 목록)
 *  - <discriminator>: 컬럼 값에 따라 다른 Java 타입으로 매핑
 *
 * 실행: ./gradlew runSqlMappingResultMapExample
 */
public class ResultMapExample {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapExample.class);

    public static void main(String[] args) throws IOException {
        Reader reader = Resources.getResourceAsReader("SqlMapping/mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

        logger.info("========================================");
        logger.info("   SQL 매핑 - ResultMap 심화 예제");
        logger.info("========================================");

        try (SqlSession session = factory.openSession()) {
            FilmMapper mapper = session.getMapper(FilmMapper.class);

            // ── 1. <association>: 1:1 관계 매핑 ──────────────────────────
            logger.info("\n[1] <association> 예제: film + language 1:1 조인");
            logger.info("    film 내부에 Language 객체가 중첩 매핑됩니다.");
            FilmDetail detail = mapper.selectDetailById((short) 1);
            logger.info("  FilmDetail: {}", detail);
            if (detail.getLanguage() != null) {
                logger.info("  → 언어 정보: {}", detail.getLanguage());
            }

            // ── 2. <collection>: 1:N 관계 매핑 ───────────────────────────
            logger.info("\n[2] <collection> 예제: actor + 출연 영화 목록 1:N 조인");
            logger.info("    Actor 내부에 List<Film>이 자동으로 구성됩니다.");
            ActorWithFilms actor = mapper.selectActorWithFilms((short) 1);
            logger.info("  배우: {} {}", actor.getFirstName(), actor.getLastName());
            logger.info("  출연 영화 수: {}편", actor.getFilms() != null ? actor.getFilms().size() : 0);
            if (actor.getFilms() != null) {
                actor.getFilms().stream().limit(5).forEach(f ->
                        logger.info("    - [{}] {} ({})", f.getFilmId(), f.getTitle(), f.getRating()));
                if (actor.getFilms().size() > 5) {
                    logger.info("    ... 외 {}편 더", actor.getFilms().size() - 5);
                }
            }

            // ── 3. <discriminator>: 단건 조회 ─────────────────────────────
            logger.info("\n[3] <discriminator> 예제: rating 값에 따라 다른 Java 타입 반환");
            logger.info("    G/PG → FamilyFilm, PG-13/R/NC-17 → RestrictedFilm");

            short[] testFilmIds = {1, 12, 15, 20};
            for (short filmId : testFilmIds) {
                FilmSummary summary = mapper.selectSummaryById(filmId);
                if (summary != null) {
                    logger.info("  filmId={} | Java타입={} | 콘텐츠유형={}",
                            filmId,
                            summary.getClass().getSimpleName(),
                            summary.getContentType());
                }
            }

            // ── 4. <discriminator>: 목록 조회 ────────────────────────────
            logger.info("\n[4] <discriminator> 목록 조회: G/PG/R/NC-17 등급 혼합");
            List<FilmSummary> summaries = mapper.selectMixedRatings();
            summaries.forEach(s -> logger.info("  {}", s));
        }
    }
}
