package com.example.acgallery.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.acgallery.Adapters.SwipeAdapter;
import com.example.acgallery.Composited.AbstractFile;
import com.example.acgallery.Composited.Picture;
import com.example.acgallery.Filters.CriterionFilter;
import com.example.acgallery.Filters.FolderFilter;
import com.example.acgallery.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class FullPictureActivity extends AppCompatActivity {

    private AbstractFile fullPictureToShow;
    private ViewPager viewPager;
    private ArrayList<AbstractFile> pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        }
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
    private void showFullPicture(AbstractFile picture){
        viewPager = findViewById(R.id.view_pager);
        pictures = picture.getContainer().getFilteredFiles(new FolderFilter());
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

        int currentPicPosition = viewPager.getCurrentItem();
        for (int i = 0; i < pictures.size(); i++){
            if (i == currentPicPosition){
                fullPictureToShow = pictures.get(i);
            }
        }

        if(item.getItemId() == R.id.delete_image_op){
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item permanently?")
                    .setMessage("If you delete this item, it will be removed permanently from your device.")
                    .setPositiveButton("Delete Permanently", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            fullPictureToShow.delete();
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
        else if(item.getItemId() == R.id.details_image_op){
            Log.d("DETAILS", "Details executing" + fullPictureToShow.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fullPictureToShow.getAbsolutePath(), options);
            int h = options.outHeight;
            int w = options.outWidth;

            BasicFileAttributes attributes = null;
            Path filePath = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                filePath = Paths.get(fullPictureToShow.getAbsolutePath());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    attributes = Files.readAttributes(filePath,BasicFileAttributes.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String time = attributes.creationTime().toString();

                new AlertDialog.Builder(this)
                        .setTitle(fullPictureToShow.getName())
                        .setMessage(
                                "Dimensions: " + w + " x " + h + "\n" +
                                "Size: " + fullPictureToShow.getInnerFile().length()/(1024*1024) +  " MB"+ "\n" +
                                "Path: " + fullPictureToShow.getAbsolutePath() + "\n" +
                                "Taken on: " + time.substring(0,time.indexOf("T"))
                        )
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create().show();
            }
        }
        else if(item.getItemId() == R.id.renameImage_op){
            if (fullPictureToShow.getInnerFile().renameTo(new File (fullPictureToShow.getContainer().getAbsolutePath() + "/geralt.jpg"))){
                Toast.makeText(this, "File renamed", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Tu vieja te va a renombrar", Toast.LENGTH_SHORT).show();
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fullPictureToShow.innerFile)));
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