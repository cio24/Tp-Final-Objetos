package com.example.acgallery.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.acgallery.Adapters.ThumbnailsAdapter;
import com.example.acgallery.ClassifierService;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;
import com.example.acgallery.Sorters.RecentDateSort;
import com.example.acgallery.Sorters.NameSort;

/*
    this activity shows the thumbnails of all the pictures and a thumbnail of a folder for folders
    that are inside in the given folder
 */
public class ThumbnailsActivity extends AppCompatActivity {

    final static int ROWS_OF_GRID = 4; //Number of rows of pics showed
    private Folder folderToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //binding the activity to the activity_thumbnails_layout layout.
        setContentView(R.layout.activity_thumbnails_layout);

        //getting the folder to display
        folderToShow = (Folder) ActivitiesHandler.getData("folderToShow");
        //folderToShow = (Folder) getIntent().getSerializableExtra("file");

        //we make sure that there's no empty files inside the folder to be displayed
        //clean();

        //defining the adapter which will handle the binding between the views and the layout
        RecyclerView.Adapter adapter = new ThumbnailsAdapter(folderToShow.getFilteredFiles(new TrueFilter()),this);

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

    //in this method we indicate the actions to be taken for each option of the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Folder folderRoot = folderToShow.getFolderRoot();

        if(item.getItemId() == R.id.all_pictures_op) {
            //Intent intent = new Intent(this, AllPicturesActivity.class);

            //then send the folder where the option was chosen so the user can comeback
            ActivitiesHandler.addData("folderToReturn",folderToShow);
            ActivitiesHandler.sendData(this,AllPicturesActivity.class);
            //intent.putExtra("file",folderToShow);
            //startActivity(intent);
            //finish();
        }
        else if(item.getItemId() == R.id.animal_picutres_op) {
            Intent intent = new Intent(getApplicationContext(), ServicePicturesActivity.class);

            //we have to send the current folder so the back event knows where to comeback
            intent.putExtra("folderToReturn", folderToShow);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.order_by_name_op || item.getItemId() == R.id.order_by_date_op) {
            if(item.getItemId() == R.id.order_by_name_op)
                folderToShow.sort(new NameSort());
            else
                folderToShow.sort(new RecentDateSort());
            ActivitiesHandler.addData("folderToShow",folderToShow);
            ActivitiesHandler.sendData(this,ThumbnailsActivity.class);
            /*
            Intent intent = new Intent(this, ThumbnailsActivity.class);
            intent.putExtra("folderToShow",folderToShow);
            startActivity(intent);
            finish();

             */
        }
        else if(item.getItemId() == R.id.copy_picture_op || item.getItemId() == R.id.move_picture_op) {
            ActivitiesHandler.addData("folderToShow",folderRoot);
            ActivitiesHandler.addData("fileToPaste",folderToShow);
            if(item.getItemId() == R.id.copy_picture_op)
                ActivitiesHandler.addData("opCode",0);
            else
                ActivitiesHandler.addData("opCode",1);

            ActivitiesHandler.sendData(this,PasteActivity.class);
            /*
            Intent intent = new Intent(getApplicationContext(), PasteActivity.class);
            intent.putExtra("file", folderRoot);
            intent.putExtra("paste", folderToShow);
            intent.putExtra("opCode", 0);
            startActivity(intent);
            finish();
            //folderToShow.copyTo(folderRoot);

             */
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        this will be executed before showing the options of the menu so we can control here
        the option that displays the animal pictures, enabling or disabling the option
        when the service that creates the album is finished or not
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(ClassifierService.isFinished())
            menu.getItem(1).setEnabled(true);
        else
            menu.getItem(1).setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    /*
        in order to get an up-to-date screen we create a new intent to go back, when the container of
         the displayed folder_thumbnail is null it means the folder_thumbnail root is shown
     */
    @Override
    public void onBackPressed() {
        if(folderToShow.getParent() != null){
            ActivitiesHandler.addData("folderToShow",folderToShow.getParent());
            ActivitiesHandler.sendData(this,ThumbnailsActivity.class);
            /*
            Intent intent = new Intent(this, ThumbnailsActivity.class);
            intent.putExtra("folderToShow", folderToShow.getParent());
            startActivity(intent);
            finish();

             */
        }
        else
            this.moveTaskToBack(true); //it sends the app to the background without closing it.
    }

    /*
        this method remove all the abstract files that don't have a inner file which path is linked
        with a real file in the phone.
     */
    /*
    private void clean(){
        for(int i = 0; i < folderToShow.getFilesAmount(); i++){
            if(!folderToShow.getFileAt(i).getRealFile().exists()){
                folderToShow.removeByName(folderToShow.getFileAt(i).getName());
            }
        }
    }

     */

}