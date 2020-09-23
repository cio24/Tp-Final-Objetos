package com.example.acgallery.Filters;

import com.example.acgallery.Composite.AbstractFile;

public class FolderFilter implements Filterable {

    @Override
    public boolean satisfy(AbstractFile file) {
        return file.getRealFile().isDirectory();
    }
}
