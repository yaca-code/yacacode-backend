package com.yacacode.backend.repository;

import javax.persistence.EntityManager;

import com.yacacode.backend.models.Score;

import com.yacacode.backend.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public class ScoreRepositoryCustom {
    
    public static EntityManager em;

    private Calendar cal = Calendar.getInstance();

    public ScoreRepositoryCustom(EntityManager em) {
        this.em = em;
    }

    @Query(value = "SELECT * FROM score WHERE user_id = ?1", nativeQuery = true)
    public Score findScoreByUserIdCurrentDay(User user) {
        try{
            Score score =  em.createQuery("SELECT s FROM Score s WHERE s.user_id = ?1 AND s.day = ?2 AND s.month = ?3 AND s.year = ?4", Score.class)
                    .setParameter(1, user)
                    .setParameter(2, cal.get(Calendar.DAY_OF_MONTH))
                    .setParameter(3, cal.get(Calendar.MONTH) + 1)
                    .setParameter(4, cal.get(Calendar.YEAR))
                    .getSingleResult();
            return score;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public List<Score> findScoreByUserIdMonth(User user, Integer month, Integer year) {
        try{
            List<Score> scores =  em.createQuery("SELECT s FROM Score s WHERE s.user_id = ?1 AND s.month = ?2 AND s.year = ?3", Score.class)
                    .setParameter(1, user)
                    .setParameter(2, month)
                    .setParameter(3, year)
                    .getResultList();
            for(Score score : scores){
                score.setUser_id(null);
            }
            return scores;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }


}
