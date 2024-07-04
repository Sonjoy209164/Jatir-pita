package com.example.myaudiobook.classes;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataSaver {
    private static SharedPreferences sp;
    private static DataSaver dataSaver;

    public static DataSaver getInstance(Activity activity){
        if(dataSaver == null){
            dataSaver = new DataSaver(activity);
        }

        return dataSaver;
    }

    public void saveAudioTime(String bookKey, long timePlayed){
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(bookKey,timePlayed);
        editor.apply();
    }

    public Long getLastTime(String key){
        return sp.getLong(key,0);
    }

    public Map<String,Long> readAll(){
        Map<String, ?> allEntries = sp.getAll();
        Set<String> keys = allEntries.keySet();


        Map<String, Long> map = new HashMap<>();
        for (String key : keys) {
            map.put(key,sp.getLong(key,0));
        }

        return map;

    }

    private DataSaver(Activity activity) {
        sp = activity.getSharedPreferences("saved_sp", MODE_PRIVATE);
    }
}
