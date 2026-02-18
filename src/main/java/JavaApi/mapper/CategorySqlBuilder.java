package JavaApi.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * @SelectProvider / @InsertProvider 와 함께 사용하는 SQL 빌더 클래스입니다.
 *
 * MyBatis 의 SQL 클래스(org.apache.ibatis.jdbc.SQL)는 Java 코드로
 * 동적 SQL 문자열을 안전하게 조립할 수 있도록 도와줍니다.
 *
 * 특징:
 *  - 메서드 체이닝으로 SELECT/FROM/WHERE/ORDER_BY 등을 구성
 *  - 조건부 WHERE 절 추가가 간결
 *  - 최종적으로 toString() 으로 SQL 문자열을 반환
 */
public class CategorySqlBuilder {

    /**
     * name 파라미터가 있으면 LIKE 조건을 추가하는 동적 SELECT SQL을 생성합니다.
     *
     * @param params Map 으로 전달된 파라미터 ("name" 키 사용)
     * @return 완성된 SQL 문자열
     */
    public String buildSearchQuery(Map<String, Object> params) {
        return new SQL() {{
            SELECT("category_id, name, last_update");
            FROM("category");
            if (params.get("name") != null && !params.get("name").toString().isEmpty()) {
                WHERE("name LIKE CONCAT('%', #{name}, '%')");
            }
            ORDER_BY("category_id");
        }}.toString();
    }

    /**
     * minId 파라미터가 있으면 category_id 범위 조건을 추가합니다.
     *
     * @param params Map 으로 전달된 파라미터 ("minId" 키 사용)
     * @return 완성된 SQL 문자열
     */
    public String buildSelectByIdRange(Map<String, Object> params) {
        return new SQL() {{
            SELECT("category_id, name, last_update");
            FROM("category");
            if (params.get("minId") != null) {
                WHERE("category_id >= #{minId}");
            }
            if (params.get("maxId") != null) {
                WHERE("category_id <= #{maxId}");
            }
            ORDER_BY("category_id ASC");
        }}.toString();
    }
}
