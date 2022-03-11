package com.yacacode.backend.repository;

import com.yacacode.backend.models.Request;
import com.yacacode.backend.models.Score;
import com.yacacode.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class RequestRepositoryCustom {

    @Autowired
    private UserRepository userRepository;

    public static EntityManager em;

    public RequestRepositoryCustom(EntityManager em) {
        this.em = em;
    }

    public Request findByScoreId(Score score){
        try{
            Request request =  em.createQuery("SELECT r FROM Request r WHERE r.score_id = ?1 AND r.active = true", Request.class)
                    .setParameter(1, score)
                    .getSingleResult();
            return request;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public List<Request> findByUserId(User user) {
        try{
            List<Request> requests =  em.createQuery("SELECT r FROM Request r WHERE r.user_id = ?1", Request.class)
                    .setParameter(1, user)
                    .getResultList();
            return requests;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public List<Request> findByActive(boolean b) {
        try{
            List<Request> requests =  em.createQuery("SELECT r FROM Request r WHERE r.active = ?1", Request.class)
                    .setParameter(1, b)
                    .getResultList();
            return requests;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
}
