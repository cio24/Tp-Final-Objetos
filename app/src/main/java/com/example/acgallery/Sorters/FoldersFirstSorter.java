package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;
import java.io.Serializable;

public class FoldersFirstSorter implements CriterionSorter, Serializable {
    // sort by type (picture or folder_thumbnail)
    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        return a.getRealFile().isDirectory();
    }
}
