/*
 * This activity shows the pictures the service classifies that is to say
 * the pictures of animals
 */

package com.example.acgallery.Controller.Activities.ThumbnailsActivities;

import androidx.recyclerview.widget.RecyclerView;
import com.example.acgallery.Controller.Adapters.ThumbnailsAdapters.AnimalPicturesThumbnailAdapter;
import com.example.acgallery.Model.Composite.AbstractFile;
import com.example.acgallery.Controller.AnimalsClassifierService;
import java.util.ArrayList;

public class AnimalPicturesThumbnailsActivity extends FilteredPicturesThumbnailsActivity {
    private ArrayList<AbstractFile> picturesToShow;

    @Override
    public ArrayList<AbstractFile> getPicturesToShow() {
        if(picturesToShow == null)
            picturesToShow = AnimalsClassifierService.getPictures(this);
        return picturesToShow;
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new AnimalPicturesThumbnailAdapter(getPicturesToShow(),this,getFolderToReturn());
    }

    @Override
    public String getActionBarTitle() {
        return "Animals Pictures";
    }

}