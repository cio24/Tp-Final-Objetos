package com.example.acgallery.Filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.example.acgallery.Classifiers.TensorFlowClassifier;
import com.example.acgallery.Composited.AbstractFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClassifierFilter implements CriterionFilter {

    private TensorFlowClassifier classifier;
    private ArrayList<String> labels;
    private ArrayList<Pair<String,Float>> results;
    private Context context;
    private ImageView image;

    public ClassifierFilter(Context context, ArrayList<String> labels){
        this.context = context;
        classifier = new TensorFlowClassifier(context);
        this.labels = labels;
    }

    @Override
    public boolean satisfy(AbstractFile file) {
        image = new ImageView(context);
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        image.setImageBitmap(myBitmap);
        results = classifier.classify(image);

        for(String label: labels){
            for(Pair<String,Float> result : results){
                if(label.equals(result.first))
                    return true;
            }
        }
        return false;
    }
}
