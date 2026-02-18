package SqlMapping.entity;

/**
 * film 과 language 를 <association> 으로 조인한 결과를 담는 엔티티입니다.
 * resultMap의 <association> 태그를 통해 1:1 관계를 표현합니다.
 */
public class FilmDetail {
    private Short filmId;
    private String title;
    private String description;
    private Short length;
    private String rating;
    private Language language; // 1:1 association

    public Short getFilmId() { return filmId; }
    public void setFilmId(Short filmId) { this.filmId = filmId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Short getLength() { return length; }
    public void setLength(Short length) { this.length = length; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    @Override
    public String toString() {
        return "FilmDetail{filmId=" + filmId +
                ", title='" + title + "'" +
                ", length=" + length +
                ", rating='" + rating + "'" +
                ", language=" + language +
                '}';
    }
}
