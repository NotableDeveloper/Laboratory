package SqlMapping.entity;

/**
 * <discriminator> 예제의 기반 클래스입니다.
 * rating 컬럼 값에 따라 FamilyFilm 또는 RestrictedFilm 인스턴스로 생성됩니다.
 */
public class FilmSummary {
    private Short filmId;
    private String title;
    private String rating;

    public Short getFilmId() { return filmId; }
    public void setFilmId(Short filmId) { this.filmId = filmId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    /**
     * 서브클래스에서 재정의하여 콘텐츠 유형을 반환합니다.
     */
    public String getContentType() {
        return "일반";
    }

    @Override
    public String toString() {
        return "FilmSummary{filmId=" + filmId +
                ", title='" + title + "'" +
                ", rating='" + rating + "'" +
                ", contentType='" + getContentType() + "'" +
                ", javaType='" + this.getClass().getSimpleName() + "'" +
                '}';
    }
}
