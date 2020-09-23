/*
 * this activity displayed in full screen mode the pictures shown in the FullPictureFromFolderActivity
 */

package com.example.acgallery.Activities.FullPictureActivities;

import com.example.acgallery.Activities.ThumbnailsActivities.FolderThumbnailsActivity;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.Utilities.ActivitiesHandler;
import java.util.ArrayList;

public class FullPictureFromFolderActivity extends FullPictureActivity {

    @Override
    protected ArrayList<AbstractFile> getPicturesToShow() {
        return getPictureToShow().getParent().getFilteredFiles(new TrueFilter());
    }

    @Override
    public void onBackPressed() {
        ActivitiesHandler.sendData("folderToShow", getFolderToReturn());
        ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
    }
}