package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;

public interface CriterionSorter {
    public abstract boolean lessThan(AbstractFile a, AbstractFile b);
}
