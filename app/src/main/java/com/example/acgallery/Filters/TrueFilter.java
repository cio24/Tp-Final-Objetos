package com.example.acgallery.Filters;

import com.example.acgallery.Composite.AbstractFile;

public class TrueFilter implements Filterable {

    @Override
    public boolean satisfy(AbstractFile file) {
        return true;
    }
}
