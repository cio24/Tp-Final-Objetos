package com.example.acgallery.Sorters;

import com.example.acgallery.Composite.AbstractFile;
import java.io.Serializable;

public class RecentDateSorter implements CriterionSorter, Serializable {

    @Override
    public boolean lessThan(AbstractFile a, AbstractFile b) {
        String creationTimeA = a.getCreationTime();
        String creationTimeB = b.getCreationTime();
        String yearA = creationTimeA.substring(0,creationTimeA.indexOf("-"));
        String yearB = creationTimeB.substring(0,creationTimeB.indexOf("-"));
        int yearAValue = Integer.parseInt(yearA);
        int yearBValue = Integer.parseInt(yearB);
        if(yearAValue != yearBValue)
            return yearAValue > yearBValue;
        String monthA = creationTimeA.substring(creationTimeA.indexOf("-") + 1);
        String monthB = creationTimeB.substring(creationTimeB.indexOf("-") + 1);
        String dayA = monthA.substring(monthA.indexOf("-") + 1);
        String dayB = monthB.substring(monthB.indexOf("-") + 1);
        monthA = monthA.substring(0,monthA.indexOf("-"));
        monthB = monthB.substring(0,monthB.indexOf("-"));
        int monthAValue = Integer.parseInt(monthA);
        int monthBValue = Integer.parseInt(monthB);
        if(monthAValue != monthBValue)
            return monthAValue > monthBValue;
        int dayAValue = Integer.parseInt(dayA);
        int dayBValue = Integer.parseInt(dayB);
        if(dayAValue != dayBValue)
            return dayAValue > dayBValue;
        return a.getName().compareToIgnoreCase(b.getName()) < 0;
    }
}
