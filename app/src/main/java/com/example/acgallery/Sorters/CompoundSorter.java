package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;
import java.io.Serializable;

public class CompoundSorter implements Sorteable, Serializable {
    private Sorteable c1;
    private Sorteable c2;

    public CompoundSorter(Sorteable c1, Sorteable c2){
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
