package com.example.acgallery.Model.Comparators;

import com.example.acgallery.Model.Composite.AbstractFile;

public interface Comparable {
    boolean lessThan(AbstractFile a, AbstractFile b);
}
