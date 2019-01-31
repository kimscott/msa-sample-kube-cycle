package com.example.template;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
public class Application {

    protected static ApplicationContext applicationContext;
    public static void main(String[] args) {

        applicationContext = SpringApplication.run(Application.class, args);


        try {
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjMzMyIsIm5hbWUiOiLquYDtmJXqta0iLCJleHQxIjoiZWMyIiwiZXh0MiI6Ims4cy5iemR2b3BzLmNvbSJ9.3FhQ4F-ak5FxO-QZRMo2LWCJt7mXgqKzPRRJAQAPEZ8";
            Jwt jwt = JwtHelper.decode(token);

            JSONParser parser = new JSONParser();
            Object obj = parser.parse( jwt.getClaims() );
            JSONObject jsonObj = (JSONObject) obj;
            System.out.println(jsonObj.get("id"));
            System.out.println(jsonObj.get("name"));
            System.out.println(jsonObj.get("ext1"));
            System.out.println(jsonObj.get("ext2"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}

