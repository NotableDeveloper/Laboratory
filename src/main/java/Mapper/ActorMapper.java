package Mapper;

import Mapper.entity.Actor;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface ActorMapper {

    @Select("SELECT actor_id as actorId, first_name as firstName, last_name as lastName, last_update as lastUpdate FROM actor WHERE actor_id = #{id}")
    Actor selectActorById(Short id);

    @Select("SELECT actor_id as actorId, first_name as firstName, last_name as lastName, last_update as lastUpdate FROM actor")
    List<Actor> selectAllActors();
}
