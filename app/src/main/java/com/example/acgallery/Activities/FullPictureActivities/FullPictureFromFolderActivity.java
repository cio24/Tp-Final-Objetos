package com.example.acgallery.Activities.FullPictureActivities;

import com.example.acgallery.Activities.ThumbnailsActivities.FolderThumbnailsActivity;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.Utilities.ActivitiesHandler;
import java.util.ArrayList;

/*
    this activity open a selected picture and shows a menu of actions like move, remove, etc.
     Additionally it lets you to display in full screen mode and zoom the picture.
 */
public class FullPictureFromFolderActivity extends FullPictureActivity {

    @Override
    public ArrayList<AbstractFile> getPicturesToShow() {
        return getPictureToShow().getParent().getFilteredFiles(new TrueFilter());
    }

    @Override
    public void onBackPressed() {
        ActivitiesHandler.sendData("folderToShow", getFolderToReturn());
        ActivitiesHandler.changeActivity(this, FolderThumbnailsActivity.class);
    }
}