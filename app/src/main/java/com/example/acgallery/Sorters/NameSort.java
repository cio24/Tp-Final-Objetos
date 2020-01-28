package com.example.acgallery.Sorters;

import com.example.acgallery.Composited.AbstractFile;

public class NameSort implements CriterionSorter {

    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        return (a.getName().compareToIgnoreCase(b.getName()) < 0); //a is less than b if the return is negative
    }
}
