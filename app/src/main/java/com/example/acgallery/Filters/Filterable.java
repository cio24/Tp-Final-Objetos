/*
 * This interface is thought as a way to create filters that let you filters files by a certain criteria
 * the method satisfy returns true when the
 */

package com.example.acgallery.Filters;

import com.example.acgallery.Composite.AbstractFile;

public interface Filterable {
    boolean satisfy(AbstractFile file);
}
