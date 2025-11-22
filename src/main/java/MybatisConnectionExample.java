import mapper.TimeMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

public class MybatisConnectionExample {

    public static void main(String[] args) {
        String resource = "mybatis-config.xml";
        SqlSessionFactory sqlSessionFactory = null;
        SqlSession session = null;

        try {
            // 1. Load the configuration file
            InputStream inputStream = Resources.getResourceAsStream(resource);

            // 2. Build the SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            // 3. Open a SqlSession
            session = sqlSessionFactory.openSession();

            // 4. Get the mapper
            TimeMapper mapper = session.getMapper(TimeMapper.class);

            // 5. Execute the query
            String currentTime = mapper.selectTime();

            // 6. Print the result
            System.out.println("Successfully connected to the database.");
            System.out.println("Current database time: " + currentTime);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
