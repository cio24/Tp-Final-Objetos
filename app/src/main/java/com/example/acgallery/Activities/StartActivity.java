/*
 * this is the starting activity that has to request for permissions to read an write the internal and external storage
 * then it search for pictures in some directories. It also run a background service that classifies pictures in order
 * to create a dynamic album of animals pictures
 */

package com.example.acgallery.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.acgallery.Activities.ThumbnailsActivities.FolderThumbnailsActivity;
import com.example.acgallery.Utilities.ActivitiesHandler;
import com.example.acgallery.Utilities.AnimalsClassifierService;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Utilities.FileManager;
import com.example.acgallery.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    private final static int REQUEST_PERMISSION = 1;
    private final static int START_SCREEN_DELAY = 1000;

    //it is used to prevent the dialog screen to be shown when the app runs the first time.
    private boolean firstRequirement = true;

    private String [] permissionsCodes;
    private Folder folderRoot;
    private  ArrayList<String> pathsToScan, directoriesToTrack, directoriesToExclude, targetedExtensions;

    //this method runs automatically when the activity is created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding the activity to the activity_start_layout layout.
        setContentView(R.layout.activity_start_layout);

        //we hide the actions buttons 'cause we copied the style from Google Photos
        getSupportActionBar().hide();

        permissionsCodes = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        try {
            targetedExtensions = loadTextResource("targetedExtensions");
            pathsToScan = loadTextResource("pathsToScan");
            directoriesToTrack = loadTextResource("directoriesToTrack");
            directoriesToExclude = loadTextResource("directoriesToExclude");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
            we save the instance of the activity to use it in the AnimalsClassifierService because we can't use
            "this" inside the timer.schedule since it will reference the timer.scheduler itself.
         */
        final StartActivity originActivity = this;

        /*
            the following timer it is used to show this activity after a given time so the app icon can be seen
             for a while (like google photos does)
         */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isPermissionsGranted = true;

                        for(String permission: permissionsCodes) //we check whether we have all the permissions or not
                            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
                                isPermissionsGranted = false;

                        if(isPermissionsGranted){
                            folderRoot = FileManager.getFolderRootLoaded(directoriesToTrack, pathsToScan, targetedExtensions, directoriesToExclude);

                            if(!AnimalsClassifierService.isFinished(originActivity))
                                runAnimalPicturesService();

                            ActivitiesHandler.sendData("folderToShow",folderRoot);
                            ActivitiesHandler.changeActivity(originActivity, FolderThumbnailsActivity.class);
                        }
                        else
                            askForPermissions();
                    }
                });
            }
        }, START_SCREEN_DELAY);
    }

    /*
        this method runs automatically when the app starts, so we have to control the result of the request
        of the permissions. when the permissions are not accepted we show a message explaining the user why
        we need them, in the other case we continue loading the pictures and showing them
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                folderRoot = FileManager.getFolderRootLoaded(directoriesToTrack, pathsToScan, targetedExtensions, directoriesToExclude);

                if(!AnimalsClassifierService.isFinished(this))
                    runAnimalPicturesService();

                ActivitiesHandler.sendData("folderToShow",folderRoot);
                ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
            }
            else{
                if(firstRequirement)
                    firstRequirement = false; //next requirement won't be the first one
                else{
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Required")
                            .setMessage("the permission is needed to access the pictures!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    askForPermissions();
                                }
                            })
                            .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).create().show();

                    firstRequirement = true;
                }
            }
        }
    }

    //this method start a service that runs through a thread in the background
    private void runAnimalPicturesService(){
        ArrayList<String> animalLabels = null;

        try {
            animalLabels = loadTextResource("animalsLabels");
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnimalsClassifierService.setLabelList(animalLabels);

        Intent intent = new Intent(this, AnimalsClassifierService.class);
        intent.putExtra("filesToClassify",folderRoot);
        startService(intent);
    }

    private void askForPermissions() {
        for(String permission: permissionsCodes)
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartActivity.this, permissionsCodes, REQUEST_PERMISSION); //we ask for permissions
            }
    }

    //this method can load a txt file that is saved in the assets folder of the project
    private ArrayList<String> loadTextResource(String directoryName) throws IOException {
        ArrayList<String> textList = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("textResources/" + directoryName + ".txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            textList.add(line);
        }
        reader.close();
        return textList;
    }
}