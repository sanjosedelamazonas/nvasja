package org.sanjose.helper;

import com.vaadin.data.Container;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.data.util.ItemSorter;
import org.vaadin.gridtree.GridTreeContainer;

import java.util.Collection;

/**
 * Created by pol on 23.10.16.
 */
public class SortableGridTreeContainer extends GridTreeContainer implements Container.Sortable {

    ItemSorter itemSorter = new DefaultItemSorter();

    public SortableGridTreeContainer(Hierarchical hierachical) {
        super(hierachical);
    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {


    }

    protected void sortContainer(Object[] propertyId, boolean[] ascending) {
        if (!(this instanceof Sortable)) {
            throw new UnsupportedOperationException(
                    "Cannot sort a Container that does not implement Sortable");
        }

/*
        // Set up the item sorter for the sort operation
        getItemSorter().setSortProperties((Sortable) this, propertyId,
                ascending);

        // Perform the actual sort
        doSort();
*/
    }

    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        return null;
    }
}
