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
import androidx.constraintlayout.widget.ConstraintLayout;
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

    //time the icon remains in the start screen
    final static int START_SCREEN_DELAY = 1000;

    // internal and external storage directory path
    final static String ROOT_PATH = "/mnt";

    // Images extensions
    final static String JPG_EXTENSION = ".jpg", PNG_EXTENSION = ".png";

    // Folders we want to track
    final static String DCIM = "DCIM", DOWNLOADS = "Download", SCREENSHOTS = "Screenshots";


    private boolean firstRequirement = true; //it is used to prevent the dialog screen when the app runs the first time.
    private List<String> extensions, directories;
    private String [] permissions;
    private File directoryRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        getSupportActionBar().hide();

        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        directoryRoot = new File(ROOT_PATH);
        extensions = Arrays.asList(JPG_EXTENSION, PNG_EXTENSION);
        directories = Arrays.asList(DCIM,DOWNLOADS,SCREENSHOTS);

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(permissionsGranted())
                            startThumbnailsActivity();
                        else
                            askForPermissions();
                    }
                });
            }
        }, START_SCREEN_DELAY);
    }

    private void startThumbnailsActivity(){
        Folder folderRoot = getFolderRootLoaded();
        Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
        intent.putExtra("idFolder", folderRoot);
        startActivity(intent);
        finish();
    }


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

    private void askForPermissions() {
        for(String permission: permissions)
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartActivity.this, permissions, REQUEST_PERMISSION); //we ask for permissions
            }
    }

    private Folder getFolderRootLoaded(){
        Folder folderRoot = new Folder(directoryRoot);
        File directory;
        for (String directoryName: directories) {
            directory = findDirectory(directoryName, directoryRoot);
            if(directory != null) {
                Folder folder = new Folder(directory);
                loadFolder(folder, directory); // load pictures and folders into the BIG folder
                folderRoot.add(folder);
            }
        }
        return folderRoot;
    }

    //it loads all the pictures from the directory source into the given folder
    private void loadFolder(Folder folderToLoad,File directorySource){
        File[] files = directorySource.listFiles();
        if(files != null){ // Directory not empty
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) {
                    for(String extension: extensions){
                        if(files[i].getName().endsWith(extension)){
                            Log.d("picFound", "nombre:" + files[i].getName() + " found!");
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

    private boolean permissionsGranted(){
        for(String permission: permissions)
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        return true;
    }


}