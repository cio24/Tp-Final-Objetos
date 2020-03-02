package com.example.acgallery.Sorters;

import com.example.acgallery.Composited.AbstractFile;

public class NameSort implements CriterionSorter {

    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        int result = a.getName().compareToIgnoreCase(b.getName());
        return (result < 0); //a is less than b if the result is negative
    }
}
