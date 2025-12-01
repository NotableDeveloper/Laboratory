package service;

import entity.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = new FilmService();
    }

    @Test
    void testFindFilmById() {
        Film film = filmService.findFilmById(1);
        assertNotNull(film);
        assertEquals(1, film.getFilmId());
        assertEquals("ACADEMY DINOSAUR", film.getTitle());
    }

    @Test
    void testFindFilmsByTitle() {
        List<Film> films = filmService.findFilmsByTitle("ACADEMY");
        assertNotNull(films);
        assertFalse(films.isEmpty());
        assertTrue(films.stream().allMatch(f -> f.getTitle().contains("ACADEMY")));
    }

    @Test
    void testFindFilmById_nonExistent() {
        Film film = filmService.findFilmById(99999);
        assertNull(film);
    }

    @Test
    void testFindFilmsByTitle_nonExistent() {
        List<Film> films = filmService.findFilmsByTitle("NONEXISTENT TITLE");
        assertNotNull(films);
        assertTrue(films.isEmpty());
    }
}
