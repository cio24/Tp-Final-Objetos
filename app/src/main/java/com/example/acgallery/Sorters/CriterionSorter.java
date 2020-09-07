package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;

public interface CriterionSorter {
    boolean lessThan(AbstractFile a, AbstractFile b);
}
