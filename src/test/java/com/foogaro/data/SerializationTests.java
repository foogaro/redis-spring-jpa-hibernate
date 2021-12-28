package com.foogaro.data;


import com.foogaro.data.models.Movie;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.util.ArrayList;
import java.util.List;

public class SerializationTests {

    public static void main(String[] args) {
        JdkSerializationRedisSerializer jdkSeri = new JdkSerializationRedisSerializer();
        GenericJackson2JsonRedisSerializer genJsonSeri = new GenericJackson2JsonRedisSerializer();
        Jackson2JsonRedisSerializer jack2Seri = new Jackson2JsonRedisSerializer(Object.class);
        Gson gson = new GsonBuilder().serializeNulls().create();

        Movie movie = null;
        int rating = 0;
        int year = 0;
        String title = null;
        List<Object> list = new ArrayList<>();
        int movies = 1;
        long startDataPreparationTime = System.currentTimeMillis();
        System.out.print("Data preparation for " + movies + " objects of type Movie...");
        for(int i = 0; i < movies; i++){
            rating = (int) ((Math.random() * (4)) + 1);
            year = (int) ((Math.random() * (72)) + 1950);
            title = new Faker().book().title();
            list.add(new Movie(title,rating,year));
        }
        long endDataPreparationTime = System.currentTimeMillis() - startDataPreparationTime;
        System.out.println(" done in " + endDataPreparationTime + "ms. Preparation avg took " + (endDataPreparationTime/movies) + "ms each.");
        System.out.println("");

        long serializationStartTime = System.currentTimeMillis();
        byte[] serializedObject = jdkSeri.serialize(list);
        long serializationTime = (System.currentTimeMillis() - serializationStartTime);
        int bytes = serializedObject.length;
        long deserializationStartTime = System.currentTimeMillis();
        jdkSeri.deserialize(serializedObject);
        long deserializationTime = (System.currentTimeMillis() - deserializationStartTime);
        reportPerformance("JdkSerializationRedisSerializer", serializationTime, bytes, deserializationTime, serializedObject);

        serializationStartTime = System.currentTimeMillis();
        serializedObject = genJsonSeri.serialize(list);
        serializationTime = (System.currentTimeMillis() - serializationStartTime);
        bytes = serializedObject.length;
        deserializationStartTime = System.currentTimeMillis();
        genJsonSeri.deserialize(serializedObject);
        deserializationTime = (System.currentTimeMillis() - deserializationStartTime);
        reportPerformance("GenericJackson2JsonRedisSerializer", serializationTime, bytes, deserializationTime, serializedObject);

        serializationStartTime = System.currentTimeMillis();
        serializedObject = jack2Seri.serialize(list);
        serializationTime = (System.currentTimeMillis() - serializationStartTime);
        bytes = serializedObject.length;
        deserializationStartTime = System.currentTimeMillis();
        jack2Seri.deserialize(serializedObject);
        deserializationTime = (System.currentTimeMillis() - deserializationStartTime);
        reportPerformance("Jackson2JsonRedisSerializer", serializationTime, bytes, deserializationTime, serializedObject);

        serializationStartTime = System.currentTimeMillis();
        serializedObject = gson.toJson(list).getBytes();
        serializationTime = (System.currentTimeMillis() - serializationStartTime);
        bytes = serializedObject.length;
        deserializationStartTime = System.currentTimeMillis();
        gson.fromJson(new String(serializedObject), Object.class);
        deserializationTime = (System.currentTimeMillis() - deserializationStartTime);
        reportPerformance("Gson", serializationTime, bytes, deserializationTime, serializedObject);
    }

    private static void reportPerformance(String serializer, long serializationTime, int bytes, long deserializationTime, byte[] serializedObject) {
        System.out.println(serializer);
        System.out.println("\tStart of serialization");
        System.out.println("\t\tSerialization ends, time-consuming: " + serializationTime + "ms");
        System.out.println("\t\tThe length after serialization is: " + bytes + " bytes");
        if (serializedObject != null) System.out.println("\t\tThe serialized representation: " + new String(serializedObject));
        System.out.println("\tBegin deserialization");
        System.out.println("\t\tDeserialization takes time: " + deserializationTime + "ms");
        System.out.println("==========================================================================================");
    }
}
