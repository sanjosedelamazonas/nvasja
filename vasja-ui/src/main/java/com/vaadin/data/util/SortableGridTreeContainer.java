package com.vaadin.data.util;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import org.vaadin.gridtree.GridTreeContainer;

import java.util.*;

/**
 * Created by pol on 23.10.16.
 */
public abstract class SortableGridTreeContainer extends GridTreeContainer implements Container.Sortable {


    private final HierarchicalContainer hierachical;
    private final Set<Object> expandedItems;//all items are collapsed by default
    ItemSorter itemSorter = new DefaultItemSorter();
    private List<Object> visibleItems;

    public SortableGridTreeContainer(HierarchicalContainer hierachical) {
        super(hierachical);
        this.hierachical = hierachical;
        visibleItems = new ArrayList<Object>();
        expandedItems = new HashSet<Object>();
        init();
    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {
        sortContainer(propertyId, ascending);
    }

    protected void sortContainer(Object[] propertyId, boolean[] ascending) {
        if (!(this instanceof Sortable)) {
            throw new UnsupportedOperationException(
                    "Cannot sort a Container that does not implement Sortable");
        }

        // Set up the item sorter for the sort operation
        getItemSorter().setSortProperties((Sortable) this, propertyId,
                ascending);

        // Perform the actual sort
        List<Object> itemsToExpand = new ArrayList<>();
        for (Object itemId : getItemIds()) {
            if (isItemExpanded(itemId)) itemsToExpand.add(itemId);
        }
        collapseAll();
        Collections.sort(visibleItems, getItemSorter());
        for (Object itemId : itemsToExpand) {
            expand(itemId);
        }
        // post sort updates
        if (isFiltered()) {
            filterAll();
        } else {
            fireItemSetChange();
        }
    }

    protected abstract boolean isFiltered();

    protected abstract void filterAll();

    public void collapseAll() {
        for (Object itemId : new ArrayList<>(getNotFilteredItems())) {
            final List<Object> changedItems = new ArrayList<Object>();
            if (isItemExpanded(itemId)) collapseSelfAndChildren(itemId, false, changedItems);
            //collapse(itemId);
        }
        fireItemSetChange();

    }

    public void expandAll() {
        for (Object itemId : getNotFilteredItems()) {
            if (!isItemExpanded(itemId)) expand(itemId);
        }
    }


    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        LinkedList<Object> sortables = new LinkedList<Object>();
        for (Object propertyId : getContainerPropertyIds()) {
            Class<?> propertyType = getType(propertyId);
            if (Comparable.class.isAssignableFrom(propertyType)
                    || propertyType.isPrimitive()) {
                sortables.add(propertyId);
            }
        }
        return sortables;
    }

    public ItemSorter getItemSorter() {
        return itemSorter;
    }

    public void setItemSorter(ItemSorter itemSorter) {
        this.itemSorter = itemSorter;
    }


    // This should be package visibility - for now public, not to loose time for solving visibility problems.

    /**
     * Toogle the expand/collapse for item
     *
     * @param itemId
     * @return list of changed item ids
     */
    public List<Object> toogleCollapse(Object itemId) {
        List<Object> changedItems = new ArrayList<Object>();
        if (hierachical.hasChildren(itemId)) {
            if (isItemExpanded(itemId)) {
                changedItems = collapse(itemId);
            } else {
                expand(itemId);
                changedItems.add(itemId);
            }
        }
        return changedItems;
    }

    public boolean hasChildren(Object itemId) {
        return hierachical.hasChildren(itemId);
    }

    /**
     * Returns level of the item starting from 0.
     *
     * @param itemId
     * @return level of the item starting from 0.
     */
    public int getLevel(Object itemId) {
        return getLevel(itemId, 0);
    }

    private int getLevel(Object itemId, int levelIter) {
        final Object parent = hierachical.getParent(itemId);
        if (parent == null) {
            return levelIter;
        } else {
            return getLevel(parent, ++levelIter);
        }
    }

    /**
     * @param itemId
     * @return true if item is expanded, false otherwise. Returns false also
     * if item doesn't have children.
     */
    public boolean isItemExpanded(Object itemId) {
        return expandedItems.contains(itemId);
    }

