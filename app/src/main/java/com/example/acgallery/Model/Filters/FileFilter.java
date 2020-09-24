package com.example.acgallery.Model.Filters;

import com.example.acgallery.Model.Composite.AbstractFile;

public class FileFilter implements Filterable {

    @Override
    public boolean satisfy(AbstractFile abstractFile) {
        return(abstractFile.getRealFile().isFile());
    }
}
