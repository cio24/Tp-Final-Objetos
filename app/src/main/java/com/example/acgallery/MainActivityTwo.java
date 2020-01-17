package com.example.acgallery;

import android.os.Bundle;

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
}