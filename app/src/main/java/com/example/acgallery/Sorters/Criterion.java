package com.example.acgallery.Sorters;

import com.example.acgallery.Composited.AbstractFile;

public interface Criterion {
    public abstract boolean lessThan(AbstractFile a, AbstractFile b);
}
