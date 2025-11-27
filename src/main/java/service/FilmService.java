package service;

import entity.Film;
import mapper.FilmMapper;
import org.apache.ibatis.session.SqlSession;
import util.MyBatisUtil;

import java.util.List;

public class FilmService {

    public Film findFilmById(int filmId) {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            FilmMapper mapper = session.getMapper(FilmMapper.class);
            return mapper.selectFilmById(filmId);
        } finally {
            session.close();
        }
    }

    public List<Film> findFilmsByTitle(String title) {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            FilmMapper mapper = session.getMapper(FilmMapper.class);
            return mapper.selectFilmsByTitle("%" + title + "%");
        } finally {
            session.close();
        }
    }
}
