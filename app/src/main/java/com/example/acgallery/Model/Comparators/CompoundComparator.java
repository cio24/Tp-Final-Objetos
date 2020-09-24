package com.example.acgallery.Model.Comparators;

import com.example.acgallery.Model.Composite.AbstractFile;
import java.io.Serializable;

public class CompoundComparator implements Comparable, Serializable {
    private Comparable c1;
    private Comparable c2;

    public CompoundComparator(Comparable c1, Comparable c2){
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
