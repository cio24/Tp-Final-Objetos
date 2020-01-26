package com.example.acgallery.Sorters;

import com.example.acgallery.Composited.AbstractFile;

public class TypeSort implements Criterion{
    // sort by type (picture or folder)
    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        return a.getInnerFile().isDirectory();
    }
}
