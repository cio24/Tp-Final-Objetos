package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;
import java.io.Serializable;

public class FoldersFirstSorter implements Sorteable, Serializable {
    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        return a.getRealFile().isDirectory();
    }
}
