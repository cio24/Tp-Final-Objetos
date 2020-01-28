package com.example.acgallery.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.PagerAdapter;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.acgallery.Composited.AbstractFile;
import com.example.acgallery.Composited.Folder;

import java.util.ArrayList;

public class SwipeAdapter extends PagerAdapter {
    private Context context;
    private Folder folder;
    private ArrayList<AbstractFile> pictures;
    private boolean mVisible;
    private final Handler mHideHandler = new Handler();
    private SubsamplingScaleImageView image;
    private ActionBar actionBar;


    public SwipeAdapter(Context context, ArrayList<AbstractFile> pictures, ActionBar actionBar) {
        this.pictures = pictures;
        this.actionBar = actionBar;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mVisible = true;

        image = new SubsamplingScaleImageView(context);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Log.d("KAWAI","Position de imagen actual: " + position);
        Bitmap bitmap = BitmapFactory.decodeFile(pictures.get(position).getAbsolutePath(),bmOptions);
        ImageSource source = ImageSource.bitmap(bitmap);
        image.setImage(source);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        container.addView(image,0);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((SubsamplingScaleImageView) object);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }


    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private void hide() {
        // Hide UI first
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        mHideHandler.postDelayed(mHidePart2Runnable, 300);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mHideHandler.postDelayed(mShowPart2Runnable,300);
    }

}