/*
 * this abstract class generalizes the behavior of an activity that shows pictures in full screen mode
 * and shows a menu of actions like move, remove, etc. Additionally it lets you to display in full screen
 *  mode and zoom the picture.
 */

package com.example.acgallery.Controller.Activities.FullPictureActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.acgallery.Controller.ActivitiesHandler;
import com.example.acgallery.Controller.Activities.ThumbnailsActivities.PasteThumbnailsActivity;
import com.example.acgallery.Controller.Adapters.FullPictureAdapter;
import com.example.acgallery.Model.Composite.AbstractFile;
import com.example.acgallery.Model.Composite.Folder;
import com.example.acgallery.Model.Composite.Picture;
import com.example.acgallery.R;
import java.util.ArrayList;

public abstract class FullPictureActivity extends AppCompatActivity{
    private Picture pictureToShow, currentPictureDisplayed;
    private Folder folderToReturn;
    private ArrayList<AbstractFile> picturesToShow;
    private ViewPager viewPager;
    private int currentPicturePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture_layout);

        //changing the status bar color
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        //the following action are taken in order to hide the title of the activity and show a icon instead
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //retrieving the picture to be shown
        pictureToShow = (Picture) ActivitiesHandler.getData("pictureToShow");
        folderToReturn = (Folder) ActivitiesHandler.getData("folderToReturn");
        picturesToShow = getPicturesToShow();
        viewPager = findViewById(R.id.view_pager);

        currentPicturePos = getCurrentPicturePosition();

        FullPictureAdapter adapter = new FullPictureAdapter(this, picturesToShow, getSupportActionBar());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPicturePos);
    }

    private int getCurrentPicturePosition(){
        int currentPicturePos = 0;
        for(int i = 0; i < picturesToShow.size(); i++){
            if(picturesToShow.get(i).equals(pictureToShow))
                break;
            currentPicturePos++;
        }

        return currentPicturePos;
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
        Folder folderRoot = currentPictureDisplayed.getParent().getFolderRoot();

        switch (item.getItemId()) {
            case R.id.delete_picture_op:
                final AppCompatActivity originActivity = this;

                new AlertDialog.Builder(originActivity)
                        .setTitle("Delete picture permanently?")
                        .setMessage("If you delete this item, it will be removed permanently from your device.")
                        .setPositiveButton("Delete permanently", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //we have to control if we are erasing the last picture of the folder or not
                                //so we know what is the nextPicture to show
                                int posNextPicture = currentPicturePos;
                                if(posNextPicture == picturesToShow.size() - 1)
                                    posNextPicture--;

                                //we delete the picture and make sure that won't remain an empty file of it.
                                currentPictureDisplayed.delete();
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(currentPictureDisplayed.getRealFile())));

                                //if it was the last picture of the album we return to the folder activity
                                if(picturesToShow.isEmpty())
                                    onBackPressed();
                                else{
                                    //we show the next picture to the folder where the picture was
                                    ActivitiesHandler.sendData("folderToReturn",folderToReturn);
                                    ActivitiesHandler.sendData("pictureToShow",picturesToShow.get(posNextPicture));
                                    ActivitiesHandler.changeActivity(originActivity,originActivity.getClass());
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //close the Alert Dialog
                            }
                        }).create().show();
                return true;

            case R.id.details_picture_op:
                //we shot the units according the size
                String units;

                float pictureSize = (float) currentPictureDisplayed.size();
                if(pictureSize/(1024*1024) > 1.0){
                    pictureSize = pictureSize/(1024*1024);
                    units = "MB";
                }
                else{
                    units = "KB";
                    pictureSize = pictureSize/1024;
                }

                //finally we show all the info about this picture
                new AlertDialog.Builder(this)
                        .setTitle(currentPictureDisplayed.getName())
                        .setMessage(
                                "\n" + "Dimensions: " + currentPictureDisplayed.getWidth() + " x " + currentPictureDisplayed.getHeight() + "\n" + "\n" +
                                        "Size: " + pictureSize + " " + units + "\n" + "\n" +
                                        "Path: " + currentPictureDisplayed.getAbsolutePath() + "\n" + "\n" +
                                        "Taken on: " + currentPictureDisplayed.getCreationTime()
                        )
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //close the Alert Dialog
                            }
                        })
                        .create().show();
                return true;

            case R.id.rename_picture_op:
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
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.copy_picture_op:
                ActivitiesHandler.sendData("folderToShow",folderRoot);
                ActivitiesHandler.sendData("fileToPaste", currentPictureDisplayed);
                ActivitiesHandler.sendData("opCode",0);
                ActivitiesHandler.changeActivity(this, PasteThumbnailsActivity.class);
                return true;
            case R.id.move_picture_op:
                currentPictureDisplayed.getParent().removeFile(currentPictureDisplayed);
                ActivitiesHandler.sendData("folderToShow",folderRoot);
                ActivitiesHandler.sendData("fileToPaste", currentPictureDisplayed);
                ActivitiesHandler.sendData("opCode",1);
                ActivitiesHandler.changeActivity(this, PasteThumbnailsActivity.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected Folder getFolderToReturn(){
        return this.folderToReturn;
    }
    protected Picture getPictureToShow(){
        return this.pictureToShow;
    }

    protected abstract ArrayList<AbstractFile> getPicturesToShow();
}
