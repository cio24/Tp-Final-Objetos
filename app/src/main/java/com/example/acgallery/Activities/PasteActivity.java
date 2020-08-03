package com.example.acgallery.Activities;

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

import com.example.acgallery.Adapters.RecyclerViewAdapter;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Composite.Picture;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;

public class PasteActivity extends AppCompatActivity {

    final static int ROWS_OF_GRID = 4; //Number of rows of pics showed
    private Folder folderToShow;
    private static AbstractFile pictureToPaste;
    private final int COPY_CODE = 0;
    private static int opCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //buinding the activity to the activity_thumbnails_layout layout.
        setContentView(R.layout.activity_paste_layout);

        //getting the folder_thumbnail from to be displayed
        folderToShow = (Folder) getIntent().getSerializableExtra("idFolder");
        if(pictureToPaste == null) {
            pictureToPaste = (AbstractFile) getIntent().getSerializableExtra("idPicToPaste");
            opCode = (int) getIntent().getSerializableExtra("opCode");
        }

        //defining the adapter which will handle the binding between the views and the layout
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(folderToShow.getFilteredFiles(new TrueFilter()),this, PasteActivity.class, true);

        //getting the referece of the recycler view inside the activity_thumbnails_layout layout
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //setting a grid layout manager to the recycler
        recyclerView.setLayoutManager(new GridLayoutManager(this,ROWS_OF_GRID));

        //setting the adapter defined previously to the recycler
        recyclerView.setAdapter(adapter);
    }

    //this method shows a menu layout over the paste_menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.paste_menu, menu);
        return true;
    }

    //in this method we indicate the actions to be taken for each option of the menu
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.paste_picture_op){
            if (opCode == COPY_CODE)
                pictureToPaste.copyTo(folderToShow);
            else { //move
                pictureToPaste.moveTo(folderToShow);

                //we do this to prevent the remaining of a empty file in the directory origin
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureToPaste.innerFile)));
            }
            pictureToPaste = null;
            opCode = -1;
        }

        //then we comeback to the folder where the picture was
        Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
        intent.putExtra("idFolder",folderToShow);
        startActivity(intent);
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    /*
        in order to get an up-to-date screen we create a new intent to go back,
        when the container of the displayed folder_thumbnail is null it means the folder_thumbnail root
        is shown, in this situation the back event will close the app
     */
    @Override
    public void onBackPressed() {
        if(folderToShow.getContainer() != null){
            Intent intent = new Intent(getApplicationContext(), PasteActivity.class);
            intent.putExtra("idFolder", folderToShow.getContainer());
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }
}
