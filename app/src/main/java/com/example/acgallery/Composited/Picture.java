package com.example.acgallery.Composited;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acgallery.Activities.FullPictureActivity;
import com.squareup.picasso.Picasso;

import java.io.File;

public class Picture extends AbstractFile {

    //BEGIN PICTURE EXCLUSIVE BEHAVIOR-------------------------------------

    public Picture(File innerFile) {
        super(innerFile);
    }

    public String getType() {
        return null;
    }

    //END PICTURE EXCLUSIVE BEHAVIOR---------------------------------------

    //BEGIN IMPLEMENTATION OF INHERIT METHODS---------------------------

    /*
    @Override
    public float getSize() {
        return size;
    }

    @Override
    public String getPath() {
        Folder container = super.getContainer();
        return container.getPath() + "/" + this.getName() + "." + this.getType();
    }

    @Override
    public AbstractFile getCopy() {

        Picture copy  = new Picture(this.getName(), this.getSize(), this.getType(), this.originalPic);
        copy.setContainer(this.getContainer());
        copy.setCopyNumber(this.getCopyNumber());
        return copy;

        return null;
    }
    */

    @Override
    public void bindThumbnailToView(ImageView image, TextView text) {
        Picasso.get().load(new File(getAbsolutePath())).resize(300,300).centerCrop().into(image);
        text.setText("");
    }

    public String getBaseName(){
        return getName().substring(0,getName().lastIndexOf("."));
    }

    public String getExtension(){
        return getName().substring(getName().lastIndexOf("."));
    }

    @Override
    public void open(Context context) {
        Intent intent = new Intent(context, FullPictureActivity.class);

        /*
            se agrega al canal la foto que se quiere mostrar, además
            se agrega un id que permite obtener el dato desde
            la activity FullPictureActivity. Es necesario este id por si se quieren pasar muchos
            datos...
        */
        intent.putExtra("fullPicture",this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    //END IMPLEMENTATION OF INHERIT METHODS-----------------------------
}