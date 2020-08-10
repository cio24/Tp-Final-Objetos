package com.example.acgallery.Adapters;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acgallery.Activities.ActivitiesHandler;
import com.example.acgallery.Activities.FullPictureActivity;
import com.example.acgallery.Composite.AbstractFile;
import java.util.ArrayList;

public class AnimalPicturesAdapter extends RecyclerViewAdapter {


        public AnimalPicturesAdapter(ArrayList<AbstractFile> filesToShow, AppCompatActivity originActivity) {
            super(filesToShow, originActivity);
        }

        @Override
        public void setListener(ViewHolder holder, final AbstractFile thumbnail) {
            holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivitiesHandler.sendData("pictureToShow",thumbnail);
                    ActivitiesHandler.sendData("filteredPictures",true);
                    ActivitiesHandler.sendData("all",false);
                    ActivitiesHandler.changeActivity(originActivity, FullPictureActivity.class);
                }
            });
        }

}


