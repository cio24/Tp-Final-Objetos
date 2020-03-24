package com.example.acgallery.Filters;

import com.example.acgallery.Composite.AbstractFile;

public class FolderFilter implements CriterionFilter {
    @Override
    public boolean satisfy(AbstractFile file) {
        return file.getInnerFile().isDirectory();
    }
}
