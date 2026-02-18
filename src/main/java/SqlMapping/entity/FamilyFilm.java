package SqlMapping.entity;

/**
 * rating이 'G' 또는 'PG'인 영화를 나타내는 클래스입니다.
 * <discriminator>에 의해 FilmSummary 대신 이 타입으로 인스턴스화됩니다.
 */
public class FamilyFilm extends FilmSummary {

    @Override
    public String getContentType() {
        return "전체 관람가 (" + getRating() + ")";
    }
}
