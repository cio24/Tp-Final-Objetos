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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Picture extends AbstractFile {

    public Picture(File innerFile) {
        super(innerFile);
    }

    //This methods binds the image of a folder and the name of it to the view
    @Override
    public void bindThumbnailToView(ImageView image, TextView text) {
        //we change the size of the picture to prevent a stack overflow then we bind the picture with the view
        Picasso.get().load(new File(getAbsolutePath())).resize(300,300).centerCrop().into(image);

        //when we bind pictures to the view holder we don't put any text over the picture
        text.setText("");
    }

    //this methods opens the thumbnails activity to show all the pictures that this folder has
    @Override
    public void open(Context context,Class cls) {
        Intent intent = new Intent(context, FullPictureActivity.class);
        intent.putExtra("fullPicture",this);

        //before start the thumbnail activity we make sure to close the current activity displayed
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
        if (c.satisfy(this)){
            toReturn.add(this);
            return toReturn;
        }
        return null;
    }

    public String getBaseName(){
        return getName().substring(0,getName().lastIndexOf("."));
    }

    public String getExtension(){
        return getName().substring(getName().lastIndexOf("."));
    }

}