package Cache.entity;

import java.io.Serializable;

/**
 * 캐시 예제에서 사용하는 film 엔티티입니다.
 *
 * 2차 캐시(Second-Level Cache)를 사용하려면 캐시에 저장될 객체가
 * java.io.Serializable 을 구현해야 합니다.
 * MyBatis 기본 캐시 구현체는 직렬화/역직렬화로 객체를 저장·복원합니다.
 */
public class Film implements Serializable {

    private static final long serialVersionUID = 1L;

    private Short filmId;
    private String title;
    private String rating;
    private Short length;

    public Short getFilmId() { return filmId; }
    public void setFilmId(Short filmId) { this.filmId = filmId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public Short getLength() { return length; }
    public void setLength(Short length) { this.length = length; }

    @Override
    public String toString() {
        return "Film{filmId=" + filmId +
                ", title='" + title + "'" +
                ", rating='" + rating + "'" +
                ", length=" + length +
                '}';
    }
}
