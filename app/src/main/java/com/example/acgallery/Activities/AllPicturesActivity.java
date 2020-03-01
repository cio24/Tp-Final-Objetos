package com.example.acgallery.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import com.example.acgallery.Adapters.RecyclerViewAdapter;
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;

public class AllPicturesActivity extends AppCompatActivity {

    final static int ROWS_OF_GRID = 4; //Number of rows of pics showed
    private Folder folderToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //buinding the activity to the activity_thumbnails_layout layout.
        setContentView(R.layout.activity_thumbnails_layout);

        //getting the folder_thumbnail from to be displayed
        folderToShow = (Folder) getIntent().getSerializableExtra("idFolder");


        //defining the adapter which will handle the buinding between the views and the layout
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(folderToShow.getDeepFilteredFiles(new TrueFilter()),this, ServicePicturesActivity.class,false);

        //getting the referece of the recycler view inside the activity_thumbnails_layout layout
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //setting a grid layout manager to the recycler
        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));

        //setting the adapter defined previously to the recycler
        recyclerView.setAdapter(adapter);
    }


    //this method shows a menu layout over the activity_thumbnails_layout layout

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.folder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.all_pictures_op) {
            Intent intent = new Intent(this, ThumbnailsActivity.class);
            intent.putExtra("idFolder", folderToShow.getContainer());
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

     */

    /*
        in order to get an up-to-date screen we create a new intent to go back,
        when the container of the displayed folder_thumbnail is null it means the folder_thumbnail root
        is shown, in this situation the back event will close the app
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ThumbnailsActivity.class);
        intent.putExtra("idFolder", folderToShow);
        startActivity(intent);
        finish();
    }
}