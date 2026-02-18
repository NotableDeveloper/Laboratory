package JavaApi;

import JavaApi.entity.Category;
import JavaApi.mapper.CategoryMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL 빌더(org.apache.ibatis.jdbc.SQL) 클래스 예제
 *
 * MyBatis 의 SQL 클래스를 사용하면 Java 코드에서 타입 안전하게
 * 동적 SQL 문자열을 조립할 수 있습니다.
 *
 * 주요 메서드:
 *   SELECT(columns)       - SELECT 컬럼 지정
 *   FROM(table)           - FROM 절
 *   WHERE(condition)      - WHERE 조건 추가 (여러 번 호출 시 AND 로 연결)
 *   OR()                  - 다음 WHERE 를 OR 로 연결
 *   ORDER_BY(column)      - ORDER BY 절
 *   GROUP_BY(column)      - GROUP BY 절
 *   HAVING(condition)     - HAVING 절
 *   INSERT_INTO(table)    - INSERT INTO
 *   VALUES(columns, vals) - VALUES 절
 *   UPDATE(table)         - UPDATE
 *   SET(assignment)       - SET 절
 *   DELETE_FROM(table)    - DELETE FROM
 *
 * @SelectProvider 와 결합하면 매퍼 인터페이스에서 동적 SQL을 사용할 수 있습니다.
 *
 * 실행: ./gradlew runSqlBuilderExample
 */
public class SqlBuilderExample {

    private static final Logger logger = LoggerFactory.getLogger(SqlBuilderExample.class);

    public static void main(String[] args) {
        // ── 1. SQL 클래스로 정적 SQL 문자열 생성 ──────────────────────────
        logger.info("========================================");
        logger.info("   SQL 빌더 클래스 예제");
        logger.info("========================================");

        logger.info("\n[1] SQL 클래스로 SELECT 문자열 생성");
        String selectSql = new SQL() {{
            SELECT("category_id, name, last_update");
            FROM("category");
            WHERE("category_id > #{minId}");
            ORDER_BY("name ASC");
        }}.toString();
        logger.info("  생성된 SQL:\n{}", selectSql);

        logger.info("\n[2] SQL 클래스로 INSERT 문자열 생성");
        String insertSql = new SQL() {{
            INSERT_INTO("category");
            VALUES("name", "#{name}");
        }}.toString();
        logger.info("  생성된 SQL:\n{}", insertSql);

        logger.info("\n[3] SQL 클래스로 UPDATE 문자열 생성");
        String updateSql = new SQL() {{
            UPDATE("category");
            SET("name = #{name}");
            WHERE("category_id = #{categoryId}");
        }}.toString();
        logger.info("  생성된 SQL:\n{}", updateSql);

        logger.info("\n[4] SQL 클래스로 DELETE 문자열 생성");
        String deleteSql = new SQL() {{
            DELETE_FROM("category");
            WHERE("category_id = #{categoryId}");
        }}.toString();
        logger.info("  생성된 SQL:\n{}", deleteSql);

        // ── 2. 조건에 따라 SQL이 달라지는 동적 SQL 빌더 ──────────────────
        logger.info("\n[5] 동적 SQL 생성: 조건에 따라 WHERE 절 구성 변경");

        String withName = buildDynamicSelect("Action", null, null);
        logger.info("  name='Action' 조건:\n{}", withName);

        String withRange = buildDynamicSelect(null, 3, 10);
        logger.info("  id 범위 3~10 조건:\n{}", withRange);

        String withBoth = buildDynamicSelect("Comedy", 1, 5);
        logger.info("  name='Comedy' AND id 1~5 조건:\n{}", withBoth);

        String noCondition = buildDynamicSelect(null, null, null);
        logger.info("  조건 없음 (전체 조회):\n{}", noCondition);

        // ── 3. @SelectProvider 와 결합하여 실제 DB 조회 ───────────────────
        logger.info("\n[6] @SelectProvider + CategorySqlBuilder 로 실제 DB 조회");
        SqlSessionFactory factory = buildSqlSessionFactory();

        try (SqlSession session = factory.openSession()) {
            CategoryMapper mapper = session.getMapper(CategoryMapper.class);

            // name 조건 없이 전체 조회
            Map<String, Object> params = new HashMap<>();
            List<Category> allCategories = mapper.search(params);
            logger.info("  조건 없음 → {}개 카테고리", allCategories.size());

            // name 에 'a' 가 포함된 카테고리 조회
            params.put("name", "a");
            List<Category> filtered = mapper.search(params);
            logger.info("  name LIKE '%a%' → {}개 카테고리:", filtered.size());
            filtered.forEach(c -> logger.info("  {}", c));

            // ID 범위로 조회
            Map<String, Object> rangeParams = new HashMap<>();
            rangeParams.put("minId", 5);
            rangeParams.put("maxId", 10);
            List<Category> byRange = mapper.selectByIdRange(rangeParams);
            logger.info("  ID 5~10 범위 → {}개 카테고리:", byRange.size());
            byRange.forEach(c -> logger.info("  {}", c));
        } catch (Exception e) {
            logger.error("DB 조회 중 오류 발생", e);
        }
    }

    /**
     * 파라미터 존재 여부에 따라 WHERE 절을 동적으로 구성하는 SQL 빌더 예제입니다.
     */
    private static String buildDynamicSelect(String name, Integer minId, Integer maxId) {
        SQL sql = new SQL();
        sql.SELECT("category_id, name, last_update");
        sql.FROM("category");

        if (name != null && !name.isEmpty()) {
            sql.WHERE("name LIKE CONCAT('%', #{name}, '%')");
        }
        if (minId != null) {
            sql.WHERE("category_id >= #{minId}");
        }
        if (maxId != null) {
            sql.WHERE("category_id <= #{maxId}");
        }

        sql.ORDER_BY("category_id");
        return sql.toString();
    }

    private static SqlSessionFactory buildSqlSessionFactory() {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername("simple-user");
        dataSource.setPassword("q1w2e3r4!");

        Environment environment = new Environment(
                "development",
                new JdbcTransactionFactory(),
                dataSource
        );

        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMapper(CategoryMapper.class);

        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
