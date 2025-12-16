package Mapper;

import Mapper.entity.Actor;
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
 * ActorMapper를 사용하여 데이터베이스에서 Actor 정보를 조회하는 예제 클래스입니다.
 */
public class ActorMapperExample {

    private static final Logger logger = LoggerFactory.getLogger(ActorMapperExample.class);

    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory = buildSqlSessionFactory();

        if (sqlSessionFactory == null) {
            logger.error("Failed to build SqlSessionFactory.");
            return;
        }

        try (SqlSession session = sqlSessionFactory.openSession()) {
            ActorMapper mapper = session.getMapper(ActorMapper.class);

            // 1. 단일 Actor 조회 테스트 (ID: 1)
            logger.info("Fetching Actor with ID 1...");
            Actor actor = mapper.selectActorById((short) 1);
            if (actor != null) {
                logger.info("Found Actor: {}", actor);
            } else {
                logger.warn("Actor with ID 1 not found.");
            }

            // 2. 전체 Actor 조회 테스트 (상위 5명만 출력)
            logger.info("Fetching all actors...");
            List<Actor> actors = mapper.selectAllActors();
            logger.info("Total actors found: {}", actors.size());
            
            logger.info("First 5 actors:");
            actors.stream().limit(5).forEach(a -> logger.info("{}", a));

        } catch (Exception e) {
            logger.error("Error during ActorMapper execution", e);
        }
    }

    private static SqlSessionFactory buildSqlSessionFactory() {
        try {
            PooledDataSource dataSource = new PooledDataSource();
            dataSource.setDriver("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
            dataSource.setUsername("simple-user");
            dataSource.setPassword("q1w2e3r4!");

            JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource);
            Configuration configuration = new Configuration(environment);
            
            // mapUnderscoreToCamelCase 설정은 Mapper 인터페이스의 @Select 쿼리에서 별칭(as)을 사용했으므로
            // 필수적인 것은 아니지만, 일관성을 위해 true로 설정할 수 있습니다.
            // 여기서는 쿼리 내에서 alias를 주었으므로 false여도 동작하지만, 관례상 true로 둡니다.
            configuration.setMapUnderscoreToCamelCase(true);
            
            // Mapper 등록
            configuration.addMapper(ActorMapper.class);

            return new SqlSessionFactoryBuilder().build(configuration);
        } catch (Exception e) {
            logger.error("Error building SqlSessionFactory", e);
            return null;
        }
    }
}
