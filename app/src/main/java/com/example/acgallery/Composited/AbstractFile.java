package com.example.acgallery.Composited;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acgallery.Filters.CriterionFilter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class AbstractFile implements Serializable {

    private Folder container; //in order to delete this file we must have a reference of the folder_thumbnail that has it.
    public File innerFile; //file allocated in phone storage

    //BEGIN CONSTRUCTORS------------------------------------------------

    public AbstractFile(File innerFile){
        this.innerFile = innerFile;
        container = null;
    }

    //END CONSTRUCTORS----------------------------------------------------

    //BEGIN SETTERS & GETTERS---------------------------------------------

    public String getAbsolutePath(){
        return this.innerFile.getAbsolutePath();
    }

    public void setInnerFile(File innerFile){
        this.innerFile = innerFile;
    }

    protected void setContainer(Folder container){ // Check. Shouldn't we do the comprobation of an AbstractFile on the same container?
        this.container = container; //Protected because we use this method on Folder (add()) & Picture.
    }

    public Folder getContainer(){ //Protected because we use this method on Folder (getPath()) & Picture.
        return this.container;
    }

    public String getName(){//return the entire name with the extension file.
        return innerFile.getName();
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


    //END SETTERS & GETTERS-----------------------------------------------

    //BEGIN ABSTRACT METHODS----------------------------------------------
    public abstract void bindThumbnailToView(ImageView image, TextView text);

    public abstract void open(Context context, Class cls);
    public abstract boolean rename(String newName);
    public abstract boolean copyTo(Folder destination);
    public abstract boolean moveTo(Folder destination);
    public abstract ArrayList<AbstractFile> deepCopy(CriterionFilter c);
    //public abstract String getRelativePath();
    // public abstract float getSize();
    //public abstract AbstractFile getCopy();


    //END ABSTRACT METHODS------------------------------------------------

}
