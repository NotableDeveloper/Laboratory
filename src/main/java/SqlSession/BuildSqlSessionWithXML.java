package SqlSession;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * 이 클래스는 MyBatis 설정 파일(XML)을 사용하여 SqlSessionFactory를 빌드하는 방법을 보여주는 예제입니다.
 * 일반적으로 가장 많이 사용되는 방식이며, 설정 정보를 코드와 분리하여 관리할 수 있는 장점이 있습니다.
 */
public class BuildSqlSessionWithXML {
    public static void main(String[] args) {
        // MyBatis 설정 파일의 경로입니다.
        // `resources` 폴더 아래에 있는 파일을 클래스패스에서 찾습니다.
        String resource = "mybatis-config.xml";
        
        // SqlSessionFactory는 MyBatis의 핵심 객체로, SqlSession 인스턴스를 생성합니다.
        SqlSessionFactory sqlSessionFactory;

        try {
            // 설정 파일을 읽기 위한 Reader 객체를 생성합니다.
            // MyBatis는 리소스 파일을 쉽게 읽을 수 있는 `Resources` 유틸리티 클래스를 제공합니다.
            Reader reader = Resources.getResourceAsReader(resource);
            
            // SqlSessionFactoryBuilder를 사용하여 설정 파일(XML)로부터 SqlSessionFactory를 빌드합니다.
            // 이 빌더는 한 번 사용된 후에는 더 이상 필요하지 않으므로, 지역 변수로 선언하여 사용하는 것이 일반적입니다.
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            System.out.println("SqlSessionFactory build successful.");
        } catch (IOException e) {
            System.err.println("Error building SqlSessionFactory");
            e.printStackTrace();
            return; // 빌드 실패 시 프로그램 종료
        }

        // SqlSessionFactory를 통해 SqlSession을 엽니다.
        // SqlSession은 데이터베이스에 대한 실제 SQL 실행을 담당합니다.
        // try-with-resources 구문을 사용하여 세션이 자동으로 닫히도록 합니다.
        try (SqlSession session = sqlSessionFactory.openSession()) {
            System.out.println("SqlSession (XML config) opened successfully. Now testing connection...");
            
            // 등록된 매퍼 인터페이스의 구현체를 얻습니다.
            // 매퍼는 mybatis-config.xml 파일에 등록되어 있어야 합니다.
            ConnectionTestMapper mapper = session.getMapper(ConnectionTestMapper.class);
            
            // 매퍼에 정의된 메소드를 호출하여 SQL 쿼리를 실행합니다.
            Integer result = mapper.selectOne();
            
            // 쿼리 결과 확인
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