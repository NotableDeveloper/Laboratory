package Cache.mapper;

import Cache.entity.Film;

import java.util.List;

/**
 * 캐시 예제 매퍼 인터페이스입니다.
 * FilmCacheMapper.xml 에 <cache/> 가 선언되어 있어 2차 캐시가 활성화됩니다.
 */
public interface FilmCacheMapper {

    /** 단건 조회: 1차/2차 캐시 동작 확인에 사용 */
    Film selectById(Short filmId);

    /** 목록 조회: 등급별 영화 목록 */
    List<Film> selectByRating(String rating);
}
