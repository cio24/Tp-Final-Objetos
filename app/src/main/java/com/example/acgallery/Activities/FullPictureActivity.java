package com.example.acgallery.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
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

    private Picture picture;
    private CriterionFilter filter = new FolderFilter();
    private ArrayList<AbstractFile> pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picture = (Picture) getIntent().getSerializableExtra("fullPicture");
        showFullImage(picture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.picture_menu, menu);
        return true;
    }

    private void showFullImage(Picture picture){
        ViewPager viewPager = findViewById(R.id.viewPager);
        pictures = picture.getContainer().getPicturesOnly(filter);
        int i = 0;
        for(AbstractFile pic: pictures){
            if(pic.equals(picture))
                break;
            i++;
        }
        SwipeAdapter adapter = new SwipeAdapter(this, pictures, getSupportActionBar());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(i);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.deleteImage_op){
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure dude?")
                    .setMessage("The akatsuki members gonna kill u!")
                    .setPositiveButton("YOLO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            picture.delete();
                            Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
                            intent.putExtra("idFolder", picture.getContainer());
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("I'm scared", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
        }
        if(item.getItemId() == R.id.backImage_op) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
        intent.putExtra("idFolder", picture.getContainer());
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}