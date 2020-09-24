/*
 * this class represents a picture and it implements all the abstract methods of AbstractFile and also
 *  provide getters for the width and the height of the image
 */

package com.example.acgallery.Model.Composite;

import android.graphics.BitmapFactory;
import com.example.acgallery.Model.Filters.Filterable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Picture extends AbstractFile {

    //constructor

    public Picture(File realFile) {
        super(realFile);
    }


    //getters

    public int getWidth(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getRealFile().getAbsolutePath(), options);
        return options.outWidth;
    }

    public int getHeight(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getRealFile().getAbsolutePath(), options);
        return options.outHeight;
    }


    //abstracts implementation

    @Override
    public boolean rename(String newName) {
        File renamed = new File(getParent().getAbsolutePath() + "/" + newName + getName().substring(getName().lastIndexOf(".")));
        int copyNumber = 1;

        while(renamed.exists()){
            renamed = new File(getParent().getAbsolutePath() + "/" + newName + " (" + copyNumber + ")" + getName().substring(getName().lastIndexOf(".")));
            copyNumber++;
        }

        if(getRealFile().renameTo(renamed)){
            setRealFile(renamed);
            return true;
        }
        return false;
    }

    @Override
    public boolean copyTo(Folder destination) {
        File fileCopy = new File(destination.getAbsolutePath() + "/" + getName());
        int copyNumber = 0;

        while(fileCopy.exists()){
            copyNumber++;
            fileCopy = new File(destination.getAbsolutePath() + "/" + getName().substring(0,getName().lastIndexOf(".")) + " (" + copyNumber +")" + getName().substring(getName().lastIndexOf(".")));
        }

        try (InputStream in = new FileInputStream(this.getRealFile())) {
            try (OutputStream out = new FileOutputStream(fileCopy)) {
                byte[] buf = new byte[1024]; // Transfer bytes from in to out
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Picture copy = new Picture(fileCopy);
        return destination.add(copy);
    }

    @Override
    public boolean moveTo(Folder destination) {
        if(this.copyTo(destination))
            return this.delete();
        return false;
    }

    @Override
    public boolean delete() {
        if(getRealFile().delete())
            return getParent().removeFile(this);
        return false;
    }

    @Override
    public int getDeepItemsNumber(Filterable filterable) {
        if(filterable.satisfy(this))
            return 1;
        return 0;
    }

    @Override
    public long size() {
        return this.getRealFile().length();
    }

    @Override
    public ArrayList<AbstractFile> getDeepFilteredFiles(Filterable filterable) {
        ArrayList<AbstractFile> toReturn = new ArrayList<>();
        if (filterable.satisfy(this)){
            toReturn.add(this);
            return toReturn;
        }
        return null;
    }
}