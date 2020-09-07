package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;
import java.io.Serializable;

public class RecentDateSorter implements CriterionSorter, Serializable {

    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        String creationTimeA = a.getCreationTime();
        String creationTimeB = b.getCreationTime();

        int result = creationTimeA.compareTo(creationTimeB);
        return (result > 0); // if result is not negative, then the creation time of B is more recent than the creation time of A
    }
}
