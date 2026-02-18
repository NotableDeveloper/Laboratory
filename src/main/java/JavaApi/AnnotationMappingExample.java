package JavaApi;

import JavaApi.entity.Category;
import JavaApi.mapper.CategoryMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 애노테이션 기반 SQL 매핑 예제
 *
 * XML 설정 파일 없이 Java 코드만으로 SqlSessionFactory를 구성하고
 * @Select / @Insert / @Update / @Delete / @Results 애노테이션으로
 * SQL을 직접 매퍼 인터페이스에 정의하는 방법을 보여줍니다.
 *
 * 애노테이션 매핑의 장점:
 *  - 간단한 CRUD는 XML 파일 없이 한 파일에서 관리
 *  - IDE 자동완성과 리팩터링 지원
 *
 * 애노테이션 매핑의 한계:
 *  - 복잡한 resultMap (association, collection, discriminator)은 XML이 더 적합
 *  - 동적 SQL 은 @SelectProvider + SQL 빌더 클래스가 필요
 *
 * 실행: ./gradlew runAnnotationMappingExample
 */
public class AnnotationMappingExample {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationMappingExample.class);

    public static void main(String[] args) {
        SqlSessionFactory factory = buildSqlSessionFactory();

        logger.info("========================================");
        logger.info("   애노테이션 기반 SQL 매핑 예제");
        logger.info("========================================");

        try (SqlSession session = factory.openSession()) {
            CategoryMapper mapper = session.getMapper(CategoryMapper.class);

            // ── 1. SELECT ALL ──────────────────────────────────────────────
            logger.info("\n[1] @Select + @ResultMap: 전체 카테고리 조회");
            List<Category> categories = mapper.selectAll();
            logger.info("  총 {}개 카테고리:", categories.size());
            categories.forEach(c -> logger.info("  {}", c));

            // ── 2. SELECT BY ID ────────────────────────────────────────────
            logger.info("\n[2] @Select + @Results: ID로 단건 조회");
            Category cat = mapper.selectById((byte) 1);
            logger.info("  category_id=1: {}", cat);

            // ── 3. INSERT ──────────────────────────────────────────────────
            logger.info("\n[3] @Insert + @Options(useGeneratedKeys=true): 새 카테고리 삽입");
            Category newCategory = new Category();
            newCategory.setName("MyBatis Test Category");

            int inserted = mapper.insert(newCategory);
            session.commit();
            logger.info("  INSERT 완료. 영향받은 행: {}, 생성된 categoryId: {}",
                    inserted, newCategory.getCategoryId());

            // ── 4. UPDATE ──────────────────────────────────────────────────
            logger.info("\n[4] @Update: 카테고리 이름 변경");
            newCategory.setName("MyBatis Test Category (Updated)");
            int updated = mapper.update(newCategory);
            session.commit();
            logger.info("  UPDATE 완료. 영향받은 행: {}", updated);

            Category updatedCat = mapper.selectById(newCategory.getCategoryId());
            logger.info("  변경 후 조회: {}", updatedCat);

            // ── 5. DELETE ──────────────────────────────────────────────────
            logger.info("\n[5] @Delete: 카테고리 삭제");
            int deleted = mapper.delete(newCategory.getCategoryId());
            session.commit();
            logger.info("  DELETE 완료. 영향받은 행: {}", deleted);

            Category deletedCat = mapper.selectById(newCategory.getCategoryId());
            logger.info("  삭제 후 조회: {} (null 이면 정상)", deletedCat);
        } catch (Exception e) {
            logger.error("예제 실행 중 오류 발생", e);
        }
    }

    /**
     * Java API 로 SqlSessionFactory 를 구성합니다.
     * XML 설정 파일 없이 Configuration 객체를 직접 생성합니다.
     */
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

        // 애노테이션 기반 매퍼를 직접 등록
        configuration.addMapper(CategoryMapper.class);

        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