    //Below this line internal stuff :)
    //***************************************************************************
    private void expand(Object itemId) {
        final List<Object> tmpItems = new ArrayList<Object>();
        for (final Object it : visibleItems) {
            tmpItems.add(it);
            // expand item
            if (it.equals(itemId)) {
                expandedItems.add(it);
                if (hierachical.getChildren(itemId) != null) {
                    for (final Object child : hierachical.getChildren(itemId)) {
                        tmpItems.add(child);
                    }
                }
            }
        }
        visibleItems = tmpItems;
        fireItemSetChange();
    }

    private void collapseSelfAndChildren(Object itemId, boolean removeSelf, List<Object> changedItems) {
        if (removeSelf) {
            visibleItems.remove(itemId);
        }
        if (expandedItems.remove(itemId)) {
            changedItems.add(itemId);
        }
        if (hierachical.hasChildren(itemId)) {
            for (final Object child : hierachical.getChildren(itemId)) {
                collapseSelfAndChildren(child, true, changedItems);
            }
            ;
        }

    }

    private List<Object> collapse(Object itemId) {
        final List<Object> changedItems = new ArrayList<Object>();
        collapseSelfAndChildren(itemId, false, changedItems);
        fireItemSetChange();
        return changedItems;
    }

    private void init() {
        //store only items of the 0 level (those which don't have parents)
        for (final Object it : hierachical.getItemIds()) {
            if (hierachical.getParent(it) == null) {
                visibleItems.add(it);
            }
        }
        ;
    }


    @Override
    public Object nextItemId(Object itemId) {
        final int index = getNotFilteredItems().indexOf(itemId);
        if (getNotFilteredItems().size() <= index) {
            return null;
        }
        return getNotFilteredItems().get(index + 1);
    }

    @Override
    public Object prevItemId(Object itemId) {
        final int index = getNotFilteredItems().indexOf(itemId);
        if (index <= 0) {
            return null;
        }
        return getNotFilteredItems().get(index - 1);
    }

    @Override
    public Object firstItemId() {
        if (getNotFilteredItems().size() > 0) {
            return getNotFilteredItems().get(0);
        } else {
            return null;
        }
    }

    @Override
    public Object lastItemId() {
        if (getNotFilteredItems().size() > 0) {
            return getNotFilteredItems().get(getNotFilteredItems().size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public Item getItem(Object itemId) {
        return hierachical.getItem(itemId);
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return hierachical.getContainerPropertyIds();
    }

    protected abstract List<Object> getNotFilteredItems();

    @Override
    public Collection<?> getItemIds() {
        return getNotFilteredItems();
        //return hierachical.getItemIds();
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        return hierachical.getContainerProperty(itemId, propertyId);
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return hierachical.getType(propertyId);
    }

    @Override
    public int size() {
        return getNotFilteredItems().size();
    }

    @Override
    public boolean containsId(Object itemId) {
        if (itemId == null) {
            return false;
        } else {
            return getNotFilteredItems().contains(itemId);
        }
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
                                        Object defaultValue) throws UnsupportedOperationException {
        return hierachical.addContainerProperty(propertyId, type, defaultValue);
    }

    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        return hierachical.removeContainerProperty(propertyId);
    }

    @Override
    public int indexOfId(Object itemId) {
        return getNotFilteredItems().indexOf(itemId);
    }

    @Override
    public Object getIdByIndex(int index) {
        if ((index >= 0) && (index < getNotFilteredItems().size())) {
            return getNotFilteredItems().get(index);
        } else {
            return null;
        }
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException(
                    "Start index cannot be negative! startIndex=" + startIndex);
        }

        if (startIndex > getNotFilteredItems().size()) {
            throw new IndexOutOfBoundsException(
                    "Start index exceeds container size! startIndex="
                            + startIndex + " containerLastItemIndex="
                            + (getNotFilteredItems().size() - 1));
        }

        if (numberOfItems < 1) {
            if (numberOfItems == 0) {
                return Collections.emptyList();
            }

            throw new IllegalArgumentException(
                    "Cannot get negative amount of items! numberOfItems="
                            + numberOfItems);
        }

        int endIndex = startIndex + numberOfItems;

        if (endIndex > getNotFilteredItems().size()) {
            endIndex = getNotFilteredItems().size();
        }

        return Collections.unmodifiableList(getNotFilteredItems().subList(
                startIndex, endIndex));

    }

    public HierarchicalContainer getHierachical() {
        return hierachical;
    }

    public List<Object> getVisibleItems() {
        return visibleItems;
    }
}
