package com.example.acgallery.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/*
    this is the starting activity that has to search for pictures in some directories
    also has to request for permissions to read an write the internal and external storage
    and run a background service that classifies pictures in order to create a dynamic album
    in this case of animal pictures
 */
public class StartActivity extends AppCompatActivity {

    private final static int REQUEST_PERMISSION = 1; // Request permission code
    private final static int START_SCREEN_DELAY = 1000; //time the icon of the app remains in the activity_start_layout screen

    private boolean firstRequirement = true; //it is used to prevent the dialog screen to be shown when the app runs the first time.
    private String [] permissions;
    private Folder folderRoot;

    private  ArrayList<String> innerDataPaths, directories, excludedDirectories, extensions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_layout); //binding the activity to the activity_start_layout layout.
        getSupportActionBar().hide(); //we hide the actions buttons 'cause we copied the style from Google Photos

        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        try {
            extensions = loadTextResource("targetedExtensions");
            innerDataPaths = loadTextResource("scannedPaths");
            directories = loadTextResource("trackedDirectories");
            excludedDirectories = loadTextResource("excludedDirectories");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final StartActivity originActivity = this;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() { //we want to show the app icon like Google Photo does too
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isPermissionsGranted = true;
                        for(String permission: permissions)
                            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
                                isPermissionsGranted = false;
                        if(isPermissionsGranted){
                            folderRoot = FileManager.getFolderRootLoaded(directories,innerDataPaths,extensions,excludedDirectories);
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
                folderRoot = FileManager.getFolderRootLoaded(directories,innerDataPaths,extensions,excludedDirectories);
                if(!AnimalsClassifierService.isFinished(this))
                    runAnimalPicturesService();
                Log.d("asd","items " + folderRoot.getItemsNumber(new TrueFilter()));
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

    //this method runs in the background with a thread, the service that classifies the pictures
    private void runAnimalPicturesService(){
        ArrayList<String> animalLabels = null;
        try {
            animalLabels = loadTextResource("animalLabels");
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnimalsClassifierService.setLabelList(animalLabels);
        Intent intent = new Intent(this, AnimalsClassifierService.class);
        intent.putExtra("file",folderRoot);
        startService(intent);
    }

    //it asks the user for permissions so the app can have access to the files.
    private void askForPermissions() {
        for(String permission: permissions)
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartActivity.this, permissions, REQUEST_PERMISSION); //we ask for permissions
            }
    }

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