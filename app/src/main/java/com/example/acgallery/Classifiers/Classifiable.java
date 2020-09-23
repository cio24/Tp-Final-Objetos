/*
 * This interface is thought as a way to create classifiers that let you classify files by a certain criteria
 * the classification gives you a list of labels that match with something related to the image
 */

package com.example.acgallery.Classifiers;

import android.util.Pair;
import android.widget.ImageView;
import java.util.ArrayList;

public interface Classifiable {
    ArrayList<Pair<String,Float>> classify(ImageView image);
}
