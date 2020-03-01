package com.example.acgallery.Filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.example.acgallery.Classifiers.Classifier;
import com.example.acgallery.Classifiers.TensorFlowClassifier;
import com.example.acgallery.Composited.AbstractFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassifierFilter implements CriterionFilter {

    private Classifier classifier;
    private HashMap<String,Boolean> labels;
    private ArrayList<Pair<String,Float>> results;
    private Context context;
    private ImageView image;
    private static int count;

    public ClassifierFilter(Context context, ArrayList<String> labels, Classifier classifier){
        count = 0;
        this.context = context;
        this.classifier = classifier;
        this.labels = new HashMap<>();
        //classifier = new TensorFlowClassifier(context);
        for(String label: labels)
            this.labels.put(label.toLowerCase(),true);
    }

    @Override
    public boolean satisfy(AbstractFile file) {
        count++;
        Log.d("xenon","Count: " + count);
        image = new ImageView(context);
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        image.setImageBitmap(myBitmap);
        results = classifier.classify(image);
        String key = results.get(2).first.toLowerCase();
        if(this.labels.get(key) == null)
            return false;
        return true;
    }
}
