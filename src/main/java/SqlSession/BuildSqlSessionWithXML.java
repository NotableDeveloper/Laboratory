package SqlSession;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class BuildSqlSessionWithXML {
    public static void main(String[] args) {
        String resource = "mybatis-config.xml";
        SqlSessionFactory sqlSessionFactory;

        try {
            Reader reader = Resources.getResourceAsReader(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            System.out.println("SqlSessionFactory build successful.");
        } catch (IOException e) {
            System.err.println("Error building SqlSessionFactory");
            e.printStackTrace();
            return;
        }

        // From the SqlSessionFactory, we can get a SqlSession and test the connection
        try (SqlSession session = sqlSessionFactory.openSession()) {
            System.out.println("SqlSession (XML config) opened successfully. Now testing connection...");
            ConnectionTestMapper mapper = session.getMapper(ConnectionTestMapper.class);
            Integer result = mapper.selectOne();
            if (result != null && result == 1) {
                System.out.println("Database connection test successful. Query returned: " + result);
            } else {
                System.err.println("Database connection test failed. Query returned: " + result);
            }
        } catch (Exception e) {
            System.err.println("Error with SqlSession (XML config) or database query.");
            e.printStackTrace();
        }
    }
}
