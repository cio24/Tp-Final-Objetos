package com.example.acgallery.Activities.ThumbnailsActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Utilities.ActivitiesHandler;
import com.example.acgallery.Adapters.ThumbnailsAdapters.PasteThumbnailAdapter;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;
import com.example.acgallery.Utilities.AnimalsClassifierService;

public class PasteThumbnailsActivity extends AppCompatActivity {
    private int opCode;
    private static AbstractFile fileToPaste;
    private static final int COPY_CODE = 0;
    private Folder folderToShow;

    private final static int ROWS_OF_GRID = 4; //Number of rows of pics showed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding the activity to the activity_thumbnails_layout layout.
        setContentView(R.layout.activity_thumbnails_layout);

        this.folderToShow = (Folder) ActivitiesHandler.getData("folderToShow");
        if(fileToPaste == null) {
            fileToPaste = (AbstractFile) ActivitiesHandler.getData("fileToPaste");
            opCode = (int) ActivitiesHandler.getData("opCode");
        }

        //defining the adapter which will handle the binding between the views and the layout
        RecyclerView.Adapter adapter = new PasteThumbnailAdapter(folderToShow.getFilteredFiles(new TrueFilter()),this);

        //showing the name of the folder
        getSupportActionBar().setTitle(folderToShow.getName());

        //getting the reference of the recycler view inside the activity_thumbnails_layout layout
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //setting a grid layout manager to the recycler
        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));

        //setting the adapter defined previously to the recycler
        recyclerView.setAdapter(adapter);
    }

    //this method shows a menu layout over the activity_thumbnails_layout layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.paste_menu, menu);
        return true;
    }

    /*
        this will be executed before showing the options of the menu so we can control here
        the option that displays the animal pictures, enabling or disabling the option
        when the service that creates the album is finished or not
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(fileToPaste.equals(folderToShow)){

        }
        if(folderToShow.equals(Folder.getFolderRoot())){
            getSupportActionBar().setTitle("Root");
            menu.getItem(0).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //in this method we indicate the actions to be taken for each option of the menu
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.paste_picture_op:
                if (opCode == COPY_CODE)
                    fileToPaste.copyTo(folderToShow);
                else { //move
                    fileToPaste.moveTo(folderToShow);
                }
                //we do this to prevent the remaining of a empty file in the directory origin
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileToPaste.getRealFile())));
                fileToPaste = null;
                opCode = -1;
                ActivitiesHandler.sendData("folderToShow", fileToPaste.getParent());
                ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
                return true;

            case R.id.cancel_op:
                fileToPaste.getParent().add(fileToPaste);
                ActivitiesHandler.sendData("folderToShow", fileToPaste.getParent());
                fileToPaste = null;
                ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
                return true;

            case R.id.backFolder_op:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        in order to get an up-to-date screen we create a new intent to go back,
        when the container of the displayed folder_thumbnail is null it means the folder_thumbnail root
        is shown, in this situation the back event will close the app
     */
    @Override
    public void onBackPressed() {
        if(folderToShow.getParent() != null) {
            ActivitiesHandler.sendData("folderToShow",folderToShow.getParent());
            ActivitiesHandler.changeActivity(this, PasteThumbnailsActivity.class);
        }
        else {
            fileToPaste.getParent().add(fileToPaste);
            ActivitiesHandler.sendData("folderToShow", fileToPaste.getParent());
            ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
        }
    }
}
