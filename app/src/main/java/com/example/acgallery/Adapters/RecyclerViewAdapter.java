package com.example.acgallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acgallery.Activities.FullPictureActivity;
import com.example.acgallery.Activities.PasteActivity;
import com.example.acgallery.Activities.ThumbnailsActivity;
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
public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView thumbnailToShow;
        private TextView nameToShow;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailToShow = (ImageView) itemView.findViewById(R.id.thumbnail_holder);
            nameToShow = (TextView) itemView.findViewById(R.id.folder_name_holder);
        }
    }

    private ArrayList<AbstractFile> filesToShow;
    protected Context context;
    protected Class destinationActivity;


    public RecyclerViewAdapter(ArrayList<AbstractFile> filesToShow, Context context){
        this.filesToShow = filesToShow;
        this.context = context;
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
        //thumbnail.bindThumbnailToView(holder.thumbnailToShow,holder.folderNameToShow);
        bindThumbnailToView(holder.thumbnailToShow,holder.nameToShow,thumbnail);

        /*
           if the folder was opened to paste a picture then we don't add the listener
           that opens in full screen to each picture.
           if the folder was opened to show the pictures then we add the listener to each picture
           and in both cases we always add the listener that open a folder
         */
        setListener(holder,thumbnail);
    }

    public void bindThumbnailToView(ImageView image, TextView text, AbstractFile thumbnail) {
        if(thumbnail.getRealFile().isDirectory()) {
            //we change the size of the picture to prevent a stack overflow then we bind the picture with the view
            Picasso.get().load(R.drawable.folder_thumbnail).resize(300, 300).into(image);
            //we also put the name of the folder on the view
            text.setText(thumbnail.getName());
        }
        else{
            //we change the size of the picture to prevent a stack overflow then we bind the picture with the view
            Picasso.get().load(new File(thumbnail.getAbsolutePath())).resize(300,300).centerCrop().into(image);

            //when we bind pictures to the view holder we don't put any text over the picture
            text.setText("");
        }
    }

    public abstract void setListener(ViewHolder holder, final AbstractFile thumbnail);

    /*
    public abstract void setListener(ViewHolder holder, final AbstractFile thumbnail){

        holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(thumbnail.getRealFile().isDirectory()){
                    if(pasteMode)
                        intent = new Intent(context, PASTE_ACTIVITY);
                    else
                        intent = new Intent(context, THUMBNAILS_ACTIVITY);
                    intent.putExtra("folder",thumbnail);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
                else if(!pasteMode){
                    intent = new Intent(context, FULL_PICTURE_ACTIVITY);
                    //intent.putExtra("allPictures",true);
                    intent.putExtra("pictureToShow",thumbnail);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

     */



    @Override
    public int getItemCount() {
        return filesToShow.size();
    }
}