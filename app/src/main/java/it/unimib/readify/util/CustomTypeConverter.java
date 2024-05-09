package it.unimib.readify.util;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;

public class CustomTypeConverter {
    @TypeConverter
    public static ArrayList<String> toBooks(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromBooks(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static ArrayList<OLWorkApiResponse> toWorks(String value) {
        Type listType = new TypeToken<List<OLWorkApiResponse>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromWorks(List<OLWorkApiResponse> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

}
