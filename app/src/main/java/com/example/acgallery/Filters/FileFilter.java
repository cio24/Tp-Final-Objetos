package com.example.acgallery.Filters;

import com.example.acgallery.Composite.AbstractFile;

public class FileFilter implements CriterionFilter {

    @Override
    public boolean satisfy(AbstractFile abstractFile) {
        return(abstractFile.getRealFile().isFile());
    }
}
