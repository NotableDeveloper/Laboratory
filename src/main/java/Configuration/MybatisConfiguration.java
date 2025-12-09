package Configuration;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

import java.io.IOException;
import java.io.Reader;

/**
 * 이 클래스는 MyBatis의 Configuration 객체를 로드하고,
 * 설정된 주요 값들을 확인하여 INFO 레벨의 로그로 출력하는 예제입니다.
 * 이를 통해 mybatis-config.xml 파일이 올바르게 파싱되고 적용되었는지 검증할 수 있습니다.
 */
public class MybatisConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MybatisConfiguration.class);

    public static void main(String[] args) {
        String resource = "mybatis-config.xml";
        SqlSessionFactory sqlSessionFactory;

        try {
            Reader reader = Resources.getResourceAsReader(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            logger.info("SqlSessionFactory build successful. Reading configuration...");

            Configuration config = sqlSessionFactory.getConfiguration();

            // 주요 설정 값들을 로그로 출력
            logConfigurationDetails(config);

        } catch (IOException e) {
            logger.error("Error building SqlSessionFactory or reading configuration", e);
        }
    }

    private static void logConfigurationDetails(Configuration config) {
        if (config == null) {
            logger.error("Configuration object is null.");
            return;
        }

        logger.info("--- MyBatis Configuration Details ---");

        // Settings
        logger.info("[Settings]");
        logger.info("  mapUnderscoreToCamelCase: {}", config.isMapUnderscoreToCamelCase());
        logger.info("  cacheEnabled: {}", config.isCacheEnabled());
        logger.info("  defaultStatementTimeout: {} seconds", config.getDefaultStatementTimeout());
        logger.info("  defaultFetchSize: {}", config.getDefaultFetchSize());
        logger.info("  logImpl: {}", config.getLogImpl());


        // Environment
        if (config.getEnvironment() != null) {
            logger.info("[Environment]");
            logger.info("  ID: {}", config.getEnvironment().getId());
            logger.info("  TransactionManager: {}", config.getEnvironment().getTransactionFactory().getClass().getSimpleName());
            logger.info("  DataSource: {}", config.getEnvironment().getDataSource().getClass().getSimpleName());

            // Log DataSource properties
            if (config.getEnvironment().getDataSource() instanceof PooledDataSource) {
                // PooledDataSource extends UnpooledDataSource, so we can cast and access properties directly
                PooledDataSource pooledDs = (PooledDataSource) config.getEnvironment().getDataSource();
                logger.info("[DataSource Properties]");
                logger.info("  Driver: {}", pooledDs.getDriver());
                logger.info("  URL: {}", pooledDs.getUrl());
                logger.info("  Username: {}", pooledDs.getUsername());
                // Password should generally not be logged, but for demonstration, we'll include it.
                logger.info("  Password: {}", pooledDs.getPassword());
            } else if (config.getEnvironment().getDataSource() instanceof UnpooledDataSource) {
                // This block handles pure UnpooledDataSource (if not wrapped by PooledDataSource)
                UnpooledDataSource ds = (UnpooledDataSource) config.getEnvironment().getDataSource();
                logger.info("[DataSource Properties]");
                logger.info("  Driver: {}", ds.getDriver());
                logger.info("  URL: {}", ds.getUrl());
                logger.info("  Username: {}", ds.getUsername());
                // Password should generally not be logged, but for demonstration, we'll include it.
                logger.info("  Password: {}", ds.getPassword());
            }
            else {
                logger.info("[DataSource Properties] (specific properties not available for this DataSource type)");
            }
        } else {
            logger.warn("[Environment] is not configured.");
        }

        // Mappers
        logger.info("[Mappers]");
        if (config.getMapperRegistry().getMappers().isEmpty()) {
            logger.info("  No mappers are registered.");
        } else {
            config.getMapperRegistry().getMappers().forEach(mapper ->
                logger.info("  Registered Mapper: {}", mapper.getName())
            );
        }

        logger.info("-------------------------------------");
    }
}
