package com.example.acgallery.Composite;

import android.util.Log;
import android.widget.Toast;

import com.example.acgallery.Filters.CriterionFilter;
import com.example.acgallery.Sorters.CriterionSorter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Folder extends AbstractFile {

    private ArrayList<AbstractFile> files;

    public Folder(File realFile) {
        super(realFile);
        files = new ArrayList<>();
    }

    public int getFilesAmount() {
        return files.size();
    }
    public ArrayList<AbstractFile> getFilteredFiles(CriterionFilter filter){
        ArrayList<AbstractFile> pictures = new ArrayList<>();
        for(AbstractFile file: files){
            if(filter.satisfy(file))
                pictures.add(file);
        }
        return pictures;
    }
    public ArrayList<AbstractFile> getDeepFilteredFiles(CriterionFilter filter){
        ArrayList<AbstractFile> toReturn = new ArrayList<>();
        ArrayList<AbstractFile> aux;
        for (AbstractFile f:files) {
            aux = f.getDeepFilteredFiles(filter);
            if (aux != null)
                toReturn.addAll(aux);
        }
        if (toReturn.isEmpty()){
            return null;
        }
        return toReturn;
    }
    public  Folder getFolderRoot(){
        Folder folderRoot = this;
        while(folderRoot.getParent() != null){
            folderRoot = folderRoot.getParent();
        }
        return folderRoot;
    }

    /*
    public AbstractFile getFileAt(int index){
        if(index > 0 && index < files.size())
            return files.get(index);
        return null;
    }

     */

    public boolean add(AbstractFile abstractFile) {
        abstractFile.setParent(this);
        return files.add(abstractFile);
    }
    public boolean removeFile(AbstractFile abstractFile) {
            return files.remove(abstractFile);
    }

    public void sort(CriterionSorter criterionSorter){
        AbstractFile aux;
        for(int i = 0; i < files.size() - 1; i++){
            for (int j = i+1; j < files.size(); j++){
                if (!criterionSorter.lessThan(files.get(i), files.get(j))){
                    aux = files.remove(i);
                    files.add(i, files.remove(j-1));
                    files.add(j, aux);
                }
            }
        }
    }

    /*
        the following methods are not implemented because they're not related
        with object oriented programming concepts
     */
    @Override
    public boolean rename(String newName) {
        return false;
    }

    @Override
    public boolean copyTo(Folder destination){
        File directoryCopy = new File(destination.getAbsolutePath()+ "/" + getName());
        int copyNumber = 1;
        while(directoryCopy.exists()){
            directoryCopy = new File(destination.getAbsolutePath() + "/" + getName() + " (" + copyNumber +")");
        }
        directoryCopy.mkdir();
        Folder newFolder = new Folder(directoryCopy);
        destination.add(newFolder);
        for(AbstractFile file: files)
            file.copyTo(newFolder);
        return true;
    }

    @Override
    public boolean moveTo(Folder destination) {
        /*
        this.getParent().removeByName(this.getName());
        File renamed = new File(destination.getAbsolutePath() + "/" + this.getName());
        int copyNumber = 1;
        while(renamed.exists()){
            renamed = new File(destination.getAbsolutePath() + "/" + this.getName() + " (" + copyNumber + ")");
            copyNumber++;
        }
        //renamed.mkdir();
        if(getRealFile().renameTo(renamed)){
            setRealFile(renamed);
            destination.add(this);
            return true;
        }

         */
        return false;
    }

    @Override
    public boolean delete() {
        try {
            FileUtils.deleteDirectory(getRealFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getParent().removeFile(this);
    }
}