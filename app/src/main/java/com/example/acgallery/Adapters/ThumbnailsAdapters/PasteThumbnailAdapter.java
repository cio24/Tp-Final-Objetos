package com.example.acgallery.Adapters.ThumbnailsAdapters;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.acgallery.Utilities.ActivitiesHandler;
import com.example.acgallery.Activities.ThumbnailsActivities.PasteThumbnailsActivity;
import com.example.acgallery.Composite.AbstractFile;
import java.util.ArrayList;

public class PasteThumbnailAdapter extends ThumbnailAdapter {

    public PasteThumbnailAdapter(ArrayList<AbstractFile> filesToShow, AppCompatActivity originActivity) {
        super(filesToShow,originActivity);
    }

    @Override
    public void setListener(ViewHolder holder, final AbstractFile thumbnail) {
        holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thumbnail.getRealFile().isDirectory()) {
                    ActivitiesHandler.sendData("folderToShow",thumbnail);
                    ActivitiesHandler.changeActivity(originActivity, PasteThumbnailsActivity.class);
                }
            }
        });
    }
}
