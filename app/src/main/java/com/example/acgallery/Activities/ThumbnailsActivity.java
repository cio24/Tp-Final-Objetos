package com.example.acgallery.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.acgallery.Adapters.RecyclerViewAdapter;
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.R;

public class ThumbnailsActivity extends AppCompatActivity {

    //Number of rows of pics showed
    final static int ROWS_OF_GRID = 4;
    private Folder folderToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));
        RecyclerView.Adapter adapter;

        folderToShow = (Folder) getIntent().getSerializableExtra("idFolder");
        adapter = new RecyclerViewAdapter(folderToShow,this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.folder_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(folderToShow.getContainer() != null){
            Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
            intent.putExtra("idFolder", folderToShow.getContainer());
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }
}