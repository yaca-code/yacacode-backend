package com.ph.ponto.repository;

import com.ph.ponto.models.Request;
import com.ph.ponto.models.Score;
import com.ph.ponto.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserRepositoryCustom {

    private EntityManager em;

    public UserRepositoryCustom(EntityManager em) {
        this.em = em;
    }

    public List<User> findAllActive(){
        String query = "SELECT u FROM User u WHERE u.active = true";
        return em.createQuery(query, User.class).getResultList();
    }

}
