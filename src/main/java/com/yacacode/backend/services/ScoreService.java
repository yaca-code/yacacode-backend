package com.yacacode.backend.services;

import com.google.gson.Gson;
import com.yacacode.backend.models.Score;
import com.yacacode.backend.models.User;
import com.ph.ponto.repository.*;

import com.yacacode.backend.repository.RequestRepositoryCustom;
import com.yacacode.backend.repository.ScoreRepository;
import com.yacacode.backend.repository.ScoreRepositoryCustom;
import com.yacacode.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScoreRepositoryCustom repositoryCustom;

    @Autowired
    private RequestRepositoryCustom requestRepositoryCustom;

    private Gson gson = new Gson();

    private Calendar cal = Calendar.getInstance();

    public String beatTime(Long id){
        User user = new User();
        try{
            user = userRepository.findById(id).get();
        }catch(NoSuchElementException e){
            return ("{" + "\"message\":\"" + "User not found\"" + ","+ "\"Listed\":\"false\"" + "}");
        }
        Score score = repositoryCustom.findScoreByUserIdCurrentDay(user);
        if(score==null){
            Score score1 = new Score();
            score1.setActive(true);
            score1.setCreated_at(getDate());
            score1.setDay(cal.get(Calendar.DAY_OF_MONTH));
            score1.setMonth(cal.get(Calendar.MONTH)+1);
            score1.setYear(cal.get(Calendar.YEAR));
            score1.setUser_id(user);
            score1.setStatus(0);
            score1.setInput(getDate());
            repository.save(score1);
            return ("{" + "\"message\":\"Entry registered successfully\"" + ","+ "\"Score\":\"true\"" + "}");
        }else{
            if(score.getStatus() == 0){
                score.setStatus(1);
                score.setOut_lunch(getDate());
                repository.save(score);
//                String dayWeekText = new SimpleDateFormat("EEEE").format(getDate());
//                if(dayWeekText.equals("Saturday")){
//                    extraSaturday(score.getId());
//                }
                return ("{" + "\"message\":\"Successfully registered lunch outing\"" + ","+ "\"Score\":\"true\"" + "}");
            }else if(score.getStatus() == 1){
                score.setStatus(2);
                score.setLunch_entree(getDate());
                repository.save(score);
                return ("{" + "\"message\":\"Return from lunch registered successfully\"" + ","+ "\"Score\":\"true\"" + "}");
            }else if(score.getStatus() == 2){
                score.setStatus(3);
                score.setExit(getDate());
                score.setActive(false);
                repository.save(score);
                // extra(score.getId());
                return ("{" + "\"message\":\"Successfully registered exit\"" + ","+ "\"Score\":\"true\"" + "}");
            }else if(score.getStatus() > 2){
                return ("{" + "\"message\":\"You already registered all the hours\"" + ","+ "\"Score\":\"false\"" + "}");
            }
        }
        return ("{" + "\"message\":\"You already registered all the hours\"" + ","+ "\"Score\":\"false\"" + "}");
    }

    public void extra(Long id){
        Score score = repository.getById(id);
        User user = userRepository.findById(score.getUser_id().getId()).get();

        long diff = score.getOut_lunch().getTime() - score.getInput().getTime();
        long diff2 = score.getExit().getTime() - score.getLunch_entree().getTime();

        float minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        float minutes2 = TimeUnit.MILLISECONDS.toMinutes(diff2);

        float total = minutes + minutes2;

        Integer extra = (int) (total - 480);
        score.setExtra(extra);
        try{
            user.setExtras(user.getExtras() + extra);
            score.setExtra(extra);
        }catch (NullPointerException e){
            user.setExtras(extra);
            score.setExtra(user.getExtras());
            repository.save(score);
            userRepository.save(user);
        }
        repository.save(score);
        userRepository.save(user);
    }

    public void extraSaturday(Long id){
        Score score = repository.getById(id);
        User user = userRepository.findById(score.getUser_id().getId()).get();

        long diff = score.getOut_lunch().getTime() - score.getInput().getTime();

        float total = TimeUnit.MILLISECONDS.toMinutes(diff);

        Integer extra = (int) (total - 240);
        try{
            user.setExtras(user.getExtras() + extra);
            score.setExtra(extra);
        }catch (NullPointerException e){
            user.setExtras(extra);
            score.setExtra(user.getExtras());
            repository.save(score);
            userRepository.save(user);
        }
        repository.save(score);
        userRepository.save(user);
    }

    public String findScoreByUserIdMonth(Long id, Integer month, Integer year){
        User user = userRepository.findById(id).get();
        List<Score> scores = repositoryCustom.findScoreByUserIdMonth(user, month, year);
        if(scores.size()>0){
            return removeLastChar(gson.toJson(scores))+(",{" + "\"message\":\"" + "Scores listed\"" + ","+ "\"Listed\":\"true\"" + "}]");
        }else{
            return ("{" + "\"message\":\"" + "No scores found\"" + ","+ "\"Listed\":\"false\"" + "}");
        }
    }

    public String removeLastChar(String str){
        return str.substring(0, str.length()-1);
    }

    public Date getDate(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        try{
            return dateFormatLocal.parse( dateFormatGmt.format(new Date()));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
