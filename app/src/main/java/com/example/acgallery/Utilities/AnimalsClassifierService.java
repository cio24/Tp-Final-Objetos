package com.example.acgallery.Utilities;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.example.acgallery.Classifiers.TensorFlowAnimalsClassifier;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Filters.ClassifierFilter;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.acgallery.Utilities.FileManager.readObject;

public class AnimalsClassifierService extends IntentService {

    private static ArrayList<AbstractFile> pictures = null;
    private static ArrayList<String> labels;
    private Folder folderToClassify;

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
        folderToClassify = (Folder) intent.getSerializableExtra("file");

        ClassifierFilter filter = new ClassifierFilter(getApplicationContext(), labels, new TensorFlowAnimalsClassifier(getApplicationContext()));
        pictures = folderToClassify.getDeepFilteredFiles(filter);
        try {
            FileManager.writeObject(this,"pictures",pictures);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
