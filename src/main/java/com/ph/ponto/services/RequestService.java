package com.ph.ponto.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ph.ponto.models.Request;
import com.ph.ponto.models.Score;
import com.ph.ponto.models.User;
import com.ph.ponto.repository.RequestRepository;
import com.ph.ponto.repository.RequestRepositoryCustom;
import com.ph.ponto.repository.ScoreRepository;
import com.ph.ponto.repository.UserRepository;
import com.ph.ponto.services.adapters.HibernateProxyTypeAdapter;
import com.ph.ponto.services.adapters.RequestAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;

@Service
public class RequestService {

    @Autowired
    private RequestRepository repository;

    @Autowired
    private RequestRepositoryCustom requestRepositoryCustom;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private UserRepository userRepository;

    private Gson gson = new Gson();

    public String getRequestsById(Long id){
        User user = new User();
        try{
            user = userRepository.getById(id);
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return ("{" + "\"message\":\"User not found\"" + ","+ "\"Listed\":\"false\"" + "}");
        }
        List<Request> reqPesq = requestRepositoryCustom.findByUserId(user);
        if(reqPesq.size()>0){
            GsonBuilder gsonBuilder = new GsonBuilder();
//            gsonBuilder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
            Gson gson = gsonBuilder.registerTypeAdapter(Request.class, new RequestAdapter()).create();
//            Gson gson = gsonBuilder.create();
            return gson.toJson(reqPesq);
        }else{
            return ("{" + "\"message\":\"Request not found\"" + ","+ "\"Listed\":\"false\"" + "}");
        }
    }

    public String getRequestsActives(){
        try{
            List<Request> reqPesq = requestRepositoryCustom.findByActive(true);
            if(reqPesq.size()>0){
                GsonBuilder gsonBuilder = new GsonBuilder();
//            gsonBuilder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
                Gson gson = gsonBuilder.registerTypeAdapter(Request.class, new RequestAdapter()).create();
//            Gson gson = gsonBuilder.create();
                return gson.toJson(reqPesq);
            }else{
                return ("{" + "\"message\":\"Request not found\"" + ","+ "\"Listed\":\"false\"" + "}");
            }
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return ("{" + "\"message\":\"User not found\"" + ","+ "\"Listed\":\"false\"" + "}");
        }
    }

    public String getAllRequests(){
        try{
            List<Request> reqPesq = repository.findAll();
            if(reqPesq.size()>0){
                GsonBuilder gsonBuilder = new GsonBuilder();
//            gsonBuilder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
                Gson gson = gsonBuilder.registerTypeAdapter(Request.class, new RequestAdapter()).create();
//            Gson gson = gsonBuilder.create();
                return gson.toJson(reqPesq);
            }else{
                return ("{" + "\"message\":\"Request not found\"" + ","+ "\"Listed\":\"false\"" + "}");
            }
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return ("{" + "\"message\":\"User not found\"" + ","+ "\"Listed\":\"false\"" + "}");
        }
    }

    public String request(Long id, String requestString) {
        Score score = new Score();
        try{
            score = scoreRepository.findById(id).get();
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return ("{" + "\"message\":\"Score not found\"" + ","+ "\"Request\":\"false\"" + "}");
        }

        Request reqPesq = requestRepositoryCustom.findByScoreId(score);
        Request request = new Request();
        if(reqPesq!=null){
            request = reqPesq;
        }
        try{
            JsonObject jobj = new Gson().fromJson(requestString, JsonObject.class);

            Date input = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(jobj.get("input").getAsString());
            Date out_lunch = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(jobj.get("out_lunch").getAsString());
            Date lunch_entree = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(jobj.get("lunch_entree").getAsString());
            Date exit = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(jobj.get("exit").getAsString());

            String description = jobj.get("description").getAsString();

            request.setInput(input);
            request.setOut_lunch(out_lunch);
            request.setLunch_entree(lunch_entree);
            request.setExit(exit);
            request.setDescription(description);
            request.setUser_id(score.getUser_id());
            request.setScore_id(score);
            request.setActive(true);
            request.setCreated_at(getDate());
            repository.save(request);
            return ("{" + "\"message\":\"Request saved successfully\"" + ","+ "\"Request\":\"true\"" + "}");
        }catch (ParseException e){
            System.out.println(e.getMessage());
            return ("{" + "\"message\":\"Parse error\"" + ","+ "\"Request\":\"false\"" + "}");
        }
    }

    public String aproveRequest(Long id){
        try{
            Request request = repository.getById(id);
            request.setActive(false);
            request.setDeleted_at(getDate());
            Score score = scoreRepository.findById(request.getScore_id().getId()).get();
            score.setInput(request.getInput());
            score.setOut_lunch(request.getOut_lunch());
            score.setLunch_entree(request.getLunch_entree());
            score.setExit(request.getExit());
            scoreRepository.save(score);
            repository.save(request);
            return ("{" + "\"message\":\"Request aproved successfully\"" + ","+ "\"Request\":\"true\"" + "}");
        }catch (Exception e){
            return ("{" + "\"message\":\"Error to aprove request\"" + ","+ "\"Request\":\"false\"" + "}");
        }
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
