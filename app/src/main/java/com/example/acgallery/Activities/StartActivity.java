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
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.Composited.Picture;
import com.example.acgallery.R;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    // Request permission code
    final static int REQUEST_PERMISSION = 1;

    //time the icon of the app remains in the activity_start_layout screen
    final static int START_SCREEN_DELAY = 1000;

    // internal and external storage directory path
    final static String ROOT_PATH = "/mnt";

    // Images extensions
    final static String JPG_EXTENSION = ".jpg", PNG_EXTENSION = ".png";

    // Folders we want to track
    final static String DCIM = "DCIM", DOWNLOADS = "Download", SCREENSHOTS = "Screenshots";


    private boolean firstRequirement = true; //it is used to prevent the dialog screen to be shown when the app runs the first time.
    private List<String> extensions, directories;
    private String [] permissions;
    private File directoryRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //buinding the activity to the activity_start_layout layout.
        setContentView(R.layout.activity_start_layout);

        //we hide the actions buttons 'cause we copied the style from Google Photos
        getSupportActionBar().hide();

        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        directoryRoot = new File(ROOT_PATH);
        extensions = Arrays.asList(JPG_EXTENSION, PNG_EXTENSION);
        directories = Arrays.asList(DCIM,DOWNLOADS,SCREENSHOTS);

        Timer timer = new Timer();

        //we want to show the app icon like Google Photo does
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isPermissionsGranted())
                            startThumbnailsActivity();
                        else
                            askForPermissions();
                    }
                });
            }
        }, START_SCREEN_DELAY);
    }

    /*
        this methods runs automatically when the app starts, so
        we have to control the result of the request of the permissions
        so we show a message explaining the user why we need them when the permissions are noy accepted
        in the other case we continue loading the pictures and showing them
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                startThumbnailsActivity();
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

    /*
    this method load the root folder_thumbnail and puts it in an intent
    then starts the activity_thumbnails_layout activity so it can get the folder_thumbnail to displayed
    */
    private void startThumbnailsActivity(){
        Folder rootFolder = getFolderRootLoaded();
        Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
        intent.putExtra("idFolder", rootFolder);
        startActivity(intent);
        finish();
    }

    //it asks the user for permissions so the app can have access to the files.
    private void askForPermissions() {
        for(String permission: permissions)
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartActivity.this, permissions, REQUEST_PERMISSION); //we ask for permissions
            }
    }

    /*
        this method creates a Folder and adds all the folders we want to track,
        loaded with pictures and other folders
     */
    private Folder getFolderRootLoaded(){
        Folder folderRoot = new Folder(directoryRoot);
        File directory;
        for (String directoryName: directories) {
            directory = findDirectory(directoryName, directoryRoot);
            if(directory != null) {
                Folder folder = new Folder(directory);
                loadFolder(folder, directory); // load pictures and folders into the BIG folder_thumbnail
                folderRoot.add(folder);
            }
        }
        return folderRoot;
    }

    //it loads all the pictures from the directory source into the given folder_thumbnail
    private void loadFolder(Folder folderToLoad,File directorySource){
        File[] files = directorySource.listFiles();
        if(files != null){ // Directory not empty
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) {
                    for(String extension: extensions){
                        if(files[i].getName().endsWith(extension)){
                            Picture picture = new Picture (files[i]);
                            folderToLoad.add(picture);
                        }
                    }
                }
                else { // the file is a directory
                    Folder folder = new Folder(files[i]);
                    loadFolder(folder, files[i]); // deep loading
                    if(folder.getFilesAmount() > 0)
                        folderToLoad.add(folder);
                }
            }
            if(folderToLoad.getFilesAmount() == 0){
                Folder container = folderToLoad.getContainer();
                if(container != null){
                    container.deleteFile(folderToLoad);
                }
            }
        }
    }

    //it searchs for a directory with the given name in the directory source
    private File findDirectory(String directoryName, File directorySource) {
        File[] files = directorySource.listFiles();
        if (files != null) {
            File aux;
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if(files[i].getName().equals(directoryName)){
                        return files[i];
                    }
                    aux = findDirectory(directoryName, files[i]); // deep searching
                    if(aux != null){
                        return aux;
                    }
                }
            }
        }
        return null; // the directory is not on the storage
    }

    //it checks wether the app has the required permissions or not
    private boolean isPermissionsGranted(){
        for(String permission: permissions)
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        return true;
    }

}