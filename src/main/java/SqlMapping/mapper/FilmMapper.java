package SqlMapping.mapper;

import SqlMapping.entity.ActorWithFilms;
import SqlMapping.entity.Film;
import SqlMapping.entity.FilmDetail;
import SqlMapping.entity.FilmSummary;

import java.util.List;

/**
 * film 테이블에 대한 SQL 매핑 인터페이스입니다.
 * FilmMapper.xml 파일과 namespace로 연결됩니다.
 */
public interface FilmMapper {

    // === 기본 CRUD ===

    /** <sql> 재사용 + resultMap으로 단건 조회 */
    Film selectById(Short filmId);

    /** <sql> 재사용 + resultMap으로 전체 조회 (상위 10건) */
    List<Film> selectAll();

    /** useGeneratedKeys로 자동 생성 PK를 film 객체에 반영 */
    int insert(Film film);

    /** 전체 필드 업데이트 */
    int update(Film film);

    /** 단건 삭제 */
    int delete(Short filmId);

    // === ResultMap 예제 ===

    /** <association>: film + language 1:1 조인 결과 매핑 */
    FilmDetail selectDetailById(Short filmId);

    /** <collection>: actor + film 1:N 조인 결과 매핑 */
    ActorWithFilms selectActorWithFilms(Short actorId);

    /** <discriminator>: rating 값에 따라 FamilyFilm 또는 RestrictedFilm 반환 */
    FilmSummary selectSummaryById(Short filmId);

    /** <discriminator> 목록 조회: G, PG, NC-17 등급 혼합 */
    List<FilmSummary> selectMixedRatings();
}
