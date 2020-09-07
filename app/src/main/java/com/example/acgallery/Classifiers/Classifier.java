package com.example.acgallery.Classifiers;

import android.util.Pair;
import android.widget.ImageView;
import java.util.ArrayList;

public interface Classifier {

    ArrayList<Pair<String,Float>> classify(ImageView image);
}
