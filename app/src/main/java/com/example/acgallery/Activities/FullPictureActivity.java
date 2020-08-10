package com.example.acgallery.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.acgallery.Adapters.SwipeAdapter;
import com.example.acgallery.ClassifierService;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Composite.Picture;
import com.example.acgallery.Filters.PictureFilter;
import com.example.acgallery.InternalStorage;
import com.example.acgallery.R;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/*
    this activity open a selected picture and shows a menu of actions like move, remove, etc.
     Additionally it lets you to display in full screen mode and zoom the picture.
 */
public class FullPictureActivity extends AppCompatActivity {

    private Picture pictureToShow, currentPictureDisplayed;
    private Folder folderToReturn;
    private ArrayList<AbstractFile> picturesToShow;
    private ViewPager viewPager;
    private boolean filteredPictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture_layout);

        //changing the status bar color
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //retrieving the picture to be shown
        pictureToShow = (Picture) ActivitiesHandler.getData("pictureToShow");
        Log.d("axax","se tiene que mostrar:" + pictureToShow.getName());
        filteredPictures = (boolean) ActivitiesHandler.getData("filteredPictures");
        if(filteredPictures){
            folderToReturn = (Folder) ActivitiesHandler.getData("folderToReturn");

            if((boolean) ActivitiesHandler.getData("all"))
                picturesToShow = pictureToShow.getParent().getFolderRoot().getDeepFilteredFiles(new PictureFilter());
            else{
                try {
                    picturesToShow = (ArrayList<AbstractFile>) InternalStorage.readObject(this,"pictures");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(picturesToShow == null)
                    picturesToShow = ClassifierService.getPictures();
            }
        }
        else{
            picturesToShow = pictureToShow.getParent().getFilteredFiles(new PictureFilter());
            folderToReturn = pictureToShow.getParent();
        }

        viewPager = findViewById(R.id.view_pager);

        Log.d("axax","cantidad de fotos a mostrar:" + picturesToShow.size());
        int currentPicturePos = 0;
        for(int i = 0; i < picturesToShow.size(); i++){
            Log.d("axax","real: " + pictureToShow.getName() + " pos i: " + picturesToShow.get(i).getName());

            if(picturesToShow.get(i).equals(pictureToShow))
                break;
            currentPicturePos++;


        }
        SwipeAdapter adapter = new SwipeAdapter(this, picturesToShow, getSupportActionBar());
        viewPager.setAdapter(adapter);
        //Log.d("axax","se mostra:" + picturesToShow.get(currentPicturePos).getName());
        viewPager.setCurrentItem(currentPicturePos);
    }

    //this method shows a menu layout over the activity_thumbnails_layout layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.picture_menu, menu);
        return true;
    }

    //in this method we indicate the actions to be taken for each option of the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        currentPictureDisplayed = (Picture) picturesToShow.get(viewPager.getCurrentItem());

        if(item.getItemId() == R.id.delete_picture_op){
            final AppCompatActivity originActivity = this;
            new AlertDialog.Builder(originActivity)
                    .setTitle("Delete picture permanently?")
                    .setMessage("If you delete this item, it will be removed permanently from your device.")
                    .setPositiveButton("Delete permanently", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Folder folderToShow;
                            Class destinationActivity;
                            if(filteredPictures){
                                folderToShow = currentPictureDisplayed.getParent().getFolderRoot();
                                destinationActivity = AllPicturesActivity.class;
                            }
                            else{
                                folderToShow = currentPictureDisplayed.getParent();
                                destinationActivity = ThumbnailsActivity.class;
                            }

                            //we delete the picture and make sure that won't remain an empty file of it.
                            currentPictureDisplayed.delete();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(currentPictureDisplayed.getRealFile())));

                            //then we comeback to the folder where the picture was

                            ActivitiesHandler.sendData("folderToShow",folderToShow);
                            ActivitiesHandler.changeActivity(originActivity,destinationActivity);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the Alert Dialog
                        }
                    }).create().show();
        }
        else if(item.getItemId() == R.id.details_picture_op){

            //all of this is necessary to get the real dimensions of the picture
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPictureDisplayed.getAbsolutePath(), options);
            int height = options.outHeight;
            int width = options.outWidth;

            //all this things are needed to show the creationTime
            String time = null;
            BasicFileAttributes attributes = null;
            Path filePath = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                filePath = Paths.get(currentPictureDisplayed.getAbsolutePath());
                try {
                    attributes = Files.readAttributes(filePath,BasicFileAttributes.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                time = attributes.creationTime().toString();
            }

            //we shot the units according the size
            String units;
            float size;
            float pictureSize = (float) currentPictureDisplayed.getRealFile().length();
            if(pictureSize/(1024*1024) > 1.0){
                size = pictureSize/(1024*1024);
                units = "MB";
            }
            else{
                units = "KB";
                size = pictureSize/1024;
            }

            //finally we show all the info about this picture
            assert time != null;
            new AlertDialog.Builder(this)
                    .setTitle(currentPictureDisplayed.getName())
                    .setMessage(
                            "Dimensions: " + width + " x " + height + "\n" +
                                    "Size: " + size + " " + units + "\n" +
                                    "Path: " + currentPictureDisplayed.getAbsolutePath() + "\n" +
                                    "Taken on: " + time.substring(0,time.indexOf("T"))
                    )
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the Alert Dialog
                        }
                    })
                    .create().show();
        }
        else if(item.getItemId() == R.id.rename_picture_op){
            //we defined an editText to read the input of the user and let the user select all the text of old name
            final EditText inputNewName = new EditText(this);
            inputNewName.setText(currentPictureDisplayed.getName().substring(0, currentPictureDisplayed.getName().lastIndexOf(".")));
            inputNewName.setSelectAllOnFocus(true);

            new AlertDialog.Builder(this)
                    .setView(inputNewName)
                    .setTitle("Rename")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!currentPictureDisplayed.rename(inputNewName.getText().toString()))
                                Toast.makeText(getApplicationContext(),"The name couldn't be changed!",Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the Alert Dialog
                        }
                    })
                    .create().show();
        }
        else{
            Folder folderRoot = currentPictureDisplayed.getParent().getFolderRoot();
            if(item.getItemId() == R.id.copy_picture_op || item.getItemId() == R.id.move_picture_op) {
                ActivitiesHandler.sendData("folderToShow",folderRoot);
                Folder folderToReturn;
                if(filteredPictures)
                    folderToReturn = pictureToShow.getParent().getFolderRoot();
                else
                    folderToReturn = pictureToShow.getParent();
                ActivitiesHandler.sendData("folderToReturn",folderToReturn);
                ActivitiesHandler.sendData("fileToPaste", currentPictureDisplayed);
                if(item.getItemId() == R.id.copy_picture_op)
                    ActivitiesHandler.sendData("opCode",0);
                else
                    ActivitiesHandler.sendData("opCode",1);

                ActivitiesHandler.changeActivity(this,PasteActivity.class);
            }
        }
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        this method remove all the abstract files that don't have a inner file which path is linked
        wieth a real file in the phone.
    */
    @Override
    public void onBackPressed() {
        ActivitiesHandler.sendData("folderToShow", folderToReturn);
        ActivitiesHandler.changeActivity(this,ThumbnailsActivity.class);
    }
}