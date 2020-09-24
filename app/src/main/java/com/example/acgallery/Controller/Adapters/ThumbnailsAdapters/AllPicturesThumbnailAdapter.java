package com.example.acgallery.Controller.Adapters.ThumbnailsAdapters;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.acgallery.Controller.Activities.FullPictureActivities.FullPictureFromAllActivity;
import com.example.acgallery.Model.Composite.Folder;
import com.example.acgallery.Controller.ActivitiesHandler;
import com.example.acgallery.Model.Composite.AbstractFile;
import java.util.ArrayList;

public class AllPicturesThumbnailAdapter extends ThumbnailAdapter {
    private Folder folderToReturn;

    public AllPicturesThumbnailAdapter(ArrayList<AbstractFile> filesToShow, AppCompatActivity originActivity, Folder folderToReturn) {
        super(filesToShow, originActivity);
        this.folderToReturn = folderToReturn;
    }

    @Override
    public void setListener(ViewHolder holder, final AbstractFile thumbnail) {
        holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesHandler.sendData("pictureToShow",thumbnail);
                ActivitiesHandler.sendData("folderToReturn",folderToReturn);
                ActivitiesHandler.changeActivity(originActivity, FullPictureFromAllActivity.class);
            }
        });
    }
}
