package com.example.acgallery.Sorters;

import com.example.acgallery.Composited.AbstractFile;

public class DateSort implements CriterionSorter {

    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        String creationTimeA = a.getCreationTime();
        String creationTimeB = b.getCreationTime();

        int result = creationTimeA.compareTo(creationTimeB);
        return (result < 0); // if result is negative, then the creation time of A is less than B
    }
}
