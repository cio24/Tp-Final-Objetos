package com.example.acgallery.Adapters;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.acgallery.Activities.ActivitiesHandler;
import com.example.acgallery.Activities.PasteActivity;
import com.example.acgallery.Composite.AbstractFile;
import java.util.ArrayList;

public class PasteAdapter extends RecyclerViewAdapter {

    public PasteAdapter(ArrayList<AbstractFile> filesToShow, AppCompatActivity originActivity) {
        super(filesToShow,originActivity);
    }

    @Override
    public void setListener(ViewHolder holder, final AbstractFile thumbnail) {
        holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thumbnail.getRealFile().isDirectory()) {
                    ActivitiesHandler.addData("folderToShow",thumbnail);
                    ActivitiesHandler.sendData(originActivity,PasteActivity.class);
                    /*
                    intent = new Intent(context, PasteActivity.class);
                    intent.putExtra("file", thumbnail);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

                     */
                }
            }
        });
    }
}
