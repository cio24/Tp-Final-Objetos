package com.example.acgallery.Activities.FullPictureActivities;

import com.example.acgallery.Activities.ThumbnailsActivities.AnimalPicturesThumbnailsActivity;
import com.example.acgallery.Utilities.ActivitiesHandler;
import com.example.acgallery.Utilities.AnimalsClassifierService;
import com.example.acgallery.Composite.AbstractFile;
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
