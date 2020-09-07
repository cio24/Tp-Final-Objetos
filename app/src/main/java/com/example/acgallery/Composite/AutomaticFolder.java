package com.example.acgallery.Composite;

import com.example.acgallery.Filters.CriterionFilter;

import java.io.File;
import java.util.ArrayList;

public class AutomaticFolder extends AbstractFile {
    private Folder folderRoot;
    private CriterionFilter criterionFilter;

    public AutomaticFolder() {
        super();
        this.folderRoot = Folder.getFolderRoot();
    }

    @Override
    public boolean rename(String newName) {
        return false;
    }

    @Override
    public boolean copyTo(Folder destination) {
        return false;
    }

    @Override
    public boolean moveTo(Folder destination) {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public int getDeepItemsNumber(CriterionFilter criterionFilter) {
        return 0;
    }

    @Override
    public ArrayList<AbstractFile> getDeepFilteredFiles(CriterionFilter criterionFilter) {
        return null;
    }
}
