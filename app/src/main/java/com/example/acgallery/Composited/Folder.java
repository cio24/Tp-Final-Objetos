package com.example.acgallery.Composited;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acgallery.Activities.MainActivity;
import com.example.acgallery.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class Folder extends AbstractFile{

    private ArrayList<AbstractFile> files;


    //BEGIN FOLDER EXCLUSIVE BEHAVIOR-------------------------------------

    public Folder(File innerFile) {
        super(innerFile);
        files = new ArrayList<>();
    }

    @Override
    public void bindThumbnailToView(ImageView image, TextView text) {

        Picasso.get().load(R.drawable.folder).resize(300,300).into(image);
        text.setText(getName());
    }

    @Override
    public void open(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        /*
            se agrega al canal el path de la imagen que se quiere mostrar, además
            se agrega un nombre, que es como una id que permite obtener el dato desde
            la activity ImageActivity. Es necesario este id por si se quieren pasar muchos
            datos...
        */

        intent.putExtra("idFolder",this);



        // Una vez hecha todas las configuraciones, se inicia la nueva activity con la imagen que tocamos
        context.startActivity(intent);
    }



    public boolean add(AbstractFile f) {
        /*
        for(AbstractFile file: files){
            if(file.equals(f)){
                f.setCopyNumber(f.getCopyNumber() + 1);
                return add(f);
            }
        }
        */
        f.setContainer(this);
        files.add(f);
        return true;
    }

    public boolean deleteFile(AbstractFile f) {
        /*
        Acá hablando un poco de interfaz... ¿Qué pasa si lo que queremos eliminar es una carpeta?
        Porque si eliminamos la carpeta así nomás, los archivos que estaban dentro de la misma
        seguirían existiendo. De esta manera, al estar procesando miniaturas, quizás estaríamos
        mostrando algunas que sean erróneas, es decir, de archivos que se encontraban dentro de
        la carpeta que eliminamos.
        Entonces, a lo que voy es, ¿deberíamos hacer el deleteFile en profundidad?
         */
        return files.remove(f);
    }

    public boolean existName(String name) {
        for (AbstractFile f : files) {
            if (f.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public int getFilesAmount() {
        /*
        Acá otra cosa. Revisando el explorador de archivos de windows, la cantidad de archivos la
        cuenta en profundidad. ¿Lo hacemos igual o lo dejamos así como está?
         */
        return files.size();
    }

    public AbstractFile getFileAt(int index) {
        if ((index >= 0) && (index < files.size())) {
            return files.get(index);
        }
        return null;
    }

    public int getFilePos(AbstractFile f) {
        for(int i = 0; i < files.size(); i++){
            if(files.get(i).getName().equals(f.getName()))
                return i;
        }
        return -1;
    }

    public Object getImageFolder() {
        return R.drawable.folder;
    }



    //END FOLDER EXCLUSIVE BEHAVIOR---------------------------------------

    //BEGIN IMPLEMENTATION OF INHERIT METHODS-----------------------------

    /*
    @Override
    public String getPath() {
        Folder container = getContainer();
        if(container == null){ //if this is a root folder
            return "/" + this.getName(); //with the copyNumber included
        }
        return container.getPath() + "/" + this.getName(); //with the copyNumber included
    }

    @Override
    public float getSize() {
        float counter = 0;
        for(AbstractFile file: files){
            counter += file.getSize();
        }
        return counter;
    }
     */

    /*
    @Override
    public AbstractFile getCopy() {
        Folder folderCopy = new Folder(this.originalDirectory);
        for(AbstractFile file: files){
            folderCopy.add(file.getCopy());
        }
        return folderCopy;
        return null;
    }

     */


    //END IMPLEMENTATION OF INHERIT METHODS-------------------------------
}