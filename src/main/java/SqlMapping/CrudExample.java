package SqlMapping;

import SqlMapping.entity.Film;
import SqlMapping.mapper.FilmMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;

/**
 * SQL 매핑 - 기본 CRUD 예제
 *
 * 이 예제는 FilmMapper.xml 에 정의된 SQL 구문을 통해 film 테이블의
 * SELECT / INSERT / UPDATE / DELETE 를 수행하는 방법을 보여줍니다.
 *
 * 주요 학습 포인트:
 *  - <sql> 태그로 공통 컬럼 목록을 정의하고 <include> 로 재사용
 *  - <resultMap> 을 통한 명시적 컬럼-프로퍼티 매핑
 *  - useGeneratedKeys="true" 로 INSERT 후 자동 생성 PK 획득
 *  - parameterType 으로 엔티티 또는 기본 타입 전달
 *
 * 실행: ./gradlew runSqlMappingCrudExample
 */
public class CrudExample {

    private static final Logger logger = LoggerFactory.getLogger(CrudExample.class);

    public static void main(String[] args) throws IOException {
        Reader reader = Resources.getResourceAsReader("SqlMapping/mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

        logger.info("========================================");
        logger.info("   SQL 매핑 - 기본 CRUD 예제");
        logger.info("========================================");

        try (SqlSession session = factory.openSession()) {
            FilmMapper mapper = session.getMapper(FilmMapper.class);

            // ── 1. SELECT ALL ──────────────────────────────────────────────
            logger.info("\n[1] 전체 조회 (상위 10건)");
            List<Film> films = mapper.selectAll();
            logger.info("총 {}건 조회됨", films.size());
            films.forEach(f -> logger.info("  {}", f));

            // ── 2. SELECT BY ID ────────────────────────────────────────────
            logger.info("\n[2] ID로 단건 조회 (film_id=1)");
            Film film = mapper.selectById((short) 1);
            logger.info("  {}", film);

            // ── 3. INSERT ──────────────────────────────────────────────────
            logger.info("\n[3] 새 영화 INSERT");
            Film newFilm = new Film();
            newFilm.setTitle("MyBatis CRUD Example Film");
            newFilm.setDescription("MyBatis CRUD 예제를 위해 삽입된 테스트 영화입니다.");
            newFilm.setLanguageId((byte) 1);      // English (sakila DB에 반드시 존재)
            newFilm.setRentalDuration((byte) 3);
            newFilm.setRentalRate(new BigDecimal("2.99"));
            newFilm.setLength((short) 90);
            newFilm.setReplacementCost(new BigDecimal("14.99"));
            newFilm.setRating("PG");

            int inserted = mapper.insert(newFilm);
            session.commit();
            logger.info("  INSERT 완료. 영향받은 행: {}, 생성된 filmId: {}", inserted, newFilm.getFilmId());

            // ── 4. UPDATE ──────────────────────────────────────────────────
            logger.info("\n[4] 방금 삽입한 영화 UPDATE");
            newFilm.setTitle("MyBatis CRUD Example Film (Updated)");
            newFilm.setRentalRate(new BigDecimal("3.99"));
            newFilm.setRating("G");

            int updated = mapper.update(newFilm);
            session.commit();
            logger.info("  UPDATE 완료. 영향받은 행: {}", updated);

            // 업데이트 결과 확인
            Film updatedFilm = mapper.selectById(newFilm.getFilmId());
            logger.info("  업데이트 후 조회: {}", updatedFilm);

            // ── 5. DELETE ──────────────────────────────────────────────────
            logger.info("\n[5] 삽입한 영화 DELETE");
            int deleted = mapper.delete(newFilm.getFilmId());
            session.commit();
            logger.info("  DELETE 완료. 영향받은 행: {}", deleted);

            // 삭제 확인
            Film deletedFilm = mapper.selectById(newFilm.getFilmId());
            logger.info("  삭제 후 조회 결과: {} (null 이면 정상 삭제)", deletedFilm);
        }
    }
}
