package com.example.acgallery.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.PagerAdapter;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.acgallery.Composite.AbstractFile;
import java.util.ArrayList;

public class FullPictureAdapter extends PagerAdapter {

    /*
        context is required when we want to create a view and there's no xml file to associate with,
        only in classes that implements AppCompatActivity the context is the instance of the class itself
        so you can use the reserved word this or getContextApplication() (something like that) when you create a thread that
        needs that context.
        For this reason, any other class that required a context must get it as one of his parameters.
     */
    private Context context;

    /*
        like the context, the actionBar is related to an activity, when we want to manipulate
        it we must use the getSupportActionBar() method of its activity, but in this case we
        need to get it through another parameter too.
     */
    private ActionBar actionBar;

    private SubsamplingScaleImageView fullPictureToShow; //the current picture to be displayed
    private ArrayList<AbstractFile> picturesToShow; //its folder container

    //staff required to change to full screen mode
    private boolean mVisible;
    private final Handler mHideHandler = new Handler();

    /*
        this adapter, unlike the RecyclerViewAdapter, does show only one view at a time
        as the interface name suggest, it controls the views so they can be displayed
        as a page.
     */
    public FullPictureAdapter(Context context, ArrayList<AbstractFile> picturesToShow, ActionBar actionBar) {
        this.picturesToShow = picturesToShow;
        this.actionBar = actionBar;
        this.context = context;
        mVisible = true;
    }

    /*
        same as RecyclerViewAdapter getCount() method: the adapter
        uses this method to control the limit of pictures where it can iterate
     */
    @Override
    public int getCount() {
        return picturesToShow.size();
    }

    /*
        I'm not sure of this, we have to do some research about this kind of adapter
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /*
        As yo can see below, this method creates a view that lets you
        make zoom in and sets the picture of the given position as its resource,
        then add the listener to the view so it can be displayed in full screen with a touch
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        fullPictureToShow = new SubsamplingScaleImageView(context);
        Bitmap bitmap = BitmapFactory.decodeFile(picturesToShow.get(position).getAbsolutePath());
        fullPictureToShow.setImage(ImageSource.bitmap(bitmap));
        fullPictureToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        container.addView(fullPictureToShow,0);
        return fullPictureToShow;
    }

    /*
        If I have to guess, I would say that this method detroy everyItem you
        left behind when there is a left or right swipe
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((SubsamplingScaleImageView) object);
    }

    /*
        Thw following methods are needed for giving the picture the capability of
        hide the navigation and status bar in order to see it in full screen
     */
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
            fullPictureToShow.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
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
        mHideHandler.postDelayed(mHidePart2Runnable, 0);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        fullPictureToShow.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mHideHandler.postDelayed(mShowPart2Runnable,0);
    }
}