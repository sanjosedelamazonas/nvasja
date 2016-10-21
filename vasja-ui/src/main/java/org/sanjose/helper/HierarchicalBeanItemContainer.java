package org.sanjose.helper;

/**
 * From: https://dev.vaadin.com/svn/doc/book-examples/branches/vaadin-7/src/com/vaadin/book/examples/datamodel/HierarchicalExample.java
 * <p>
 * "<h1>Implementing the Hierarchical Interface</h1>" +
 * "<p>This example shows how to implement <b>Hierarchical</b> interface for <b>BeanItemContainer</b> (BIC).</p>" +
 * "<p>The implementation depends on the representation of hierarchy in the contained data type. " +
 * "In this implementation, we assume that the hierarchy is represented with a <i>parent</i> property that " +
 * "contains the item ID of the parent item. With BIC this is easy because bean references are used " +
 * "for the bean IDs, so we can use a getParent() method in the bean. The parent-pointing property " +
 * "is parameterized in this implementation.</p>" +
 * "<p>Notice that the getChildren() is awfully inefficient in this implementation. It has O(n) " +
 * "complexity, which results in at least O(n^2) complexity when painting the tree. If the bean " +
 * "type would have references for the children as well, the task would become much lighter.</p>";
 */

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

import java.util.Collection;
import java.util.LinkedList;

/** Extension of BeanItemContainer that implements Hierarchical */
public class HierarchicalBeanItemContainer<T>
        extends BeanItemContainer<T> implements Container.Hierarchical {
    private static final long serialVersionUID = 5475310742299028402L;

    // The contained bean type uses this property to store
    // the parent relationship.
    Object parentPID;

    public HierarchicalBeanItemContainer(Class<T> type,
                                         Object parentPropertyId) {
        super(type);

        this.parentPID = parentPropertyId;
    }

    @Override
    public Collection<?> getChildren(Object itemId) {
        LinkedList<Object> children = new LinkedList<Object>();

        // This implementation has O(n^2) complexity when
        // painting the tree, so it's really inefficient.
        for (Object candidateId : getItemIds()) {
            Object parentRef = getItem(candidateId).
                    getItemProperty(parentPID).getValue();
            if (parentRef == itemId)
                children.add(candidateId);
        }

        if (children.size() > 0)
            return children;
        else
            return null;
    }

    @Override
    public Object getParent(Object itemId) {
        BeanItem bi = getItem(itemId);
        if (bi != null && bi.getItemProperty(parentPID) != null)
            return bi.getItemProperty(parentPID).getValue();
        else {
            System.out.println(parentPID + " bean not found for:  " + itemId);
            return null;
        }
    }

    @Override
    public Collection<?> rootItemIds() {
        LinkedList<Object> result = new LinkedList<Object>();
        for (Object candidateId : getItemIds()) {
            Object parentRef = getItem(candidateId).
                    getItemProperty(parentPID).getValue();
            if (parentRef == null)
                result.add(candidateId);
        }

        if (result.size() > 0)
            return result;
        else
            return null;
    }

    @Override
    public boolean setParent(Object itemId, Object newParentId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Not implemented here");
    }

    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return hasChildren(itemId);
    }

    @Override
    public boolean setChildrenAllowed(Object itemId,
                                      boolean childrenAllowed)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Not implemented here");
    }

    @Override
    public boolean isRoot(Object itemId) {
        return getItem(itemId).getItemProperty(parentPID).
                getValue() == null;
    }

    @Override
    public boolean hasChildren(Object itemId) {
        for (Object candidateId : getItemIds()) {
            Object parentRef = getItem(candidateId).
                    getItemProperty(parentPID).getValue();
            if (parentRef == itemId)
                return true;
        }
        return false;
    }
}