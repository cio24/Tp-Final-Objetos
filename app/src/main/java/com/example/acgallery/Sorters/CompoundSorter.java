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
        if(c1.lessThan(a,b) && c1.lessThan(a,b))
            return c2.lessThan(a,b);
        return c1.lessThan(a,b);

    }
}
