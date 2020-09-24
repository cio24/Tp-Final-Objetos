package com.example.acgallery.Model.Filters;

import com.example.acgallery.Model.Composite.AbstractFile;

public class TrueFilter implements Filterable {

    @Override
    public boolean satisfy(AbstractFile file) {
        return true;
    }
}
