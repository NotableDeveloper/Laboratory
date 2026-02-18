package DynamicSql.mapper;

import DynamicSql.entity.Film;
import DynamicSql.entity.FilmSearchCriteria;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 동적 SQL 예제 매퍼 인터페이스입니다.
 * FilmDynamicMapper.xml 과 namespace 로 연결됩니다.
 */
public interface FilmDynamicMapper {

    /**
     * <where> + <if>: null이 아닌 조건만 WHERE 절에 포함
     * title, rating, minLength, maxLength 는 각각 독립적으로 적용 가능
     */
    List<Film> searchWithIf(FilmSearchCriteria criteria);

    /**
     * <choose> + <when> + <otherwise>: sortBy 값에 따라 ORDER BY 절 변경
     * Java의 switch-case 와 유사한 구조
     */
    List<Film> searchWithChoose(FilmSearchCriteria criteria);

    /**
     * <set> + <if>: null이 아닌 필드만 UPDATE
     * 부분 업데이트(partial update) 패턴
     */
    int updatePartial(Film film);

    /**
     * <foreach>: List<Short> 를 IN 절로 변환
     * @Param 으로 파라미터 이름을 명시하면 XML에서 "ids" 로 참조
     */
    List<Film> selectByIds(@Param("ids") List<Short> filmIds);

    /**
     * <trim>: prefix/prefixOverrides 로 WHERE 절을 커스텀 조합
     * <where> 태그와 동일한 역할이지만 더 세밀한 제어 가능
     */
    List<Film> searchWithTrim(FilmSearchCriteria criteria);
}
