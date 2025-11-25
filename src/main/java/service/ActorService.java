package service;

import entity.Actor;
import mapper.ActorMapper;
import org.apache.ibatis.session.SqlSession;
import util.MyBatisUtil;

import java.util.List;

public class ActorService {
    public List<Actor> findAllActors() {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            ActorMapper mapper = session.getMapper(ActorMapper.class);
            return mapper.findAllActors();
        } finally {
            session.close();
        }
    }

    public Actor findActorById(int actorId) {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            ActorMapper mapper = session.getMapper(ActorMapper.class);
            return mapper.findActorById(actorId);
        } finally {
            session.close();
        }
    }

    public List<Actor> findActorsByLastName(String lastName) {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            ActorMapper mapper = session.getMapper(ActorMapper.class);
            return mapper.findActorsByLastName(lastName);
        } finally {
            session.close();
        }
    }
}
