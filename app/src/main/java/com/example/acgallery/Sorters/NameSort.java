package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;
import java.io.Serializable;

public class NameSort implements CriterionSorter, Serializable {

    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        int result = a.getName().compareToIgnoreCase(b.getName());
        return (result < 0); //a is less than b if the result is negative
    }
}
