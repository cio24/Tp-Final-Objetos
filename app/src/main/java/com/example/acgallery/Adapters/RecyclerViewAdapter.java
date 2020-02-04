package com.example.acgallery.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acgallery.Composited.AbstractFile;
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.R;

/*
    Esta clase es la encargada de administrar todas las imagenes que se van a mostrar en la interfaz,
    basciamente crea todos los vies para las imagenes.
    Es parametrizada porqué necesita el holder (o contenedor )de cada imagen, esta clase la tenemos
    que crear nosotros.
*/
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    /*
        Esta clase se crea para que el adaptador la utilice para mostrar cada item (en nuestro caso
        una imagen con un texto) que se quiera mostrar en el recyclerview.
    */
    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnailToShow;
        private TextView folderNameToShow;

        /*
            Constructor:
            Cada viewHolder tendra un itemView cuya interfaz sera la determinada por el itemView
            que esta dentro de image_item.xml cuyo id es IMAGEN
        */
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailToShow = (ImageView) itemView.findViewById(R.id.thumbnail_holder);
            folderNameToShow = (TextView) itemView.findViewById(R.id.folder_name_holder);
        }
    }

    // Una vez que tenemos el holder listo, pasamos a implementar los métodos del adaptador.

    private Folder folderToShow; //De acá es de donde se van a tomar todas las imagenes a mostrar.
    private Context context; //el contexto es necesario pero no tengo bien claro xq.
    private boolean pasteMode;
    private Class cls;

    // Constructor
    public RecyclerViewAdapter(Folder folderToShow, Context context, Class cls, boolean pasteMode){
        this.folderToShow = folderToShow;
        this.cls = cls;
        this.context = context;
        this.pasteMode = pasteMode;
    }

    /*
        Este método crea nuevos view holder y es invocado por el layoutmanager (después fijate que en ThumbnailsActivity
        el que utuliza el adaptador es el gridLayout).
    */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        /*
            esto es lo que crea una nueva view que sera mostrada dentro de la interfaz del image item layout
            al igual que cuando se agrega el layout de un menu a otro layout, se debe utilizar el método
            inflate.
         */
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.item_image_layout,viewGroup,false);

        //se la retorna dentro del view holder para poder ser utilizada por el siguiente método
        return new ViewHolder(view);
    }

    /*
        Este método liga el holder (que posee el view creado por el método anterior) con la imagen que esta
        en la posición posicion, es decír reemplazada el contenido del view dentro del view holder por
        la imagen que queresmos mostrar.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        /*
            Picasso es una librería que simplifica la utilización de imagenes
            por ejemplo, Sin Picasso, para poder mostrar una imagen que esta representada por una
            instancia de la clase File tenemos que hacer lo siguiente:
                Bitmap myBitmap = BitmapFactory.decodeFile(images.get(position).getAbsolutePath());
                holder.image.setImageBitmap(myBitmap);

            mientras que con Picasso:
                Picasso.get().load(images.get(position).into(holder.image);

            Pero no utilizo Picasso solo para ahorrar una linea de código, también te permite
            cambiar el tamaño de la imagen con el resize, lo cual es clave ya que
            si mostramos las imagenes completas, por más que utilicemos un recyclerView
            la aplicación termina consumiendo mucha memoria y hace que se cierre por el error
            Java.lang.outOfMemory
        */
        final AbstractFile thumbnail = folderToShow.getFileAt(position);
        thumbnail.bindThumbnailToView(holder.thumbnailToShow,holder.folderNameToShow);

        /*
         Agregamos un Evento que nos muestre la imagen en pantalla completa cada vez que es tocada
         o bien abra una nueva carpeta
         */
        if(pasteMode)
            holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(thumbnail.getInnerFile().isDirectory())
                        thumbnail.open(context,cls);
                }
            });
        else
            holder.thumbnailToShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thumbnail.open(context,cls);
                }
            });
        Toast.makeText(context, "Cantidad de elementos: " + folderToShow.getFilesAmount(), Toast.LENGTH_SHORT).show();
    }

    //Acá simplemente le damos la cantidad de datos que tenemos para que no haga cagada con el indice (position)
    @Override
    public int getItemCount() {
        return folderToShow.getFilesAmount();
    }
}