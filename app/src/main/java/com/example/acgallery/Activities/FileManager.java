package com.example.acgallery.Activities;

import android.content.Context;
import android.content.Intent;

import com.example.acgallery.Composite.AbstractFile;

import java.io.Serializable;

public class FileManager implements Serializable {
    private static String id = "undefined";

    public static String getId(){
        return id;
    }

    public static void sendFile(AbstractFile file, Context context, Class cls){
        Intent intent = new Intent(context, cls);
        id = file.getName();
        intent.putExtra(file.getName(),file);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


}
