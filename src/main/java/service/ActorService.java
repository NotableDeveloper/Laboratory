package service;

import entity.Actor;
import mapper.ActorMapper;
import org.apache.ibatis.session.SqlSession;
import util.MyBatisUtil;

import java.util.List;

public class ActorService {
    public List<Actor> findAllActors() {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ActorMapper mapper = session.getMapper(ActorMapper.class);
            return mapper.findAllActors();
        }
    }

    public Actor findActorById(int actorId) {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ActorMapper mapper = session.getMapper(ActorMapper.class);
            return mapper.findActorById(actorId);
        }
    }

    public List<Actor> findActorsByLastName(String lastName) {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ActorMapper mapper = session.getMapper(ActorMapper.class);
            return mapper.findActorsByLastName(lastName);
        }
    }
}
