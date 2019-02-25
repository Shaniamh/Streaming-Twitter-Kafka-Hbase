package com.kp.hbase.config;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TwitterSerializer implements Serializer {

    public void configure(Map map, boolean b) {}

    public byte[] serialize(String s, Object o) {
    	
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(o);
        } catch (Exception e) {}
        return retVal;
    }

    public void close() {}
}
