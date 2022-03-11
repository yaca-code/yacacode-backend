package com.ph.ponto.services.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ph.ponto.models.Request;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RequestAdapter implements JsonSerializer<Request> {
    @Override
    public JsonElement serialize(Request request, Type type, JsonSerializationContext jsc) {
        JsonObject jsonObject = new JsonObject();
        try{
            jsonObject.addProperty("id", request.getId());
            jsonObject.addProperty("score_id", request.getScore_id().getId());
            jsonObject.addProperty("score_date", new SimpleDateFormat("dd-MM-yyyy").format(request.getScore_id().getInput()));
            jsonObject.addProperty("user_id", request.getUser_id().getId());
            jsonObject.addProperty("username", request.getUser_id().getName());
            jsonObject.addProperty("input", new SimpleDateFormat("HH:mm").format(request.getInput()));
            jsonObject.addProperty("out_lunch", new SimpleDateFormat("HH:mm").format(request.getOut_lunch()));
            jsonObject.addProperty("lunch_entree", new SimpleDateFormat("HH:mm").format(request.getLunch_entree()));
            jsonObject.addProperty("exit", new SimpleDateFormat("HH:mm").format(request.getExit()));
            jsonObject.addProperty("description", request.getDescription());
            jsonObject.addProperty("active", request.getActive());
            jsonObject.addProperty("created_at", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(request.getCreated_at()));
//            dateFormatLocal.parse( dateFormatGmt.format(request.getInput()));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }
}
