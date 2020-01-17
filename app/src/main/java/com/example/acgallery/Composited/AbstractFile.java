package com.example.acgallery.Composited;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;

public abstract class AbstractFile implements Serializable {

    private int copyNumber; //it is used for rename this file when there's another file with the same name in the container.
    private Folder container; //in order to delete this file we must have a reference of the folder that has it.
    public File innerFile; //file allocated in phone storage

    //BEGIN CONSTRUCTORS------------------------------------------------

    public AbstractFile(File innerFile){
        this.copyNumber = 1;
        this.innerFile = innerFile;
        container = null;
    }

    //END CONSTRUCTORS----------------------------------------------------

    //BEGIN SETTERS & GETTERS---------------------------------------------

    public String getAbsolutePath(){
        return this.innerFile.getAbsolutePath();
    }

    protected void setContainer(Folder container){ // Check. Shouldn't we do the comprobation of an AbstractFile on the same container?
        this.container = container; //Protected because we use this method on Folder (add()) & Picture.
    }

    public Folder getContainer(){ //Protected because we use this method on Folder (getPath()) & Picture.
        return this.container;
    }

    public void setName(String name){
        //setCopyNumber(1); //we do this 'cause the previous name could be duplicated thus copyNumber > 1
        if(this.container != null) {//if it is not a root folder
            if (this.container.existName(name)) {
                this.copyNumber++;
                setName(name + "(" + this.copyNumber + ")");
            }
            else{
                this.copyNumber = 1;
                //innerFile.renameTo();
            }
        }
        else{
            //innerFile.renameTo(); investigar
        }

    }

    public String getName(){//return the entire name with the extension file.
        return innerFile.getName();
    }

    public boolean moveTo(Folder destination) {

        /*
        destination.add(this);

        if(container != null) {
            container.deleteFile(this);
        }
        else{
            Log.d(TAG, "NO TE TENDRIA QUE PODER MOVER UNA CARPETA ROOT!");
        }

        String pathDestination = getContainer().getAbsolutePath() + "/" + getName();
        File targetLocation = new File (pathDestination);

        // just to take note of the location sources
        Log.d(TAG, "sourceLocation: " + getAbsolutePath());
        Log.d(TAG, "targetLocation: " + targetLocation.getAbsolutePath());

        try {
            // moving the file to another directory
            if(innerFile.renameTo(targetLocation))
                Log.d(TAG, "Move file successful.");
            else
                Log.d(TAG, "Move file failed.");
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    */
        return true;
    }

    public boolean copyTo(Folder destination){
        /*
        Folder container = this.getContainer();
        if(container != null) {
            if (this.getCopyNumber() > 1){ //Only if it is bigger than 1, otherwise it makes no sense.
                this.setName(this.getName() + " (" + this.getCopyNumber() + ")"); //Actual name + copyNumber
                // this.setCopyNumber(1); //Reset copyNumber. Actually not necessary because we do it on setName().
            }
            this.setCopyNumber(1);
            destination.add(this.getCopy());
            return true;
        }

         */
        return false;
    }

    public boolean delete() {
        Folder container = getContainer();
        if(container != null)
            return container.deleteFile(this);
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
    public abstract void open(Context context);
    //public abstract String getRelativePath();
    // public abstract float getSize();
    //public abstract AbstractFile getCopy();


    //END ABSTRACT METHODS------------------------------------------------

}
