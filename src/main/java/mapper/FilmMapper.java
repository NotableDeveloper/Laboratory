package mapper;

import entity.Film;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface FilmMapper {
    Film selectFilmById(@Param("filmId") int filmId);
    List<Film> selectFilmsByTitle(@Param("title") String title);
}
