package SqlMapping.entity;

import java.util.List;

/**
 * actor 와 해당 배우가 출연한 film 목록을 <collection> 으로 조회한 결과를 담는 엔티티입니다.
 * resultMap의 <collection> 태그를 통해 1:N 관계를 표현합니다.
 */
public class ActorWithFilms {
    private Short actorId;
    private String firstName;
    private String lastName;
    private List<Film> films; // 1:N collection

    public Short getActorId() { return actorId; }
    public void setActorId(Short actorId) { this.actorId = actorId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public List<Film> getFilms() { return films; }
    public void setFilms(List<Film> films) { this.films = films; }

    @Override
    public String toString() {
        return "ActorWithFilms{actorId=" + actorId +
                ", name='" + firstName + " " + lastName + "'" +
                ", filmCount=" + (films != null ? films.size() : 0) +
                '}';
    }
}
