package com.example.acgallery.Sorters;

import com.example.acgallery.Composited.AbstractFile;

public class AndSort implements CriterionSorter {
    private CriterionSorter c1;
    private CriterionSorter c2;

    public AndSort(CriterionSorter c1, CriterionSorter c2){
        this.c1 = c1;
        this.c2 = c2;
    }
    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        return (c1.lessThan(a,b) && c2.lessThan(a,b));
    }
}
