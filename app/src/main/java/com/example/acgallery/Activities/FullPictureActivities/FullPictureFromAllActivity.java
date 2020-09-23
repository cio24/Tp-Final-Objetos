/*
 * this activity displays in full screen mode the pictures shown in the AllPictureThumbnailsActivity
 */

package com.example.acgallery.Activities.FullPictureActivities;

import com.example.acgallery.Activities.ThumbnailsActivities.AllPicturesThumbnailsActivity;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.Utilities.ActivitiesHandler;
import java.util.ArrayList;

public class FullPictureFromAllActivity extends FullPictureActivity {

    @Override
    protected ArrayList<AbstractFile> getPicturesToShow() {
        return Folder.getFolderRoot().getDeepFilteredFiles(new TrueFilter());
    }

    @Override
    public void onBackPressed() {
        ActivitiesHandler.sendData("folderToReturn", getFolderToReturn());
        ActivitiesHandler.changeActivity(this, AllPicturesThumbnailsActivity.class);
    }
}
