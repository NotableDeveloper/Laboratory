package mapper;

import entity.Actor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActorMapper {

    List<Actor> findAllActors();

    Actor findActorById(@Param("actorId") int actorId);

    List<Actor> findActorsByLastName(@Param("lastName") String lastName);
}

