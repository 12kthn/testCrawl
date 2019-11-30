package com.truyenfull.query.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {

    private static ObjectMapper instance;

    public static ObjectMapper getInstance(){
        if (instance == null){
            instance = new ObjectMapper();
        }
        return instance;
    }
}
