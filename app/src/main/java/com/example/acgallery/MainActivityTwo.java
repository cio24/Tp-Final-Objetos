package com.example.acgallery;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.acgallery.Composited.Picture;

public class MainActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);
        Picture imageToShow = (Picture) getIntent().getSerializableExtra("fullImage");
        ImageAdapter adapter = new ImageAdapter(this, imageToShow.getContainer());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(imageToShow.getContainer().getFilePos(imageToShow));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.picture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete_op){
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure dude?")
                    .setMessage("The akatsuki members gonna kill u!")
                    .setPositiveButton("YOLO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(),"Better hide while you can!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("I'm scared", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
        }
        if(item.getItemId() == R.id.back_op) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}