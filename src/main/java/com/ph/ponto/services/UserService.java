package com.ph.ponto.services;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ph.ponto.config.AwsTools;
import com.ph.ponto.models.User;
import com.ph.ponto.repository.UserRepository;

import com.ph.ponto.repository.UserRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserRepositoryCustom repositoryCustom;

    public static AwsTools awsTools = new AwsTools();

    Gson gson = new Gson();

    public static PasswordEncoder passwordEncoder;

    public String auth(User user){
        User userAuth = repository.findByName(user.getName());
        if(userAuth != null){
            if(checkPassword(user.getPassword(),userAuth.getPassword())){
                userAuth.setPassword("");
                userAuth.setImage(getImage(user.getName()));
                return removeLastChar(gson.toJson(userAuth))+("," + "\"message\":\"User authenticated\"" + ","+ "\"auth\":\"true\"" + "}");
            }else{
                return ("{" + "\"message\":\"Wrong password\"" + ","+ "\"auth\":\"false\"" + "}");
            }
        }else{
            return ("{" + "\"message\":\"User not found\"" + ","+ "\"auth\":\"false\"" + "}");
        }
    }

    public String listUsers() {
    	List<User> teste = repositoryCustom.findAllActive();
		for(int i=0;i<teste.size();i++) {
            if (teste.get(i).getActive() == true) {
                teste.get(i).setPassword("");
                teste.get(i).setImage(getImage(teste.get(i).getName()));
            }
        }
		String json = gson.toJson(teste);
        return json;
    }

    public String saveUser(User user) {
        try{
            user.setCreated_at(getDate());
            user.setActive(true);
            user.setPassword(encryptPassword(user.getPassword()));
            User checkUser = repository.findByName(user.getName());
            if(checkUser!=null) {
                return ("{" + "\"message\":\"Username already registered\"" + "," + "\"auth\":\"false\"" + "}");
            }
            if(user.getHierarchy()==null){
                user.setHierarchy(0);
            }else{
                if(user.getHierarchy()>1){
                    return ("{" + "\"message\":\"User hierarchy invalid\"" + ","+ "\"auth\":\"false\"" + "}");
                }
            }
            if(user.getImage()!=""&&user.getImage()!=null){
                if(saveImage(user.getName(),user.getImage())){
                    user.setImage("");
                    repository.save(user);
                    return ("{" + "\"message\":\"User saved\"" + ","+ "\"save\":\"true\"" + "}");
                }else{
                    return ("{" + "\"message\":\"User not saved\"" + ","+ "\"save\":\"false\"" + "}");
                }
            }else{
                repository.save(user);
            }
            return ("{" + "\"message\":\"User saved\"" + ","+ "\"save\":\"true\"" + "}");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e);
            return ("{" + "\"message\":\"User not saved\"" + ","+ "\"save\":\"false\"" + "}");
        }
    }

    public String deleteUser(Long id){
        try{
            User user = repository.getById(id);
            if(user.getActive()==false){
                return ("{" + "\"message\":\"User already deleted\"" + ","+ "\"delete\":\"false\"" + "}");
            }
            user.setActive(false);
            user.setDeleted_at(getDate());
            awsTools.deleteImage(user.getName());
            repository.save(user);
            return ("{" + "\"message\":\"User deleted\"" + ","+ "\"deleted\":\"true\"" + "}");
        }catch(Exception e){
            System.out.println(e);
            return ("{" + "\"message\":\"User not deleted\"" + ","+ "\"deleted\":\"false\"" + "}");
        }
    }

    public String getUserById(Long id){
        try{
            Optional<User> userOpt = repository.findById(id);
            User user = repository.findByName(userOpt.get().getName());
            if(user==null||user.equals(null)){
                return ("{" + "\"message\":\"User not found\"" + ","+ "\"found\":\"false\"" + "}");
            }
            user.setPassword("");
            user.setImage(getImage(user.getName()));
            return removeLastChar(gson.toJson(user))+("," + "\"message\":\"User found\"" + ","+ "\"found\":\"true\"" + "}");
        }catch(Exception e){
            System.out.println(e);
            return ("{" + "\"message\":\"User not found\"" + ","+ "\"found\":\"false\"" + "}");
        }
    }

    public String getUserByName(String name){
        try{
            User user = repository.findByName(name);
            user.setPassword("");
            user.setImage(getImage(user.getName()));
            return removeLastChar(gson.toJson(user))+("," + "\"message\":\"User found\"" + ","+ "\"found\":\"true\"" + "}");
        }catch(Exception e){
            System.out.println(e);
            return ("{" + "\"message\":\"User not found\"" + ","+ "\"found\":\"false\"" + "}");
        }
    }

    public String forgotPassword(User user){
        try{
            User userFound = repository.findByName(user.getName());
            if(userFound==null||userFound.equals(null)){
                return ("{" + "\"message\":\"User not found\"" + ","+ "\"found\":\"false\"" + "}");
            }
            userFound.setPassword(encryptPassword(user.getPassword()));
            repository.save(userFound);
            return ("{" + "\"message\":\"Password changed\"" + ","+ "\"changed\":\"true\"" + "}");
        }catch(Exception e){
            System.out.println(e);
            return ("{" + "\"message\":\"Password not changed\"" + ","+ "\"changed\":\"false\"" + "}");
        }
    }

    public String handleExtra(String json){
        JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
        Long id = jobj.get("id").getAsLong();
        Integer extra = jobj.get("extra").getAsInt();
        try{
            User user = repository.getById(id);
            user.setExtras(extra);
            repository.save(user);
            return ("{" + "\"message\":\"extra updated\"" + ","+ "\"changed\":\"true\"" + "}");
        }catch(Exception e){
            System.out.println(e);
            return ("{" + "\"message\":\"User not found\"" + ","+ "\"changed\":\"false\"" + "}");
        }
    }

    public String updateUser(User user){
        try{
            User userFound = repository.getById(user.getId());
            boolean res = saveImage(user.getName(),user.getImage());
            if(res){
                userFound.setName(user.getName());
                userFound.setPassword(encryptPassword(user.getPassword()));
                userFound.setImage("");
                repository.save(userFound);
                return ("{" + "\"message\":\"User updated\"" + ","+ "\"changed\":\"true\"" + "}");
            }else {
                return ("{" + "\"message\":\"User not updated\"" + "," + "\"changed\":\"false\"" + "}");
            }
        }catch(Exception e){
            e.printStackTrace();
            return ("{" + "\"message\":\"User not found\"" + ","+ "\"changed\":\"false\"" + "}");
        }
    }

    public boolean saveImage(String nome, String base64){
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64.split(",")[1]);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            String fileName = nome+".png";
            awsTools.uploadImage(fileName, bytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getImage(String nome) {
        try{
            return awsTools.getImage(nome);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String encryptPassword(String password){
        this.passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);
        return encryptedPassword;
    }

    public boolean checkPassword(String password, String encryptedPassword){
        this.passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, encryptedPassword);
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
