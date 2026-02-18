package DynamicSql.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 동적 SQL 예제에서 사용하는 film 엔티티입니다.
 * null 필드는 <set>/<if> 동적 UPDATE 시 업데이트 대상에서 제외됩니다.
 */
public class Film {
    private Short filmId;
    private String title;
    private String description;
    private Byte languageId;
    private BigDecimal rentalRate;
    private Short length;
    private String rating;
    private Timestamp lastUpdate;

    public Short getFilmId() { return filmId; }
    public void setFilmId(Short filmId) { this.filmId = filmId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Byte getLanguageId() { return languageId; }
    public void setLanguageId(Byte languageId) { this.languageId = languageId; }

    public BigDecimal getRentalRate() { return rentalRate; }
    public void setRentalRate(BigDecimal rentalRate) { this.rentalRate = rentalRate; }

    public Short getLength() { return length; }
    public void setLength(Short length) { this.length = length; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public Timestamp getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }

    @Override
    public String toString() {
        return "Film{filmId=" + filmId +
                ", title='" + title + "'" +
                ", rating='" + rating + "'" +
                ", length=" + length +
                ", rentalRate=" + rentalRate +
                '}';
    }
}
