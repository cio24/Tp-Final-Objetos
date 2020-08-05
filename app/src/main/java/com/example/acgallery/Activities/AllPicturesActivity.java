package com.example.acgallery.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import com.example.acgallery.Adapters.RecyclerViewAdapter;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;

/*
    This activity shows all the pictures without folders.
 */
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
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(folderToShow.getDeepFilteredFiles(new TrueFilter()),this,false);

        //getting the referece of the recycler view inside the activity_thumbnails_layout layout
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //setting a grid layout manager to the recycler
        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));

        //setting the adapter defined previously to the recycler
        recyclerView.setAdapter(adapter);
    }

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