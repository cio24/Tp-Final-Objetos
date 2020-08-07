package com.example.acgallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.acgallery.Activities.FullPictureActivity;
import com.example.acgallery.Composite.AbstractFile;

import java.util.ArrayList;

public class AllPicturesAdapter extends RecyclerViewAdapter {

    public AllPicturesAdapter(ArrayList<AbstractFile> filesToShow, Context context) {
        super(filesToShow, context);
    }

    @Override
    public void setListener(ViewHolder holder, final AbstractFile thumbnail) {
        holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullPictureActivity.class);
                intent.putExtra("file",thumbnail);
                intent.putExtra("allPictures",true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });
    }
}
