package com.example.acgallery.Model.Filters;

import com.example.acgallery.Model.Composite.AbstractFile;

public class FolderFilter implements Filterable {

    @Override
    public boolean satisfy(AbstractFile file) {
        return file.getRealFile().isDirectory();
    }
}
