package com.example.acgallery.Filters;

import com.example.acgallery.Composite.AbstractFile;

public class PictureFilter implements CriterionFilter {
    @Override
    public boolean satisfy(AbstractFile file) {
        return(file.innerFile.isFile());
    }
}
