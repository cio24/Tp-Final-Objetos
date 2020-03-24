package com.example.acgallery.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/*
    This class is has to administrate all the pictures to be shown, it creates all the holders required
    and bind every picture to each one of them. The holder is a inner class that we must to create
    in order to adapt it to our needs, this means that the holder has two views, one for the picture or the folder image
    and another for the text that indicates the name of the folder.
*/
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnailToShow;
        private TextView folderNameToShow;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailToShow = (ImageView) itemView.findViewById(R.id.thumbnail_holder);
            folderNameToShow = (TextView) itemView.findViewById(R.id.folder_name_holder);
        }
    }

    private ArrayList<AbstractFile> filesToShow;
    private Context context;
    private boolean pasteMode;
    private Class cls;

    public RecyclerViewAdapter(ArrayList<AbstractFile> filesToShow, Context context, Class cls, boolean pasteMode){
        this.filesToShow = filesToShow;
        this.cls = cls;
        this.context = context;
        this.pasteMode = pasteMode;
    }

    //this methods creates new viewHolder and it is used for the layoutmanager
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.item_image_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    //this methods binds the holder with the image that is located in the given position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final AbstractFile thumbnail = filesToShow.get(position);
        thumbnail.bindThumbnailToView(holder.thumbnailToShow,holder.folderNameToShow);

        /*
           if the folder was opened to paste a picture then we don't add the listener
           that opens in full screen to each picture.
           if the folder was opened to show the pictures then we add the listener to each picture
           and in both cases we always add the listener that open a folder
         */

        if(pasteMode)
            holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(thumbnail.getInnerFile().isDirectory())
                        thumbnail.open(context,cls);
                }
            });
        else
            holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thumbnail.open(context,cls);
                }
            });
    }

    @Override
    public int getItemCount() {
        return filesToShow.size();
    }
}