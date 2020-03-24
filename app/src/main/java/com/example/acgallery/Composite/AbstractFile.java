package com.example.acgallery.Composite;

import android.content.Context;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.acgallery.Filters.CriterionFilter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public abstract class AbstractFile implements Serializable {

    private Folder container; //in order to delete this file we must have a reference of the folder_thumbnail that has it.
    public File innerFile; //file allocated in phone storage

    public AbstractFile(File innerFile){
        this.innerFile = innerFile;
        container = null;
    }

    public String getAbsolutePath(){
        return this.innerFile.getAbsolutePath();
    }

    public void setInnerFile(File innerFile){
        this.innerFile = innerFile;
    }

    protected void setContainer(Folder container){
        this.container = container; //Protected because we use this method on Folder (add()) & Picture.
    }

    public Folder getContainer(){ //Protected because we use this method on Folder (getPath()) & Picture.
        return this.container;
    }

    public String getName(){//return the entire name with the extension file.
        return innerFile.getName();
    }

    public String getCreationTime(){
        String time = null;
        BasicFileAttributes attributes = null;
        Path filePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            filePath = Paths.get(this.getAbsolutePath());
            try {
                attributes = Files.readAttributes(filePath,BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            time = attributes.creationTime().toString();
        }
        return time.substring(0,time.indexOf("T"));
    }

    public File getInnerFile() { return this.innerFile; }

    public boolean delete() {
        Folder container = getContainer();
        if(container != null){
            return container.deleteFile(this);
        }
        return false;
    }

    public boolean equals(Object o) { //it only compare the names of the objects..
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFile file = (AbstractFile) o;
        if(file.getName() == this.getName()) return true;
        return false;
    }


    //this methods opens the thumbnails activity to show all the pictures that this folder has
    public abstract void open(Context context, Class cls);

    public abstract boolean rename(String newName);
    public abstract void bindThumbnailToView(ImageView image, TextView text);
    public abstract boolean copyTo(Folder destination);
    public abstract boolean moveTo(Folder destination);
    public abstract ArrayList<AbstractFile> getDeepFilteredFiles(CriterionFilter c);
}
