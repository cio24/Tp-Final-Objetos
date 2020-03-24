package com.example.acgallery.Filters;

import com.example.acgallery.Composite.AbstractFile;

public interface CriterionFilter {
    public abstract boolean satisfy(AbstractFile file);
}
