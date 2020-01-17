package com.example.acgallery.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.acgallery.Adapter;
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.R;

public class MainActivity extends AppCompatActivity {

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
        adapter = new Adapter(folderToShow,this);
        recyclerView.setAdapter(adapter);

    }
}