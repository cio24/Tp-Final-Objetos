/*
 * this activity displays in full screen mode the pictures shown in the AnimalPicturesThumbnailsActivity
 */

package com.example.acgallery.Controller.Activities.FullPictureActivities;

import com.example.acgallery.Controller.Activities.ThumbnailsActivities.AnimalPicturesThumbnailsActivity;
import com.example.acgallery.Model.Composite.AbstractFile;
import com.example.acgallery.Controller.ActivitiesHandler;
import com.example.acgallery.Controller.AnimalsClassifierService;
import java.util.ArrayList;

public class FullPictureFromAnimalsActivity extends FullPictureActivity {

    @Override
    protected ArrayList<AbstractFile> getPicturesToShow() {
        return AnimalsClassifierService.getPictures(this);
    }

    @Override
    public void onBackPressed() {
        ActivitiesHandler.sendData("folderToReturn", getFolderToReturn());
        ActivitiesHandler.changeActivity(this, AnimalPicturesThumbnailsActivity.class);
    }
}
