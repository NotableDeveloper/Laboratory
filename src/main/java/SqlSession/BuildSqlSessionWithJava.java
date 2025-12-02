package SqlSession;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class BuildSqlSessionWithJava {

    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory;

        try {
            // Configure DataSource
            PooledDataSource dataSource = new PooledDataSource();
            dataSource.setDriver("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
            dataSource.setUsername("simple-user");
            dataSource.setPassword("q1w2e3r4!");

            // Configure TransactionFactory
            JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();

            // Create Environment
            Environment environment = new Environment("development", transactionFactory, dataSource);

            // Create Configuration
            Configuration configuration = new Configuration(environment);
            
            // MyBatis `mapUnderscoreToCamelCase` setting
            configuration.setMapUnderscoreToCamelCase(true);
            
            // Add mappers
            configuration.addMapper(ConnectionTestMapper.class);


            // Build SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            System.out.println("SqlSessionFactory (Java config) build successful.");

        } catch (Exception e) {
            System.err.println("Error building SqlSessionFactory with Java config");
            e.printStackTrace();
            return;
        }

        // Open SqlSession and test the connection
        try (SqlSession session = sqlSessionFactory.openSession()) {
            System.out.println("SqlSession (Java config) opened successfully. Now testing connection...");
            ConnectionTestMapper mapper = session.getMapper(ConnectionTestMapper.class);
            Integer result = mapper.selectOne();
            if (result != null && result == 1) {
                System.out.println("Database connection test successful. Query returned: " + result);
            } else {
                System.err.println("Database connection test failed. Query returned: " + result);
            }
        } catch (Exception e) {
            System.err.println("Error with SqlSession (Java config) or database query.");
            e.printStackTrace();
        }
    }
}
