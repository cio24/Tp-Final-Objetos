package com.example.acgallery.Filters;

import com.example.acgallery.Composite.AbstractFile;

public interface CriterionFilter {
    boolean satisfy(AbstractFile file);
}
