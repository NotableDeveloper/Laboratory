package service;

import entity.Actor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActorServiceTest {

    private ActorService actorService;

    @BeforeEach
    void setUp() {
        actorService = new ActorService();
    }

    /*
        mysql> select count(*) from actor;
        +----------+
        | count(*) |
        +----------+
        |      200 |
        +----------+
     */
    @Test
    void testFindAllActors() {
        List<Actor> actors = actorService.findAllActors();
        assertNotNull(actors);
        assertFalse(actors.isEmpty());
        assertTrue(actors.size() >= 200, "Should contain at least 200 actors.");
    }

    /*
        mysql> select * from actor where actor_id = 1;
        +----------+------------+-----------+---------------------+
        | actor_id | first_name | last_name | last_update         |
        +----------+------------+-----------+---------------------+
        |        1 | PENELOPE   | GUINESS   | 2006-02-15 04:34:33 |
        +----------+------------+-----------+---------------------+
     */
    @Test
    void testFindActorById() {
        // Actor with ID 1 in Sakila DB is PENELOPE GUINESS.
        Actor actor = actorService.findActorById(1);
        assertNotNull(actor);
        assertEquals(1, actor.getActorId());
        assertEquals("PENELOPE", actor.getFirstName());
        assertEquals("GUINESS", actor.getLastName());
    }

    /*
        mysql> select * from actor where last_name = "CHASE";
        +----------+------------+-----------+---------------------+
        | actor_id | first_name | last_name | last_update         |
        +----------+------------+-----------+---------------------+
        |        3 | ED         | CHASE     | 2006-02-15 04:34:33 |
        |      176 | JON        | CHASE     | 2006-02-15 04:34:33 |
        +----------+------------+-----------+---------------------+
     */
    @Test
    void testFindActorsByLastName() {
        // Find actors with a common last name.
        List<Actor> actors = actorService.findActorsByLastName("CHASE");
        assertNotNull(actors);
        assertFalse(actors.isEmpty());
        // Verify that all returned actors have the correct last name.
        assertTrue(actors.stream().allMatch(a -> "CHASE".equals(a.getLastName())));

        // Test with a non-existent last name.
        List<Actor> noActors = actorService.findActorsByLastName("NONEXISTENT_LAST_NAME");
        assertNotNull(noActors);
        assertTrue(noActors.isEmpty());
    }
}
