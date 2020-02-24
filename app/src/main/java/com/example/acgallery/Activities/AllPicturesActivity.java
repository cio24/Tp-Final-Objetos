package com.example.acgallery.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.acgallery.Adapters.RecyclerViewAdapter;
import com.example.acgallery.Composited.AbstractFile;
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.Composited.Picture;
import com.example.acgallery.Filters.ClassifierFilter;
import com.example.acgallery.Filters.CriterionFilter;
import com.example.acgallery.Filters.PictureFilter;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AllPicturesActivity extends AppCompatActivity {

    final static int ROWS_OF_GRID = 4; //Number of rows of pics showed
    private Folder folderToShow;
    private ArrayList<AbstractFile> filesToShow;

    private void clean(){
        for(int i = 0; i < folderToShow.getFilesAmount(); i++){
            if(!folderToShow.getFileAt(i).getInnerFile().exists()){
                Log.d("panasonic","hay un file empty!!!");
                folderToShow.removeByName(folderToShow.getFileAt(i).getName());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ArrayList<String> labels = null;

        try {
            labels = loadLabelList();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.d("PIA", "tamaño de labels: " + labels.size());

        //buinding the activity to the activity_thumbnails_layout layout.
        setContentView(R.layout.activity_thumbnails_layout);


        //getting the folder_thumbnail from to be displayed
        folderToShow = (Folder) getIntent().getSerializableExtra("idFolder");

        clean();



        //defining the adapter which will handle the buinding between the views and the layout
        //CriterionFilter c = new PictureFilter();
        ClassifierFilter c = new ClassifierFilter(this,labels);
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(folderToShow.getDeepFilteredFiles(c),this,AllPicturesActivity.class,false);

        //getting the referece of the recycler view inside the activity_thumbnails_layout layout
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //setting a grid layout manager to the recycler
        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));

        //setting the adapter definied previously to the recycler
        recyclerView.setAdapter(adapter);
    }


    private ArrayList<String> loadLabelList() throws IOException {
        ArrayList<String> labelList = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("animals.txt")));
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
            i++;
            if(i == 50)
                break;
        }
        reader.close();
        if(labelList == null)
            Log.d("hipofisis","labelist es null!");
        return labelList;
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
        //super.onBackPressed();
    }
}