package com.vaadin.data.util;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import org.vaadin.gridtree.client.CellWrapper;

import java.util.*;

/**
 * Created by pol on 23.10.16.
 */
public class FilterableSortableGridTreeContainer extends SortableGridTreeContainer implements Container.Filterable, Container.SimpleFilterable {


    private Set<Filter> filters = new HashSet<Filter>();

    /**
     * An ordered {@link List} of item identifiers in the container after
     * filtering, excluding those that have been filtered out.
     * <p>
     * This is what the external API of the {@link Container} interface and its
     * subinterfaces shows (e.g. {@link #size()}, {@link #nextItemId(Object)}).
     * <p>
     * If null, the full item id list is used instead.
     */
    private List<Object> filteredItemIds;

    public FilterableSortableGridTreeContainer(HierarchicalContainer hierachical) {
        super(hierachical);
    }

    /**
     * Filter the view to recreate the visible item list from the unfiltered
     * items, and send a notification if the set of visible items changed in any
     * way.
     */
    @Override
    protected void filterAll() {
        if (doFilterContainer(!getFilters().isEmpty())) {
            fireItemSetChange();
        }
    }

    protected void addFilter(Filter filter) throws UnsupportedFilterException {
        getFilters().add(filter);
        filterAll();
    }

    @Override
    public void addContainerFilter(Filter filter) throws UnsupportedFilterException {
        addFilter(filter);
    }

    public void addContainerFilter(Object propertyId, String filterString,
                                   boolean ignoreCase, boolean onlyMatchPrefix) {
        try {
            addFilter(new SimpleStringFilter(propertyId, filterString,
                    ignoreCase, onlyMatchPrefix));
        } catch (UnsupportedFilterException e) {
            // the filter instance created here is always valid for in-memory
            // containers
        }
    }

    @Override
    public void removeContainerFilter(Filter filter) {
        removeFilter(filter);
    }

    /**
     * Remove this container as a listener for the given property.
     *
     * @param item       The {@link Item} that contains the property
     * @param propertyId The id of the property
     */
    private void removeValueChangeListener(Item item, Object propertyId) {
        Property<?> property = item.getItemProperty(propertyId);
        if (property instanceof Property.ValueChangeNotifier) {
            //((Property.ValueChangeNotifier) property).removeValueChangeListener(getHierachical());
        }
    }

    @Override
    public void removeAllContainerFilters() {
        if (!getFilters().isEmpty()) {
            /*for (Item item : visibleItems.values()) {
                //removeAllValueChangeListeners(item);
            }*/
            removeAllFilters();
        }
    }

    @Override
    public void removeContainerFilters(Object propertyId) {
        Collection<Filter> removedFilters = removeFilters(propertyId);
        if (!removedFilters.isEmpty()) {
            // stop listening to change events for the property
/*
            for (Item item : itemIdToItem.values()) {
                removeValueChangeListener(item, propertyId);
            }
*/
        }
    }

    /**
     * Remove all container filters for a given property identifier and
     * re-filter the view. This also removes filters applying to multiple
     * properties including the one identified by propertyId.
     *
     * @param propertyId
     * @return Collection<Filter> removed filters
     */
    protected Collection<Filter> removeFilters(Object propertyId) {
        if (getFilters().isEmpty() || propertyId == null) {
            return Collections.emptyList();
        }
        List<Filter> removedFilters = new LinkedList<Filter>();
        for (Iterator<Filter> iterator = getFilters().iterator(); iterator
                .hasNext(); ) {
            Filter f = iterator.next();
            if (f.appliesToProperty(propertyId)) {
                removedFilters.add(f);
                iterator.remove();
            }
        }
        if (!removedFilters.isEmpty()) {
            filterAll();
            return removedFilters;
        }
        return Collections.emptyList();
    }


    @Override
    public Collection<Filter> getContainerFilters() {
        return Collections.unmodifiableCollection(filters);
    }

    public Set<Filter> getFilters() {
        return filters;
    }

