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

    private static int NUMBER_OF_MOVIES = 1_000;

    public static void main(String[] args) {

        if (args != null && args.length > 0) {
            try {
                NUMBER_OF_MOVIES = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.err.println("The value specified is an invalid number '" + args[0] + "'.\nUsing default number of movies: " + NUMBER_OF_MOVIES);
            }
        }

        List<Movie> list = prepareData();

        useJdkSerializationRedisSerializer(list);
        useGenericJackson2JsonRedisSerializer(list);
        useJackson2JsonRedisSerializer(list);
        useGson(list);
    }

    private static List<Movie> prepareData() {
        List<Movie> list = new ArrayList<>();
        long startDataPreparationTime = System.currentTimeMillis();
        System.out.print("Data preparation for " + NUMBER_OF_MOVIES + " objects of type Movie...");
        for(int i = 0; i < NUMBER_OF_MOVIES; i++){
            list.add(generateMovie());
        }
        long endDataPreparationTime = System.currentTimeMillis() - startDataPreparationTime;
        System.out.println(" done in " + endDataPreparationTime + "ms. Preparation avg took " + (endDataPreparationTime/NUMBER_OF_MOVIES) + "ms each.");
        System.out.println("");
        return list;
    }
    private static Movie generateMovie() {
        return new Movie(generateTitle(),generateRating(),generateYear());
    }
    private static String generateTitle() {
        return new Faker().book().title();
    }
    private static int generateRating() {
        return  (int) ((Math.random() * (4)) + 1);
    }
    private static int generateYear() {
        return (int) ((Math.random() * (72)) + 1950);
    }

    private static void useJdkSerializationRedisSerializer(List<Movie> toSerialize) {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
        long serializationStartTime = System.currentTimeMillis();
        byte[] serializedObject = serializer.serialize(toSerialize);
        long serializationTime = (System.currentTimeMillis() - serializationStartTime);
        int bytes = serializedObject.length;
        long deserializationStartTime = System.currentTimeMillis();
        serializer.deserialize(serializedObject);
        long deserializationTime = (System.currentTimeMillis() - deserializationStartTime);
        reportPerformance(serializer.getClass().getSimpleName(), serializationTime, bytes, deserializationTime, serializedObject);
    }
    private static void useGenericJackson2JsonRedisSerializer(List<Movie> toSerialize) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        long serializationStartTime = System.currentTimeMillis();
        byte[] serializedObject = serializer.serialize(toSerialize);
        long serializationTime = (System.currentTimeMillis() - serializationStartTime);
        int bytes = serializedObject.length;
        long deserializationStartTime = System.currentTimeMillis();
        serializer.deserialize(serializedObject);
        long deserializationTime = (System.currentTimeMillis() - deserializationStartTime);
        reportPerformance(serializer.getClass().getSimpleName(), serializationTime, bytes, deserializationTime, serializedObject);
    }
    private static void useJackson2JsonRedisSerializer(List<Movie> toSerialize) {
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        long serializationStartTime = System.currentTimeMillis();
        byte[] serializedObject = serializer.serialize(toSerialize);
        long serializationTime = (System.currentTimeMillis() - serializationStartTime);
        int bytes = serializedObject.length;
        long deserializationStartTime = System.currentTimeMillis();
        serializer.deserialize(serializedObject);
        long deserializationTime = (System.currentTimeMillis() - deserializationStartTime);
        reportPerformance(serializer.getClass().getSimpleName(), serializationTime, bytes, deserializationTime, serializedObject);
    }
    private static void useGson(List<Movie> toSerialize) {
        Gson serializer = new GsonBuilder().serializeNulls().create();;
        long serializationStartTime = System.currentTimeMillis();
        byte[] serializedObject = serializer.toJson(toSerialize).getBytes();
        long serializationTime = (System.currentTimeMillis() - serializationStartTime);
        int bytes = serializedObject.length;
        long deserializationStartTime = System.currentTimeMillis();
        serializer.fromJson(new String(serializedObject), Object.class);
        long deserializationTime = (System.currentTimeMillis() - deserializationStartTime);
        reportPerformance(serializer.getClass().getSimpleName(), serializationTime, bytes, deserializationTime, serializedObject);
    }

    private static void reportPerformance(String serializer, long serializationTime, int bytes, long deserializationTime, byte[] serializedObject) {
        System.out.println(serializer);
        System.out.println("\tStart of serialization");
        System.out.println("\t\tSerialization ends, time-consuming: " + serializationTime + "ms");
        System.out.println("\t\tThe length after serialization is: " + bytes + " bytes");
        //if (serializedObject != null) System.out.println("\t\tThe serialized representation: " + new String(serializedObject));
        System.out.println("\tBegin deserialization");
        System.out.println("\t\tDeserialization takes time: " + deserializationTime + "ms");
        System.out.println("==========================================================================================");
    }
}
