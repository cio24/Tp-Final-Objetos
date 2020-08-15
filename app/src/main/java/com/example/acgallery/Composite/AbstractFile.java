package com.example.acgallery.Composite;

import android.os.Build;
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

    private Folder parent; //in order to delete this file we must have a reference of the folder that contains it.
    private File realFile; //file allocated in phone storage

    public AbstractFile(File realFile){
        this.realFile = realFile;
    }

    public String getAbsolutePath(){
        return this.realFile.getAbsolutePath();
    }
    public Folder getParent(){ //Protected because we use this method on Folder (getPath()) & Picture.
        return this.parent;
    }
    public String getName(){//return the entire name with the extension file.
        return realFile.getName();
    }
    public File getRealFile() { return this.realFile; }
    public String getCreationTime(){
        String time = "undefined";
        BasicFileAttributes attributes = null;
        Path filePath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            filePath = Paths.get(getRealFile().getAbsolutePath());
            try {
                attributes = Files.readAttributes(filePath,BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            time = attributes.creationTime().toString().substring(0,attributes.creationTime().toString().indexOf("T"));
        }
        return time;
    }

    public void setRealFile(File realFile){
        this.realFile = realFile;
    }
    public void setParent(Folder parent){
        this.parent = parent;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFile file = (AbstractFile) o;
        return file.getAbsolutePath().equals(this.getAbsolutePath());
    }

    public abstract boolean rename(String newName);
    public abstract boolean copyTo(Folder destination);
    public abstract boolean moveTo(Folder destination);
    public abstract boolean delete();
    public abstract int getDeepCount(CriterionFilter criterionFilter);
    public abstract long size();
    public abstract ArrayList<AbstractFile> getDeepFilteredFiles(CriterionFilter c);
}