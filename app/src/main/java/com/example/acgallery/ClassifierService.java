package com.example.acgallery;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.acgallery.Activities.ThumbnailsActivity;
import com.example.acgallery.Classifiers.TensorFlowClassifier;
import com.example.acgallery.Composited.AbstractFile;
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.Filters.ClassifierFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ClassifierService extends IntentService {

    private static ArrayList<AbstractFile> pictures = null;
    private Folder folderToClassify;
    private static boolean finished;

    public ClassifierService(){
        super("Classifier Service");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("xenon","THE SERVICE HAS STARTED!");

        finished = false;

        //getting the folder_thumbnail from to be displayed
        folderToClassify = (Folder) intent.getSerializableExtra("idFolder");

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

        Log.d("xenon","THE SERVICE HAS FINISHED!");
        /*
        Intent i = new Intent(getApplicationContext(), ThumbnailsActivity.class);
        i.putExtra("pictures", files);

         */
    }

    private ArrayList<String> loadAnimalLabelList() throws IOException {
        ArrayList<String> labelList = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("animals.txt")));
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            //Log.d("LAAABEL","label of animals: " + line);
            labelList.add(line);
            i++;
            if(i == 50)
                break;
        }
        reader.close();
        if(labelList == null)
            Log.d("hipofisis","labelist es null!");
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

    public static void setFinished(){
        finished = true;
    }
}
