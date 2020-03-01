package com.example.acgallery.Composited;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acgallery.Activities.FullPictureActivity;
import com.example.acgallery.Filters.CriterionFilter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Picture extends AbstractFile {

    static int count = 0;

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
    public void open(Context context,Class cls) {
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

    @Override
    public boolean rename(String newName) {
        File renamed = new File(getContainer().getAbsolutePath() +
                "/" + newName + getExtension());
        int copyNumber = 1;
        while(renamed.exists()){
            renamed = new File(getContainer().getAbsolutePath() +
                    "/" + newName + " (" + copyNumber + ")" + getExtension());
            copyNumber++;
        }
        if(getInnerFile().renameTo(renamed)){
            setInnerFile(renamed);
            return true;
        }
        return false;
    }

    @Override
    public boolean copyTo(Folder destination) {
        File fileCopy = new File(destination.getAbsolutePath()+"/"+getName());
        int copyNumber = 1;
        while(fileCopy.exists()){
            fileCopy = new File(destination.getAbsolutePath() + "/" + getBaseName() + " (" + copyNumber +")" + getExtension());
        }
        try (InputStream in = new FileInputStream(innerFile)) {
            try (OutputStream out = new FileOutputStream(fileCopy)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Picture newPic = new Picture(fileCopy);
        destination.add(newPic);
        return true;
    }
    /*
    @Override
    public boolean moveTo(Folder destination) {
        copyTo(destination);
        this.delete();
        return true;
    }
     */

    @Override
    public boolean moveTo(Folder destination) {
        this.getContainer().removeByName(this.getName());
        File renamed = new File(destination.getAbsolutePath() + "/" + this.getName());
        int copyNumber = 1;
        while(renamed.exists()){
            renamed = new File(destination.getAbsolutePath() + "/" + this.getBaseName() + " (" + copyNumber + ")" + getExtension());
            copyNumber++;
        }
        if(getInnerFile().renameTo(renamed)){
            setInnerFile(renamed);
            Picture newPic = new Picture(renamed);
            destination.add(newPic);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<AbstractFile> getDeepFilteredFiles(CriterionFilter c) {
        ArrayList<AbstractFile> toReturn = new ArrayList<>();

        count++;
        Log.d("countinggg", "Count: " + count);
        if (c.satisfy(this)){
            toReturn.add(this);
            return toReturn;
        }
        return null;
    }


    //END IMPLEMENTATION OF INHERIT METHODS-----------------------------
}