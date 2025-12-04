package SqlSession.example;

import SqlSession.mapper.ConnectionTestMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * 이 클래스는 MyBatis의 핵심 컴포넌트인 SqlSessionFactoryBuilder, SqlSessionFactory, SqlSession의
 * 생명 주기를 이해하기 위한 예제 코드입니다.
 * 각 컴포넌트의 역할과 생성, 사용, 소멸 시점을 설명합니다.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 이 클래스는 MyBatis의 핵심 컴포넌트인 SqlSessionFactoryBuilder, SqlSessionFactory, SqlSession의
 * 생명 주기를 이해하기 위한 예제 코드입니다.
 * 각 컴포넌트의 역할과 생성, 사용, 소멸 시점을 설명합니다.
 */
public class SqlSessionLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(SqlSessionLifecycle.class);

    public static void main(String[] args) {
        // 1. SqlSessionFactoryBuilder의 생명 주기:
        // SqlSessionFactoryBuilder는 SqlSessionFactory를 빌드하는 데 사용되는 임시(Transient) 객체입니다.
        // SqlSessionFactory를 생성하고 나면 더 이상 필요 없으므로, 빌드 작업이 완료되면 가비지 컬렉션의 대상이 됩니다.
        // 애플리케이션 시작 시 한 번만 사용되는 것이 일반적입니다.
        String resource = "mybatis-config.xml";
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(resource);
        } catch (IOException e) {
            logger.error("mybatis-config.xml 파일을 읽는 중 오류 발생: " + e.getMessage(), e);
            return;
        }

        // SqlSessionFactoryBuilder 객체를 생성합니다.
        logger.info("1. SqlSessionFactoryBuilder 객체를 생성합니다.");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();

        // 2. SqlSessionFactory의 생명 주기:
        // SqlSessionFactory는 애플리케이션 전체에서 유일하게 존재해야 하는 싱글톤(Singleton) 객체입니다.
        // 한 번 생성되면 애플리케이션이 실행되는 동안 계속 사용되며, 여러 스레드에서 공유될 수 있도록 설계되었습니다.
        // 데이터베이스 커넥션 풀과 같은 리소스를 관리하며, SqlSession을 생성하는 역할을 합니다.
        logger.info("2. SqlSessionFactoryBuilder를 사용하여 SqlSessionFactory를 빌드합니다.");
        SqlSessionFactory sqlSessionFactory = builder.build(reader);
        logger.info("SqlSessionFactory가 성공적으로 빌드되었습니다.");

        // SqlSessionFactoryBuilder는 SqlSessionFactory를 빌드한 후 더 이상 필요하지 않습니다.
        // 명시적으로 null을 할당하여 가비지 컬렉션이 더 빨리 이루어지도록 할 수 있습니다.
        builder = null; // SqlSessionFactoryBuilder의 생명 주기 종료
        logger.info("SqlSessionFactoryBuilder 객체는 이제 사용되지 않습니다 (null 처리).");


        // 3. SqlSession의 생명 주기:
        // SqlSession은 데이터베이스와의 단일 상호작용(요청 또는 작업 단위)을 나타내는 짧은 수명의 객체입니다.
        // 각 데이터베이스 작업(예: 쿼리 실행)마다 새로운 SqlSession을 열고, 작업이 완료되면 반드시 닫아야 합니다.
        // 이는 데이터베이스 커넥션, 캐시, 트랜잭션 등과 관련된 리소스를 올바르게 관리하기 위함입니다.
        // 일반적으로 웹 애플리케이션에서는 요청(Request)당 하나의 SqlSession을 사용합니다.

        logger.info("3. SqlSessionFactory로부터 SqlSession을 엽니다.");
        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 생성 시작
        try {
            logger.info("SqlSession이 성공적으로 열렸습니다.");

            // SqlSession을 사용하여 데이터베이스 작업 수행 (예: 매퍼 인터페이스 사용)
            ConnectionTestMapper mapper = session.getMapper(ConnectionTestMapper.class);
            Integer result = mapper.selectOne();

            logger.info("데이터베이스 쿼리 결과: " + result);

            // 데이터베이스 작업이 성공적으로 완료되면 트랜잭션을 커밋합니다 (자동 커밋이 아닌 경우).
            // 여기서는 select 쿼리이므로 커밋은 필요 없지만, insert/update/delete 시에는 필수입니다.
            // session.commit();

        } catch (Exception e) {
            logger.error("SqlSession 사용 중 오류 발생: " + e.getMessage(), e);
            // 오류 발생 시 트랜잭션을 롤백합니다.
            // session.rollback();
        } finally {
            // SqlSession은 반드시 닫아야 합니다 (가장 중요!).
            // try-with-resources 구문을 사용하면 자동으로 닫아주므로 더 안전합니다.
            session.close(); // SqlSession의 생명 주기 종료
            logger.info("SqlSession이 성공적으로 닫혔습니다.");
        }

        logger.info("SqlSession의 다른 예시 (try-with-resources 사용):");
        try (SqlSession session2 = sqlSessionFactory.openSession()) { // SqlSession 생성 시작 (try-with-resources)
            logger.info("두 번째 SqlSession이 성공적으로 열렸습니다.");
            ConnectionTestMapper mapper2 = session2.getMapper(ConnectionTestMapper.class);
            Integer result2 = mapper2.selectOne();
            logger.info("두 번째 데이터베이스 쿼리 결과: " + result2);
        } catch (Exception e) {
            logger.error("두 번째 SqlSession 사용 중 오류 발생: " + e.getMessage(), e);
        } // try-with-resources 블록을 벗어나면 session2는 자동으로 close() 됩니다.
        logger.info("두 번째 SqlSession이 자동으로 닫혔습니다 (try-with-resources).");

        // 애플리케이션이 종료될 때까지 SqlSessionFactory는 계속 유효합니다.
        // 일반적으로 웹 서버 종료와 같은 애플리케이션 종료 시점에 SqlSessionFactory 관련 리소스도 해제됩니다.
        logger.info("애플리케이션 종료. SqlSessionFactory는 애플리케이션 생명 주기 동안 유지됩니다.");
    }
}
