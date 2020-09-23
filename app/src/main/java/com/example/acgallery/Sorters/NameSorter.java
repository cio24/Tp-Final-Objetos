package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;
import java.io.Serializable;

public class NameSorter implements Sorteable, Serializable {

    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b){
        for(int i = 0; i < Math.min(a.getName().length(),b.getName().length()); i++){
            if(a.getName().toLowerCase().charAt(i) != b.getName().toLowerCase().charAt(i))
                return a.getName().toLowerCase().charAt(i) < b.getName().toLowerCase().charAt(i);
        }
        return false;
    }
}