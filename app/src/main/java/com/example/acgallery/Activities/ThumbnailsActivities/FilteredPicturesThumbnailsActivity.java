package com.example.acgallery.Activities.ThumbnailsActivities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.R;
import com.example.acgallery.Utilities.ActivitiesHandler;
import java.util.ArrayList;

public abstract class FilteredPicturesThumbnailsActivity extends AppCompatActivity {
    private Folder folderToReturn;
    private ArrayList<AbstractFile> picturesToShow;
    final static int ROWS_OF_GRID = 4; //Number of rows of pics showed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding the activity to the activity_thumbnails_layout layout.
        setContentView(R.layout.activity_thumbnails_layout);

        this.folderToReturn = (Folder) ActivitiesHandler.getData("folderToReturn");
        this.picturesToShow = getPicturesToShow();

        //defining the adapter which will handle the binding between the views and the layout
        RecyclerView.Adapter adapter = getAdapter();

        //showing the name of the folder
        getSupportActionBar().setTitle(getActionBarTitle());

        //getting the reference of the recycler view inside the activity_thumbnails_layout layout
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //setting a grid layout manager to the recycler
        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));

        //setting the adapter defined previously to the recycler
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        ActivitiesHandler.sendData("folderToShow",folderToReturn);
        ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
    }

    //this method shows a menu layout over the activity_thumbnails_layout layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filtered_pictures_menu, menu);
        return true;
    }

    //in this method we indicate the actions to be taken for each option of the menu
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.details_filtered_pictures_op:
                //we show the units according the size
                String units;

                float size = 0;
                int n = picturesToShow.size();
                for(int i = 0; i < n; i++){
                    size += picturesToShow.get(i).size();
                }

                if(size/(1024*1024*1024) > 1.0) {
                    units = "GB";
                }
                else if(size/(1024*1024) > 1.0){
                    size = size/(1024*1024);
                    units = "MB";
                }
                else{
                    units = "KB";
                    size = size/1024;
                }

                new AlertDialog.Builder(this)
                        .setMessage(
                                "Pictures: " + picturesToShow.size() + "\n" + "\n" +
                                        "Total size: " + size  + " " + units
                        )
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //close the Alert Dialog
                            }
                        })
                        .create().show();
                return true;

            case R.id.backFolderToReturn_op:
                ActivitiesHandler.sendData("folderToShow", folderToReturn);
                ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Folder getFolderToReturn(){
        return this.folderToReturn;
    }

    protected abstract ArrayList<AbstractFile> getPicturesToShow();
    protected abstract RecyclerView.Adapter getAdapter();
    protected abstract String getActionBarTitle();
}
