package com.example.acgallery.Activities.ThumbnailsActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.acgallery.Adapters.ThumbnailsAdapters.FolderThumbnailAdapter;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Filters.FolderFilter;
import com.example.acgallery.Filters.FileFilter;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;
import com.example.acgallery.Sorters.CompoundSorter;
import com.example.acgallery.Sorters.NameSorter;
import com.example.acgallery.Sorters.RecentDateSorter;
import com.example.acgallery.Sorters.FoldersFirstSorter;
import com.example.acgallery.Utilities.ActivitiesHandler;
import com.example.acgallery.Utilities.AnimalsClassifierService;

public class FolderThumbnailsActivity extends AppCompatActivity {
    private Folder folderToShow;

    //Number of rows of pics showed
    private final static int ROWS_OF_GRID = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding the activity to the activity_thumbnails_layout layout.
        setContentView(R.layout.activity_thumbnails_layout);

        folderToShow = (Folder) ActivitiesHandler.getData("folderToShow");

        //defining the adapter which will handle the binding between the views and the layout
        RecyclerView.Adapter adapter = new FolderThumbnailAdapter(folderToShow.getFilteredFiles(new TrueFilter()),this);

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
        inflater.inflate(R.menu.folder_menu, menu);
        return true;
    }

    /*
        this will be executed before showing the options of the menu so we can control here
        the option that displays the animal pictures, enabling or disabling the option
        when the service that creates the album is finished or not
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //when the folder root is displayed we have to disabled some options so this folder can't be modified
        if(folderToShow.equals(Folder.getFolderRoot())){
            getSupportActionBar().setTitle("Root");
            menu.getItem(2).setVisible(false);
            for(int i = 3; i < menu.size() - 2; i++)
                menu.getItem(i).setEnabled(false);
        }

        /*
            here we also check if the service that classifies the animals pictures is done so
            we can activate the option that let the user see the pictures
         */
        if(!AnimalsClassifierService.isFinished(this))
            menu.getItem(1).setEnabled(false);
        else
            menu.getItem(1).setEnabled(true);

        return super.onPrepareOptionsMenu(menu);
    }

    //in this method we indicate the actions to be taken for each option of the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Folder folderRoot = Folder.getFolderRoot();

        switch (item.getItemId()) {
            case R.id.all_pictures_op:
                //we send the folder where the option was chosen so the user can comeback
                ActivitiesHandler.sendData("folderToReturn",folderToShow);
                ActivitiesHandler.changeActivity(this, AllPicturesThumbnailsActivity.class);
                return true;

            case R.id.animal_pictures_op:
                ActivitiesHandler.sendData("folderToReturn",folderToShow);
                ActivitiesHandler.changeActivity(this, AnimalPicturesThumbnailsActivity.class);
                return true;

            case R.id.order_by_name_op:
                folderToShow.sort(new CompoundSorter(new FoldersFirstSorter(), new NameSorter()));
                ActivitiesHandler.sendData("folderToShow",folderToShow);
                ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
                return true;

            case R.id.order_by_date_op:
                folderToShow.sort(new CompoundSorter(new FoldersFirstSorter(),new RecentDateSorter()));
                ActivitiesHandler.sendData("folderToShow",folderToShow);
                ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
                return true;

            case R.id.copy_folder_op:
                ActivitiesHandler.sendData("folderToShow",folderRoot);
                ActivitiesHandler.sendData("fileToPaste",folderToShow);

                //with opCode we can tell the activity whether we want to copy or move something
                ActivitiesHandler.sendData("opCode",0);
                ActivitiesHandler.changeActivity(this,PasteThumbnailsActivity.class);
                return true;

            case R.id.move_folder_op:
                ActivitiesHandler.sendData("folderToShow",folderRoot);
                ActivitiesHandler.sendData("fileToPaste",folderToShow);
                ActivitiesHandler.sendData("opCode",1);
                folderToShow.getParent().removeFile(folderToShow);
                ActivitiesHandler.changeActivity(this,PasteThumbnailsActivity.class);
                return true;

            case R.id.delete_folder_op:
                final AppCompatActivity originActivity = this;
                new AlertDialog.Builder(originActivity)
                        .setTitle("Delete folder permanently?")
                        .setMessage("If you delete this folder, it will be removed permanently from your device.")
                        .setPositiveButton("Delete permanently", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Folder folderToReturn = folderToShow.getParent();

                                //we delete the folder and make sure that won't remain an empty file of it.
                                if(!folderToShow.delete())
                                    Toast.makeText(originActivity,"The action failed!",Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(originActivity,"The file was deleted!",Toast.LENGTH_LONG).show();

                                //this is used to erased any trace of the deleted file
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(folderToShow.getRealFile())));

                                //then we comeback to the folder where the picture was
                                ActivitiesHandler.sendData("folderToShow",folderToReturn);
                                ActivitiesHandler.changeActivity(originActivity, FolderThumbnailsActivity.class);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //close the Alert Dialog
                            }
                        }).create().show();
                return true;

            case R.id.details_folder_op:
                //we show the units according the size of the folder
                String units;

                float folderSize = (float) folderToShow.size();
                if(folderSize/(1024*1024*1024) > 1.0) {
                    units = "GB";
                }
                else if(folderSize/(1024*1024) > 1.0){
                    folderSize = folderSize/(1024*1024);
                    units = "MB";
                }
                else{
                    units = "KB";
                    folderSize = folderSize/1024;
                }

                //finally we show all the info about this folder
                int picturesAmount = folderToShow.getItemsNumber(new FileFilter());
                new AlertDialog.Builder(this)
                        .setTitle(folderToShow.getName())
                        .setMessage(
                                "\n" + "CURRENT FOLDER"+ "\n" +
                                        "      Pictures: " + picturesAmount + "    Folders: " + (folderToShow.getItemsNumber(new TrueFilter()) - picturesAmount) + "\n" + "\n" +
                                        "CURRENT & INNER FOLDERS " + "\n" +
                                        "      Pictures: " + folderToShow.getDeepItemsNumber(new FileFilter()) + "    Folders: " + (folderToShow.getDeepItemsNumber(new FolderFilter()) - 1) + "\n" + "\n" +
                                        "Size: " + folderSize + " " + units +  "\n" + "\n" +
                                        "Creation time: " + folderToShow.getCreationTime() +  "\n" + "\n" +
                                        "Path: " + folderToShow.getAbsolutePath()
                        )
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //close the Alert Dialog
                            }
                        })
                        .create().show();
                return true;

            case R.id.rename_folder_op:
                //we defined an editText to read the input of the user and let the user select all the text of old name
                final EditText inputNewName = new EditText(this);
                inputNewName.setText(folderToShow.getName());
                inputNewName.setSelectAllOnFocus(true);

                new AlertDialog.Builder(this)
                        .setView(inputNewName)
                        .setTitle("Rename")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(!folderToShow.rename(inputNewName.getText().toString()))
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        in order to get an up-to-date screen we create a new intent to go back, when the container of
         the displayed folder_thumbnail is null it means the folder_thumbnail root is shown
     */
    @Override
    public void onBackPressed() {
        if(folderToShow.getParent() != null){
            ActivitiesHandler.sendData("folderToShow",folderToShow.getParent());
            ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
        }
        else
            this.moveTaskToBack(true); //it sends the app to the background without closing it.
    }
}
