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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    //it is used to prevent the dialog screen when the app runs the first time.
    private boolean firstRequirement = true;

    // Request permission code
    final static int REQUEST_PERMISSION = 1;

    final static String ROOT_PATH = "/mnt";

    // Images extensions
    final static String JPG_EXTENSION = ".jpg", PNG_EXTENSION = ".png";

    // Folders we want to track
    final static String DCIM = "DCIM", DOWNLOADS = "Downloads", SCREENSHOTS = "Screenshots";

    private String [] permissions;
    private Folder folderRoot;
    private ArrayList<String> extensions, directories; //all image extensions and directories to be found
    private File directoryRoot; //directory root of phone storage



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        getSupportActionBar().hide();


        // Permissions required to read/write internal storage
        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        directoryRoot = new File(ROOT_PATH);

        extensions = new ArrayList<>();
        extensions.add(JPG_EXTENSION); //extensions.add(PNG_EXTENSION);

        directories = new ArrayList<>();
        directories.add(DCIM); directories.add(DOWNLOADS); directories.add(SCREENSHOTS);

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ConstraintLayout layout = findViewById(R.id.start);
                        layout.removeViewAt(0);
                        if(permissionsGranted())
                            folderRoot = loadAllFolders();
                        else
                            askForPermissions();
                        Toast.makeText(getApplicationContext(), "intentando iniciar activity", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("idFolder",folderRoot);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }, 1000);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();

                folderRoot = loadAllFolders();
                // future code here, too?
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

    private Folder loadAllFolders(){
        Folder folderRoot = new Folder(directoryRoot);
        File directory;
        for (String directoryName: directories) {
            directory = findDirectory(directoryName, directoryRoot);
            if(directory != null) {
                Folder folder = new Folder(directory);
                /*
                    Bajé esa definición de folder porque por cómo definí el nuevo constructor es
                    necesario que directory esté inicializado desde antes. Además, no tiene
                    sentido crear un folder si el directorio luego va a ser nulo.
                    Thank me later :3
                 */
                loadFolder(folder, directory); // load pictures and folders into the BIG folder
                folderRoot.add(folder);
            }
            // else, the directory is not on the storage
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
                            Picture picture = new Picture (files[i].length(),extension, files[i]);
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