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
import android.widget.Toast;

import com.example.acgallery.Adapters.RecyclerViewAdapter;
import com.example.acgallery.ClassifierService;
import com.example.acgallery.Composited.AbstractFile;
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.Filters.CriterionFilter;
import com.example.acgallery.Filters.PictureFilter;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;

import java.util.ArrayList;

public class ThumbnailsActivity extends AppCompatActivity {

    final static int ROWS_OF_GRID = 4; //Number of rows of pics showed
    private Folder folderToShow;
    private ArrayList<AbstractFile> picturesToShow;

    private void clean(){
        for(int i = 0; i < folderToShow.getFilesAmount(); i++){
            if(!folderToShow.getFileAt(i).getInnerFile().exists()){
                folderToShow.removeByName(folderToShow.getFileAt(i).getName());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //buinding the activity to the activity_thumbnails_layout layout.
        setContentView(R.layout.activity_thumbnails_layout);


        //getting the folder_thumbnail from to be displayed
        folderToShow = (Folder) getIntent().getSerializableExtra("idFolder");

        clean();

        //defining the adapter which will handle the binding between the views and the layout
        CriterionFilter c = new TrueFilter();
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(folderToShow.getFilteredFiles(c),this,ThumbnailsActivity.class,false);

        //getting the referece of the recycler view inside the activity_thumbnails_layout layout
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //setting a grid layout manager to the recycler
        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));

        //setting the adapter definied previously to the recycler
        recyclerView.setAdapter(adapter);
    }

    //this method shows a menu layout over the activity_thumbnails_layout layout

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.folder_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Folder folderRoot = getFolderRoot();
        if(item.getItemId() == R.id.all_pictures_op) {
            Intent intent = new Intent(this, AllPicturesActivity.class);
            clean();

            //paso la carpeta de retorno
            intent.putExtra("idFolder", folderRoot);

            //paso todas las fotos
            intent.putExtra("pictures",folderRoot.getDeepFilteredFiles(new PictureFilter()));

            startActivity(intent);
            finish();

        }

        if(item.getItemId() == R.id.animal_picutres_op) {

            if(ClassifierService.isFinished()){
                Intent intent = new Intent(getApplicationContext(), ServicePicturesActivity.class);

                clean();

                //paso la carpeta de retorno
                intent.putExtra("idFolder", folderToShow);

                //paso las fotos filtradas por el servicio
                //intent.putExtra("pictures", pictures);

                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(this, "Service hasn't finished yet", Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }


    private Folder getFolderRoot(){
        Folder folderRoot = folderToShow;
        while(folderRoot.getContainer() != null){
            folderRoot = folderRoot.getContainer();
        }
        return folderRoot;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(ClassifierService.isFinished())
            menu.getItem(1).setEnabled(true);
        else
            menu.getItem(1).setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }
    /*
                in order to get an up-to-date screen we create a new intent to go back,
                when the container of the displayed folder_thumbnail is null it means the folder_thumbnail root
                is shown, in this situation the back event will close the app
             */
    @Override
    public void onBackPressed() {
        if(folderToShow.getContainer() != null){
            Intent intent = new Intent(this, ThumbnailsActivity.class);
            intent.putExtra("idFolder", folderToShow.getContainer());
            startActivity(intent);
            finish();
        }
        else
            super.onBackPressed();
    }
}