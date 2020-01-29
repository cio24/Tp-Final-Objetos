package com.example.acgallery.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.acgallery.Adapters.SwipeAdapter;
import com.example.acgallery.Composited.AbstractFile;
import com.example.acgallery.Composited.Picture;
import com.example.acgallery.Filters.CriterionFilter;
import com.example.acgallery.Filters.FolderFilter;
import com.example.acgallery.R;

import java.util.ArrayList;

public class FullPictureActivity extends AppCompatActivity {

    private Picture fullPictureToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture_layout);
        fullPictureToShow = (Picture) getIntent().getSerializableExtra("fullPicture");
        showFullPicture(fullPictureToShow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.picture_menu, menu);
        return true;
    }

    /*
        we have to give only the pictures to the adapter so it don't crush the app
        trying to show a folder, then we have to find what is the posicion of the
        picture to be shown in the arraylist so we can set it up as the current item
     */
    private void showFullPicture(Picture picture){
        ViewPager viewPager = findViewById(R.id.view_pager);
        ArrayList<AbstractFile> pictures = picture.getContainer().getFilteredFiles(new FolderFilter());
        int currentPicturePos = 0;
        for(AbstractFile pic: pictures){
            if(pic.equals(picture))
                break;
            currentPicturePos++;
        }
        SwipeAdapter adapter = new SwipeAdapter(this, pictures, getSupportActionBar());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPicturePos);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete_image_op){
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item permanently?")
                    .setMessage("If you delete this item, it will be removed permanently from your device.")
                    .setPositiveButton("Delete Permanently", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            fullPictureToShow.delete();
                            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(fullPictureToShow.getAbsolutePath() + Environment.getExternalStorageDirectory())));
                            Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
                            intent.putExtra("idFolder", fullPictureToShow.getContainer());
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fullPictureToShow.innerFile)));
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the Alert Dialog
                        }
                    }).create().show();

        }
        else if(item.getItemId() == R.id.back_image_op) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
        intent.putExtra("idFolder", fullPictureToShow.getContainer());
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}