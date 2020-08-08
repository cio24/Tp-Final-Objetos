package com.example.acgallery.Adapters;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.acgallery.Activities.ActivitiesHandler;
import com.example.acgallery.Activities.FullPictureActivity;
import com.example.acgallery.Composite.AbstractFile;
import java.util.ArrayList;

public class AllPicturesAdapter extends RecyclerViewAdapter {

    public AllPicturesAdapter(ArrayList<AbstractFile> filesToShow, AppCompatActivity originActivity) {
        super(filesToShow, originActivity);
    }

    @Override
    public void setListener(ViewHolder holder, final AbstractFile thumbnail) {
        holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesHandler.addData("pictureToShow",thumbnail);
                ActivitiesHandler.addData("allPictures",true);
                ActivitiesHandler.sendData(originActivity,FullPictureActivity.class);
                /*
                Intent intent = new Intent(originActivity, FullPictureActivity.class);
                intent.putExtra("file",thumbnail);
                intent.putExtra("allPictures",true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

                 */
            }
        });
    }
}
