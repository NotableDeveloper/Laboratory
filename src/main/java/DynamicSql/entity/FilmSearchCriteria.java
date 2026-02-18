package DynamicSql.entity;

import java.util.List;

/**
 * 동적 SQL 검색 조건을 담는 파라미터 객체(Parameter Object)입니다.
 *
 * null 필드는 검색 조건에서 제외되며, <if> / <where> / <choose> 태그가
 * 이 객체의 프로퍼티를 OGNL 표현식으로 평가하여 SQL을 동적으로 생성합니다.
 */
public class FilmSearchCriteria {
    /** 영화 제목 키워드 (LIKE 검색) */
    private String title;

    /** 영화 등급 ('G', 'PG', 'PG-13', 'R', 'NC-17') */
    private String rating;

    /** 최소 상영 시간 (분) */
    private Short minLength;

    /** 최대 상영 시간 (분) */
    private Short maxLength;

    /**
     * 정렬 기준: 'title' | 'length' | 'rating'
     * <choose>/<when>/<otherwise> 예제에서 사용
     */
    private String sortBy;

    /** IN 절에 사용할 film_id 목록 */
    private List<Short> filmIds;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public Short getMinLength() { return minLength; }
    public void setMinLength(Short minLength) { this.minLength = minLength; }

    public Short getMaxLength() { return maxLength; }
    public void setMaxLength(Short maxLength) { this.maxLength = maxLength; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public List<Short> getFilmIds() { return filmIds; }
    public void setFilmIds(List<Short> filmIds) { this.filmIds = filmIds; }

    @Override
    public String toString() {
        return "FilmSearchCriteria{title='" + title + "', rating='" + rating +
                "', minLength=" + minLength + ", maxLength=" + maxLength +
                ", sortBy='" + sortBy + "', filmIds=" + filmIds + '}';
    }
}
