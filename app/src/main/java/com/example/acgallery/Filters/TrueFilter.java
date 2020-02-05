package com.example.acgallery.Filters;

import com.example.acgallery.Composited.AbstractFile;

public class TrueFilter implements CriterionFilter {
    @Override
    public boolean satisfy(AbstractFile file) {
        return true;
    }
}
