package com.example.acgallery;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.Nullable;

import com.example.acgallery.Classifiers.TensorFlowClassifier;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Filters.ClassifierFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ClassifierService extends IntentService {

    private static ArrayList<AbstractFile> pictures = null;
    private Folder folderToClassify;
    private static boolean finished;
    private final int MAX_LABELS = 50;

    public ClassifierService(){
        super("Classifier Service");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        finished = false;

        //getting the folder_thumbnail from to be displayed
        folderToClassify = (Folder) intent.getSerializableExtra("file");

        ArrayList<String> animalLabels = null;

        try {
            animalLabels = loadAnimalLabelList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClassifierFilter filter = new ClassifierFilter(getApplicationContext(), animalLabels, new TensorFlowClassifier(getApplicationContext()));
        pictures = folderToClassify.getDeepFilteredFiles(filter);
        try {
            InternalStorage.writeObject(this,"pictures",pictures);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finished = true;
    }

    private ArrayList<String> loadAnimalLabelList() throws IOException {
        ArrayList<String> labelList = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("animals.txt")));
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
            i++;
            if(i == MAX_LABELS)
                break;
        }
        reader.close();
        return labelList;
    }

    public static ArrayList<AbstractFile> getPictures(){
        if(finished)
            return pictures;
        return null;
    }

    public static boolean isFinished(){
        return finished;
    }

    public static void setFinished(boolean b){
        finished = b;
    }
}
