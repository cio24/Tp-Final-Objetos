/*
 * this activity displayed in full screen mode the pictures shown in the FullPictureFromFolderActivity
 */

package com.example.acgallery.Controller.Activities.FullPictureActivities;

import com.example.acgallery.Controller.Activities.ThumbnailsActivities.FolderThumbnailsActivity;
import com.example.acgallery.Model.Composite.AbstractFile;
import com.example.acgallery.Model.Filters.FileFilter;
import com.example.acgallery.Controller.ActivitiesHandler;
import java.util.ArrayList;

public class FullPictureFromFolderActivity extends FullPictureActivity {

    @Override
    protected ArrayList<AbstractFile> getPicturesToShow() {
        return getPictureToShow().getParent().getFilteredFiles(new FileFilter());
    }

    @Override
    public void onBackPressed() {
        ActivitiesHandler.sendData("folderToShow", getFolderToReturn());
        ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
    }
}