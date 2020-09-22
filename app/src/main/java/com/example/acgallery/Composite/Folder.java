package com.example.acgallery.Composite;

import com.example.acgallery.Filters.CriterionFilter;
import com.example.acgallery.Sorters.CriterionSorter;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Folder extends AbstractFile {
    private ArrayList<AbstractFile> files;
    private static Folder folderRoot;

    public Folder(File realFile) {
        super(realFile);
        files = new ArrayList<>();
        if(folderRoot == null)
            folderRoot = this;
    }

    public ArrayList<AbstractFile> getFilteredFiles(CriterionFilter criterionFilter){
        ArrayList<AbstractFile> pictures = new ArrayList<>();
        for(AbstractFile file: files){
            if(criterionFilter.satisfy(file))
                pictures.add(file);
        }
        return pictures;
    }

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
                if (criterionSorter.lessThan(files.get(j), files.get(i))){
                    aux = files.remove(i);
                    files.add(i, files.remove(j-1));
                    files.add(j, aux);
                }
            }
        }
    }

    public int getItemsNumber(CriterionFilter criterionFilter){
        int counter = 0;
        for(AbstractFile abstractFile: files)
            if(criterionFilter.satisfy(abstractFile))
                counter++;
        return counter;
    }

    public AbstractFile get(int index){
        if(index >= 0 && index < files.size())
            return files.get(index);
        return null;
    }

    public static Folder getFolderRoot() {
        return folderRoot;
    }

    @Override
    public boolean rename(String newName) {
        File renamed = new File(getParent().getAbsolutePath() + "/" + newName);
        int copyNumber = 1;

        while(renamed.exists()){
            renamed = new File(getParent().getAbsolutePath() + "/" + newName + "(" + copyNumber + ")");
            copyNumber++;
        }

        if(getRealFile().renameTo(renamed)){
            setRealFile(renamed);
            return true;
        }
        return false;
    }

    @Override
    public boolean copyTo(Folder destination){
        File directoryCopy = new File(destination.getAbsolutePath() + "/" + this.getName());
        int copyNumber = 0;

        while(directoryCopy.exists()){
            copyNumber++;
            directoryCopy = new File(destination.getAbsolutePath() + "/" + getName() + " (" + copyNumber +")");
        }

        if(!directoryCopy.exists())
            try {
                FileUtils.forceMkdir(directoryCopy);
            } catch (IOException e) {
                e.printStackTrace();
            }

        Folder folderCopy = new Folder(directoryCopy);
        for(int i = 0; i < files.size(); i++){
            files.get(i).copyTo(folderCopy);
        }
        destination.add(folderCopy);
        return true;
    }

    @Override
    public boolean moveTo(Folder destination) {
        if(this.copyTo(destination))
            return this.delete();
        return false;
    }

    @Override
    public boolean delete() {
        while(!files.isEmpty())
            files.get(0).delete();
        return this.getParent().removeFile(this);
    }

    @Override
    public int getDeepItemsNumber(CriterionFilter criterionFilter) {
        int counter = 0;
        if(criterionFilter.satisfy(this))
            counter++;
        for(AbstractFile abstractFile: files){
                counter += abstractFile.getDeepItemsNumber(criterionFilter);

        }
        return counter;
    }

    @Override
    public long size() {
        long counter = 0;
        int n = files.size();
        for(int i = 0; i < n; i++)
            counter += files.get(i).size();
        return counter;
    }

    @Override
    public ArrayList<AbstractFile> getDeepFilteredFiles(CriterionFilter criterionFilter){
        ArrayList<AbstractFile> toReturn = new ArrayList<>();
        ArrayList<AbstractFile> aux;
        for (AbstractFile f:files) {
            aux = f.getDeepFilteredFiles(criterionFilter);
            if (aux != null)
                toReturn.addAll(aux);
        }
        if (toReturn.isEmpty()){
            return null;
        }
        return toReturn;
    }
}