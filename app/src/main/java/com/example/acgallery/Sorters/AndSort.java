package com.example.acgallery.Sorters;

import com.example.acgallery.Composited.AbstractFile;


public class AndSort implements CriterionSorter {
    private CriterionSorter c1 = new TypeSort(); // always the TypeSort first
    private CriterionSorter c2;

    public AndSort(CriterionSorter c2){
        this.c2 = c2;
    }
    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        if (c1.lessThan(a,b)){ // A is a directory (B could be, too)
            if (c1.lessThan(b,a)){ // B is a directory
                return (c2.lessThan(a,b));
            }
            else{ // B is not a directory
                return true;
            }
        }
        else{ // A is not a directory
            if (c1.lessThan(b,a)){ // B is a directory
                return false;
            }
            else{ // B is not a directory
                return (c2.lessThan(a,b));
            }
        }
    }
}
