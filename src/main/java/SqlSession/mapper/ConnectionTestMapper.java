package SqlSession.mapper;

import org.apache.ibatis.annotations.Select;

/**
 * 이 인터페이스는 MyBatis의 매퍼(Mapper)입니다.
 * 매퍼는 SQL 쿼리를 메소드와 매핑하여 데이터베이스와의 상호작용을 정의합니다.
 * 인터페이스로 선언하며, MyBatis가 실행 시점에 이 인터페이스를 구현한 프록시(Proxy) 객체를 생성합니다.
 */
public interface ConnectionTestMapper {
    /**
     * 이 메소드는 데이터베이스 연결을 테스트하기 위한 간단한 쿼리를 실행합니다.
     * `SELECT 1` 쿼리는 대부분의 데이터베이스에서 숫자 1을 반환하며,
     * 쿼리가 성공적으로 실행되면 데이터베이스 연결이 정상적임을 의미합니다.
     *
     * @return 쿼리가 성공하면 1을 반환합니다.
     *
     * @Select 어노테이션은 해당 메소드가 실행할 SQL 쿼리를 직접 명시하는 방법입니다.
     * 복잡한 쿼리는 XML 파일에 분리하여 작성하는 것이 더 일반적입니다.
     */
    @Select("SELECT 1")
    Integer selectOne();
}