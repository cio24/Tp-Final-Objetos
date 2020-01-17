package com.example.acgallery.Composited;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acgallery.Activities.ImageActivity;
import com.example.acgallery.MainActivityTwo;
import com.squareup.picasso.Picasso;

import java.io.File;

public class Picture extends AbstractFile {

    private float size;
    private String type;

    //BEGIN PICTURE EXCLUSIVE BEHAVIOR-------------------------------------

    public Picture(float size, String type, File innerFile) {
        super(innerFile);
        this.size = size;
        this.type = type;
    }

    public String getType() {
        return this.type;
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
        File f = new File(getAbsolutePath());
        Picasso.get().load(f).resize(300,300).centerCrop().into(image);
        text.setText("");
    }

    @Override
    public void open(Context context) {
        Intent intent = new Intent(context, ImageActivity.class);
        //Intent intent = new Intent(context, MainActivityTwo.class);
        /*
            se agrega al canal el path de la imagen que se quiere mostrar, además
            se agrega un nombre, que es como una id que permite obtener el dato desde
            la activity ImageActivity. Es necesario este id por si se quieren pasar muchos
            datos...
        */
        //intent.putExtra("fullImage",getAbsolutePath());
        intent.putExtra("fullImage",this);


        // Una vez hecha todas las configuraciones, se inicia la nueva activity con la imagen que tocamos
        context.startActivity(intent);
    }

    //END IMPLEMENTATION OF INHERIT METHODS-----------------------------
}