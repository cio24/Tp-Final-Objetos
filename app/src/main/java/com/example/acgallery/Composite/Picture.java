package com.example.acgallery.Composite;

import com.example.acgallery.Filters.CriterionFilter;

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

    @Override
    public boolean rename(String newName) {
        File renamed = new File(getParent().getAbsolutePath() +
                "/" + newName + getExtension());
        int copyNumber = 1;
        while(renamed.exists()){
            renamed = new File(getParent().getAbsolutePath() +
                    "/" + newName + " (" + copyNumber + ")" + getExtension());
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
        File fileCopy = new File(destination.getAbsolutePath()+"/"+getName());
        int copyNumber = 1;
        while(fileCopy.exists()){
            fileCopy = new File(destination.getAbsolutePath() + "/" + getBaseName() + " (" + copyNumber +")" + getExtension());
        }
        try (InputStream in = new FileInputStream(this.getRealFile())) {
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
        if(this.copyTo(destination))
            return this.delete();
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