package com.example.acgallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.acgallery.Activities.FullPictureActivity;
import com.example.acgallery.Activities.ThumbnailsActivity;
import com.example.acgallery.Composite.AbstractFile;

import java.util.ArrayList;

public class ThumbnailsAdapter extends RecyclerViewAdapter {

    public ThumbnailsAdapter(ArrayList<AbstractFile> filesToShow, Context context) {
        super(filesToShow, context);
    }

    @Override
    public void setListener(ViewHolder holder, final AbstractFile thumbnail) {
        holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (thumbnail.getRealFile().isDirectory())
                    intent = new Intent(context, ThumbnailsActivity.class);
                else {
                    intent = new Intent(context, FullPictureActivity.class);
                    intent.putExtra("allPictures", false);
                }
                intent.putExtra("file", thumbnail);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });
    }
}
