/*
 * This activity shows all the pictures without folders.
 */

package com.example.acgallery.Activities.ThumbnailsActivities;

import androidx.recyclerview.widget.RecyclerView;
import com.example.acgallery.Adapters.ThumbnailsAdapters.AllPicturesThumbnailAdapter;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Filters.TrueFilter;
import java.util.ArrayList;

public class AllPicturesThumbnailsActivity extends FilteredPicturesThumbnailsActivity {
    private ArrayList<AbstractFile> picturesToShow;

    @Override
    public ArrayList<AbstractFile> getPicturesToShow() {
        if(picturesToShow == null)
            picturesToShow = Folder.getFolderRoot().getDeepFilteredFiles(new TrueFilter());
        return picturesToShow;
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new AllPicturesThumbnailAdapter(getPicturesToShow(),this,getFolderToReturn());
    }

    @Override
    public String getActionBarTitle() {
        return "All Pictures";
    }
}