/*
 * this activity displays in full screen mode the pictures shown in the AllPictureThumbnailsActivity
 */

package com.example.acgallery.Controller.Activities.FullPictureActivities;

import com.example.acgallery.Controller.Activities.ThumbnailsActivities.AllPicturesThumbnailsActivity;
import com.example.acgallery.Model.Composite.AbstractFile;
import com.example.acgallery.Model.Composite.Folder;
import com.example.acgallery.Model.Filters.TrueFilter;
import com.example.acgallery.Controller.ActivitiesHandler;
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
