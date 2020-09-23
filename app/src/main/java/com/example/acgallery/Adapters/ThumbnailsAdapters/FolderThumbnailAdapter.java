/*
 * This adapter handles the pictures of the FolderThumbnailsActivity
 */

package com.example.acgallery.Adapters.ThumbnailsAdapters;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.acgallery.Activities.ThumbnailsActivities.FolderThumbnailsActivity;
import com.example.acgallery.Utilities.ActivitiesHandler;
import com.example.acgallery.Activities.FullPictureActivities.FullPictureFromFolderActivity;
import com.example.acgallery.Composite.AbstractFile;
import java.util.ArrayList;

public class FolderThumbnailAdapter extends ThumbnailAdapter {

    public FolderThumbnailAdapter(ArrayList<AbstractFile> filesToShow, AppCompatActivity originActivity) {
        super(filesToShow, originActivity);
    }

    @Override
    public void setListener(ViewHolder holder, final AbstractFile thumbnail) {
        holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thumbnail.getRealFile().isDirectory()) {
                    ActivitiesHandler.sendData("folderToShow", thumbnail);
                    ActivitiesHandler.changeActivity(originActivity, FolderThumbnailsActivity.class);
                }
                else{
                    ActivitiesHandler.sendData("pictureToShow",thumbnail);
                    ActivitiesHandler.sendData("folderToReturn",thumbnail.getParent());
                    ActivitiesHandler.changeActivity(originActivity, FullPictureFromFolderActivity.class);
                }
            }
        });
    }
}
