package com.example.acgallery.Utilities;

import android.content.Context;
import android.os.Environment;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Composite.Picture;
import com.example.acgallery.Filters.TrueFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileManager {

    public static Folder getFolderRootLoaded(ArrayList<String> directoriesNameToFind, ArrayList<String> searchPaths, ArrayList<String> fileExtensions, ArrayList<String> excludedDirectories){
        Folder folderRoot = new Folder(Environment.getDataDirectory()); //first we create the folder root

        File directory;
        ArrayList<File> directoriesFound = new ArrayList<>();

        /*
            if we find a directory with a given name we save its path then we check whether it is contained in a path found before
            so we don't display the directory's files twice
         */
        for (String directoryPath: directoriesNameToFind) {
            for(String innerPath: searchPaths){
                directory = findDirectory(directoryPath, new File(innerPath));
                if(directory != null)
                    if(!isDirectoryAlreadyTracked(directory,directoriesFound))
                        directoriesFound.add(directory);
            }
        }

        //we create a Folder with each directory found then we load every Folder
        Folder folder;
        for(File dir: directoriesFound){
            folder = getFolderLoaded(dir,fileExtensions,excludedDirectories);
            if(folder.getItemsNumber(new TrueFilter()) > 0)
                folderRoot.add(folder);
        }
        return folderRoot;
    }

    //it searches for a directory with the given name in the directory source
    private static File findDirectory(String directoryName, File directorySource) {
        File[] files = directorySource.listFiles();
        if (files != null) {
            File aux;
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if(files[i].getName().equals(directoryName)){
                        return files[i];
                    }
                    aux = findDirectory(directoryName, files[i]); // deep searching
                    if(aux != null){
                        return aux;
                    }
                }
            }
        }
        return null; // the directory is not on the storage
    }

    private static boolean isDirectoryAlreadyTracked(File directory, ArrayList<File> directoriesFound){
        int pathsAmount = directoriesFound.size();
        int index = 0;
        boolean isTracked = false;
        String path;
        String newPath = directory.getAbsolutePath();
        for(int i = 0; i < pathsAmount; i++){
            path = directoriesFound.get(index).getAbsolutePath();
            if(newPath.contains(path)){
                isTracked = true;
                break;
            }
            if(path.contains(newPath)){
                directoriesFound.remove(index);
                continue;
            }
            index++;
        }
        return isTracked;
    }

    //it loads all the pictures from the directory source into the given folder_thumbnail
    private static Folder getFolderLoaded(File directorySource, ArrayList<String> extensions, ArrayList<String> excludedDirectories){
        File[] files = directorySource.listFiles();
        Folder parent = new Folder(directorySource), child;

        if(files != null){

            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) {
                    for(String extension: extensions)
                        if(files[i].getName().endsWith(extension))
                            parent.add(new Picture (files[i]));
                }
                else{
                    boolean isExcludedDirectory = false;
                    for(String excludedDirectory : excludedDirectories)
                        if (excludedDirectory.equals(files[i].getName())) {
                            isExcludedDirectory = true;
                            break;
                        }

                    if(!isExcludedDirectory){
                        child = getFolderLoaded(files[i], extensions, excludedDirectories); // deep loading
                        if(child.getItemsNumber(new TrueFilter()) > 0)
                            parent.add(child);
                    }
                }
            }
            if(parent.getItemsNumber(new TrueFilter()) == 0){
                Folder container = parent.getParent();
                if(container != null){
                    container.removeFile(parent);
                }
            }
        }

        return parent;
    }

    public static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;

    }
}
