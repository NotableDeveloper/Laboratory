package SqlMapping.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * sakila DB의 film 테이블과 매핑되는 엔티티 클래스입니다.
 */
public class Film {
    private Short filmId;
    private String title;
    private String description;
    private String releaseYear;
    private Byte languageId;
    private Byte rentalDuration;
    private BigDecimal rentalRate;
    private Short length;
    private BigDecimal replacementCost;
    private String rating;
    private Timestamp lastUpdate;

    public Short getFilmId() { return filmId; }
    public void setFilmId(Short filmId) { this.filmId = filmId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReleaseYear() { return releaseYear; }
    public void setReleaseYear(String releaseYear) { this.releaseYear = releaseYear; }

    public Byte getLanguageId() { return languageId; }
    public void setLanguageId(Byte languageId) { this.languageId = languageId; }

    public Byte getRentalDuration() { return rentalDuration; }
    public void setRentalDuration(Byte rentalDuration) { this.rentalDuration = rentalDuration; }

    public BigDecimal getRentalRate() { return rentalRate; }
    public void setRentalRate(BigDecimal rentalRate) { this.rentalRate = rentalRate; }

    public Short getLength() { return length; }
    public void setLength(Short length) { this.length = length; }

    public BigDecimal getReplacementCost() { return replacementCost; }
    public void setReplacementCost(BigDecimal replacementCost) { this.replacementCost = replacementCost; }

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
