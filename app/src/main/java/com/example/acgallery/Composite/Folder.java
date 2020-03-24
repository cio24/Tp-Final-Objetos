package com.example.acgallery.Composite;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acgallery.Filters.CriterionFilter;
import com.example.acgallery.R;
import com.example.acgallery.Sorters.CriterionSorter;
import com.example.acgallery.Sorters.TypeSort;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Folder extends AbstractFile {

    private ArrayList<AbstractFile> files;
    private CriterionSorter criterion;

    //we initially sort the files by type so we first show the folder and then the pictures
    public Folder(File innerFile) {
        super(innerFile);
        files = new ArrayList<>();
        criterion = new TypeSort();
    }

    //This methods binds the image of a folder and the name of it to the view
    @Override
    public void bindThumbnailToView(ImageView image, TextView text) {
        //we change the size of the picture to prevent a stack overflow then we bind the picture with the view
        Picasso.get().load(R.drawable.folder_thumbnail).resize(300,300).into(image);

        //we also put the name of the folder on the view
        text.setText(getName());
    }

    //this methods opens the thumbnails activity to show all the pictures that this folder has
    @Override
    public void open(Context context, Class cls) {

        this.sort(criterion);

        Intent intent = new Intent(context, cls);
        intent.putExtra("idFolder",this);

        //before start the thumbnail activity we make sure to close the current activity displayed
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
    }

    public boolean add(AbstractFile f) {
        f.setContainer(this);
        files.add(f);
        return true;
    }

    public boolean deleteFile(AbstractFile f) {
        if(f.innerFile.delete()) {
            files.remove(f);
            return true;
        }
        return false;
    }

    public boolean removeByName(String name){
        for (AbstractFile f: files) {
            if (f.getName().equals(name)){
                files.remove(f);
                return true;
            }
        }
        return false;
    }

    public int getFilesAmount() {
        return files.size();
    }

    public AbstractFile getFileAt(int index) {
        if ((index >= 0) && (index < files.size())) {
            return files.get(index);
        }
        return null;
    }

    private void sort(CriterionSorter criterion){
        this.criterion = criterion;
        AbstractFile aux;
        for(int i = 0; i < files.size() - 1; i++){
            for (int j = i+1; j < files.size(); j++){
                if (!criterion.lessThan(files.get(i), files.get(j))){
                    aux = files.remove(i);
                    files.add(i, files.remove(j-1));
                    files.add(j, aux);
                }
            }
        }
    }

    public void setCriterionSorter(CriterionSorter criterion){
        this.criterion = criterion;
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
        this.getContainer().removeByName(this.getName());
        File renamed = new File(destination.getAbsolutePath() + "/" + this.getName());
        int copyNumber = 1;
        while(renamed.exists()){
            renamed = new File(destination.getAbsolutePath() + "/" + this.getName() + " (" + copyNumber + ")");
            copyNumber++;
        }
        //renamed.mkdir();
        if(getInnerFile().renameTo(renamed)){
            setInnerFile(renamed);
            destination.add(this);
            return true;
        }
        return false;
    }
}