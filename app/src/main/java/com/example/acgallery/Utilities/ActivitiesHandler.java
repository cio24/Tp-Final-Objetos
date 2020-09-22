package com.example.acgallery.Utilities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ActivitiesHandler{
    private static HashMap<String, Object> buffer = new HashMap<>();
    private static Intent intent;

    public static void sendData(String key, Object value) {
        buffer.put(key,value);
    }
    public static Object getData(String key){
        return intent.getSerializableExtra(key);
    }

    public static void changeActivity(AppCompatActivity originActivity, Class destinationActivity){
        intent = new Intent(originActivity.getApplicationContext(),destinationActivity);
        for (Map.Entry<String,Object> set : buffer.entrySet()) {
            intent.putExtra(set.getKey(), (Serializable) set.getValue());
        }
        buffer.clear();
        originActivity.startActivity(intent);
        originActivity.finish();
    }


}
