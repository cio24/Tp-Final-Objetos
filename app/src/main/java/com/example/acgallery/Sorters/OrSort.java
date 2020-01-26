package com.example.acgallery.Sorters;

import com.example.acgallery.Composited.AbstractFile;

public class OrSort implements Criterion {
    private Criterion c1;
    private Criterion c2;

    public OrSort(Criterion c1, Criterion c2){
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        return (c1.lessThan(a,b) || c2.lessThan(a,b));
    }
}
