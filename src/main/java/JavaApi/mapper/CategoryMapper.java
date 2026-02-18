package JavaApi.mapper;

import JavaApi.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 애노테이션 기반 SQL 매핑 예제 매퍼 인터페이스입니다.
 *
 * XML 파일 없이 Java 애노테이션만으로 SQL을 정의합니다.
 * 주요 애노테이션:
 *   @Select  - 조회 쿼리 정의
 *   @Insert  - 삽입 쿼리 정의
 *   @Update  - 수정 쿼리 정의
 *   @Delete  - 삭제 쿼리 정의
 *   @Results / @Result - resultMap 대신 컬럼-프로퍼티 매핑 정의
 *   @ResultMap - 앞서 정의한 @Results 를 재사용
 *   @Options - useGeneratedKeys 등 부가 옵션
 *   @SelectProvider - 외부 빌더 클래스로 SQL을 동적 생성
 */
public interface CategoryMapper {

    /**
     * @Results 로 컬럼-프로퍼티 매핑을 정의하고 id 를 부여합니다.
     * id="categoryResultMap" 은 다른 메서드에서 @ResultMap 으로 재사용 가능합니다.
     */
    @Select("SELECT category_id, name, last_update FROM category WHERE category_id = #{id}")
    @Results(id = "categoryResultMap", value = {
            @Result(id = true, column = "category_id", property = "categoryId"),
            @Result(column = "name",        property = "name"),
            @Result(column = "last_update", property = "lastUpdate")
    })
    Category selectById(Byte id);

    /**
     * @ResultMap 으로 selectById 에서 정의한 결과 매핑을 재사용합니다.
     */
    @Select("SELECT category_id, name, last_update FROM category ORDER BY category_id")
    @ResultMap("categoryResultMap")
    List<Category> selectAll();

    /**
     * @Options 의 useGeneratedKeys=true 로 INSERT 후 생성된 PK를
     * category.categoryId 에 자동으로 반영합니다.
     */
    @Insert("INSERT INTO category (name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "categoryId")
    int insert(Category category);

    /**
     * 이름 업데이트.
     */
    @Update("UPDATE category SET name = #{name} WHERE category_id = #{categoryId}")
    int update(Category category);

    /**
     * 단건 삭제.
     */
    @Delete("DELETE FROM category WHERE category_id = #{categoryId}")
    int delete(Byte categoryId);

    /**
     * @SelectProvider: 외부 빌더 클래스(CategorySqlBuilder)가 동적으로 SQL을 생성합니다.
     * type  - SQL 빌더 클래스
     * method - 호출할 메서드명 (Map<String, Object> 를 파라미터로 받아 String 반환)
     */
    @SelectProvider(type = CategorySqlBuilder.class, method = "buildSearchQuery")
    @ResultMap("categoryResultMap")
    List<Category> search(Map<String, Object> params);

    /**
     * @SelectProvider: ID 범위로 조회하는 동적 SQL.
     */
    @SelectProvider(type = CategorySqlBuilder.class, method = "buildSelectByIdRange")
    @ResultMap("categoryResultMap")
    List<Category> selectByIdRange(Map<String, Object> params);
}