    /**
     * Remove a specific container filter and re-filter the view (if necessary).
     * <p>
     * This can be used to implement
     * {@link Filterable#removeContainerFilter(com.vaadin.data.Container.Filter)}
     * .
     */
    protected void removeFilter(Filter filter) {
        for (Iterator<Filter> iterator = getFilters().iterator(); iterator
                .hasNext(); ) {
            Filter f = iterator.next();
            if (f.equals(filter)) {
                iterator.remove();
                filterAll();
                return;
            }
        }
    }

    /**
     * Remove all container filters for all properties and re-filter the view.
     * <p>
     * This can be used to implement
     * {@link Filterable#removeAllContainerFilters()}.
     */
    protected void removeAllFilters() {
        if (getFilters().isEmpty()) {
            return;
        }
        getFilters().clear();
        filterAll();
    }

    /**
     * Filters the data in the container and updates internal data structures.
     * This method should reset any internal data structures and then repopulate
     * them so {@link #getItemIds()} and other methods only return the filtered
     * items.
     *
     * @param hasFilters true if filters has been set for the container, false
     *                   otherwise
     * @return true if the item set has changed as a result of the filtering
     */
    protected boolean doFilterContainer(boolean hasFilters) {
        if (!hasFilters) {
            boolean changed = getVisibleItems().size() != getNotFilteredItems()
                    .size();
            setFilteredItemIds(null);
            return changed;
        }

        // Reset filtered list
        List<Object> originalFilteredItemIds = getFilteredItemIds();
        boolean wasUnfiltered = false;
        if (originalFilteredItemIds == null) {
            originalFilteredItemIds = Collections.emptyList();
            wasUnfiltered = true;
        }
        setFilteredItemIds(new ListSet<Object>());

        // Filter
        boolean equal = true;
        Iterator origIt = originalFilteredItemIds.iterator();
        for (final Iterator i = getVisibleItems().iterator(); i
                .hasNext(); ) {
            final Object id = i.next();
            if (passesFilters(id)) {
                // filtered list comes from the full list, can use ==
                equal = equal && origIt.hasNext() && origIt.next() == id;
                getFilteredItemIds().add(id);
            }
        }

        return (wasUnfiltered && !getVisibleItems().isEmpty()) || !equal
                || origIt.hasNext();
    }

    protected Item getUnfilteredItem(Object itemId) {
        return getHierachical().getUnfilteredItem(itemId);
    }

    /**
     * Checks if the given itemId passes the filters set for the container. The
     * caller should make sure the itemId exists in the container. For
     * non-existing itemIds the behavior is undefined.
     *
     * @param itemId An itemId that exists in the container.
     * @return true if the itemId passes all filters or no filters are set,
     * false otherwise.
     */
    protected boolean passesFilters(Object itemId) {
        Item item = getUnfilteredItem(itemId);
        if (getFilters().isEmpty()) {
            return true;
        }
        final Iterator<Filter> i = getFilters().iterator();
        while (i.hasNext()) {
            final Filter f = i.next();
            if ((f instanceof SimpleStringFilter) &&
                    (item.getItemProperty(((SimpleStringFilter) f).getPropertyId()).getValue() instanceof CellWrapper))
                return (((CellWrapper) item.getItemProperty(((SimpleStringFilter) f).getPropertyId()).getValue()).getValue().contains(((SimpleStringFilter) f).getFilterString()));

            if (!f.passesFilter(itemId, item)) {
                return false;
            }
        }
        return true;
    }

    public List<Object> getFilteredItemIds() {
        return filteredItemIds;
    }

    protected void setFilteredItemIds(List<Object> filteredItemIds) {
        this.filteredItemIds = filteredItemIds;
    }

    @Override
    protected List<Object> getNotFilteredItems() {
        if (isFiltered()) {
            return getFilteredItemIds();
        } else {
            return getVisibleItems();
        }
    }

    /**
     * Returns true is the container has active filters.
     *
     * @return true if the container is currently filtered
     */
    protected boolean isFiltered() {
        return filteredItemIds != null;
    }
}
