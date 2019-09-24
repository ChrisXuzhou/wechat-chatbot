package com.rokid.iot.portal.share;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonHelper {

    private static Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T convert(Object obj, Class<T> classOfT) {

        String j = JsonHelper.toJson(obj);
        return JsonHelper.fromJson(
                j,
                classOfT
        );
    }

    public static <T> T convert(Object obj, Type typeOfT) {
        return JsonHelper.fromJson(
                JsonHelper.toJson(obj),
                typeOfT
        );
    }

    public static Map toMap(String json) {
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        return JsonHelper.fromJson(json, mapType);
    }

}
