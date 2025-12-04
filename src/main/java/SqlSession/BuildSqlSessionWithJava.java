package SqlSession;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * 이 클래스는 MyBatis의 설정 파일(XML)을 사용하지 않고,
 * 순수 Java 코드를 통해 SqlSessionFactory를 빌드하는 방법을 보여주는 예제입니다.
 * Java 코드로 직접 MyBatis 설정을 구성하면, XML 파일 없이 애플리케이션을 실행할 수 있습니다.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildSqlSessionWithJava {

    private static final Logger logger = LoggerFactory.getLogger(BuildSqlSessionWithJava.class);

    public static void main(String[] args) {
        // SqlSessionFactory는 MyBatis의 핵심 객체로, SqlSession 인스턴스를 생성합니다.
        // 애플리케이션 스코프에서 한 번만 생성하여 관리하는 것이 일반적입니다.
        SqlSessionFactory sqlSessionFactory;

        try {
            // 데이터베이스 연결을 위한 DataSource를 설정합니다.
            // PooledDataSource는 MyBatis에서 제공하는 커넥션 풀 구현체입니다.
            PooledDataSource dataSource = new PooledDataSource();
            dataSource.setDriver("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/sakila"); // 사용하는 DB URL
            dataSource.setUsername("simple-user"); // DB 사용자 이름
            dataSource.setPassword("q1w2e3r4!"); // DB 비밀번호

            // 트랜잭션 관리를 위한 TransactionFactory를 설정합니다.
            // JdbcTransactionFactory는 JDBC의 commit/rollback을 이용한 간단한 트랜잭션 관리자입니다.
            JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();

            // MyBatis 실행 환경(Environment)을 설정합니다.
            // Environment는 트랜잭션 팩토리와 데이터 소스를 포함합니다.
            // "development"는 이 환경에 대한 식별자(ID)입니다.
            Environment environment = new Environment("development", transactionFactory, dataSource);

            // MyBatis의 주 설정 객체인 Configuration을 생성합니다.
            // 이 객체에 다양한 설정을 추가할 수 있습니다.
            Configuration configuration = new Configuration(environment);
            
            // 데이터베이스의 스네이크 케이스(snake_case) 컬럼명을
            // Java의 카멜 케이스(camelCase) 프로퍼티명으로 자동 매핑해주는 설정입니다.
            // 예: "first_name" -> "firstName"
            configuration.setMapUnderscoreToCamelCase(true);

            // SQL 쿼리를 정의한 매퍼(Mapper) 인터페이스를 등록합니다.
            // MyBatis는 이 인터페이스를 구현한 프록시 객체를 생성하여 SQL 실행을 처리합니다.
            configuration.addMapper(ConnectionTestMapper.class);

            // 설정이 완료된 Configuration 객체를 사용하여 SqlSessionFactory를 빌드합니다.
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            logger.info("SqlSessionFactory (Java config) build successful.");

        } catch (Exception e) {
            logger.error("Error building SqlSessionFactory with Java config");
            return; // 빌드 실패 시 프로그램 종료
        }

        // SqlSessionFactory를 통해 SqlSession을 엽니다.
        // SqlSession은 데이터베이스에 대한 실제 SQL 실행을 담당합니다.
        // try-with-resources 구문을 사용하여 세션이 자동으로 닫히도록 합니다.
        try (SqlSession session = sqlSessionFactory.openSession()) {
            logger.info("SqlSession (Java config) opened successfully. Now testing connection...");

            // 등록된 매퍼 인터페이스의 구현체를 얻습니다.
            ConnectionTestMapper mapper = session.getMapper(ConnectionTestMapper.class);
            
            // 매퍼에 정의된 메소드를 호출하여 SQL 쿼리를 실행합니다.
            Integer result = mapper.selectOne();
            
            // 쿼리 결과 확인
            if (result != null && result == 1) {
                logger.info("Database connection test successful. Query returned: " + result);
            } else {
                logger.error("Database connection test failed. Query returned: " + result);
            }
        } catch (Exception e) {
            logger.error("Error with SqlSession (Java config) or database query.");
        }
    }
}