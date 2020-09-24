package com.example.acgallery.Model.Comparators;

import com.example.acgallery.Model.Composite.AbstractFile;
import java.io.Serializable;

public class FoldersFirstComparator implements Comparable, Serializable {
    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        return a.getRealFile().isDirectory();
    }
}
