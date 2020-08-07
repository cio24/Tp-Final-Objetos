package com.example.acgallery.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import com.example.acgallery.Adapters.AllPicturesAdapter;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;

/*
    This activity shows all the pictures without folders.
 */
public class AllPicturesActivity extends AppCompatActivity {

    final static int ROWS_OF_GRID = 4;
    private Folder folderToReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnails_layout);

        //getting the folder where the all pictures option was selected
        folderToReturn = (Folder) getIntent().getSerializableExtra("file");

        RecyclerView.Adapter adapter = new AllPicturesAdapter(folderToReturn.getFolderRoot().getDeepFilteredFiles(new TrueFilter()),this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ThumbnailsActivity.class);
        intent.putExtra("file", folderToReturn);
        startActivity(intent);
        finish();
    }
}