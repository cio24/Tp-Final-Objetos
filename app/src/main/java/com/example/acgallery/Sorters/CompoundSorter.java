package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;
import java.io.Serializable;

public class CompoundSorter implements CriterionSorter, Serializable {
    private CriterionSorter c1;
    private CriterionSorter c2;

    public CompoundSorter(CriterionSorter c1, CriterionSorter c2){
        this.c1 = c1;
        this.c2 = c2;
    }
    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        boolean result = c1.lessThan(a,b);
        if(result == c1.lessThan(b,a))
            result = c2.lessThan(a,b);
        return result;
    }
}
