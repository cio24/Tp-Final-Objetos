/*
 * This interface is thought as a way to create filters that let you filters files by a certain criteria
 * the method satisfy returns true when the
 */

package com.example.acgallery.Filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.widget.ImageView;
import com.example.acgallery.Classifiers.Classifiable;
import com.example.acgallery.Composite.AbstractFile;
import java.util.ArrayList;
import java.util.HashMap;

public class ClassifierFilter implements Filterable {
    private Classifiable classifiable;
    private HashMap<String,Boolean> labels;
    private Context context;

    public ClassifierFilter(Context context, ArrayList<String> labels, Classifiable classifiable){
        this.context = context;
        this.classifiable = classifiable;
        this.labels = new HashMap<>();

        for(String label: labels)
            this.labels.put(label.toLowerCase(),true);
    }

    @Override
    public boolean satisfy(AbstractFile file) {

        ImageView image = new ImageView(context);
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        image.setImageBitmap(myBitmap);
        ArrayList<Pair<String, Float>> results = classifiable.classify(image);
        String key;
        for(int i = 0; i < results.size() -1; i++){
            key = results.get(i).first.toLowerCase();
            if(this.labels.get(key) != null)
                return true;
        }
        return false;
    }
}
