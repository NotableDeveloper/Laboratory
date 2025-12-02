package SqlSession;

import org.apache.ibatis.annotations.Select;

public interface ConnectionTestMapper {
    /**
     * This query is used to test the database connection.
     * It should return 1 if the connection is successful.
     */
    @Select("SELECT 1")
    Integer selectOne();
}
