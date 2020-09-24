package com.example.acgallery.Controller;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.example.acgallery.Model.Classifiers.TensorFlowClassifier;
import com.example.acgallery.Model.Composite.AbstractFile;
import com.example.acgallery.Model.Composite.Folder;
import com.example.acgallery.Model.Filters.ClassifierFilter;

import java.io.IOException;
import java.util.ArrayList;

public class AnimalsClassifierService extends IntentService {
    private static ArrayList<AbstractFile> pictures = null;
    private static ArrayList<String> labels;

    public AnimalsClassifierService(){
        super("Classifier Service");
    }
    public static void setLabelList(ArrayList<String> animalLabels){
        labels = animalLabels;
    }

    public static boolean isFinished(Context context){
        if(pictures == null)
            return getPictures(context) != null;
        return true;
    }

    public static ArrayList<AbstractFile> getPictures(Context context){
        if(pictures != null)
            return pictures;

        ArrayList<AbstractFile> animalPictures = null;
        try {
            animalPictures = (ArrayList<AbstractFile>) FileManager.readObject(context,"pictures");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return animalPictures;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //getting the folder_thumbnail from to be displayed
        Folder folderToClassify = (Folder) intent.getSerializableExtra("filesToClassify");

        ClassifierFilter filter = new ClassifierFilter(getApplicationContext(), labels, new TensorFlowClassifier(getApplicationContext()));
        pictures = folderToClassify.getDeepFilteredFiles(filter);
        try {
            FileManager.writeObject(this,"pictures",pictures);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
