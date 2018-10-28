package org.sanjose.views;

import java.util.Collection;

public interface ItemsRefreshing<T> {

    void refreshItems(Collection<T> items);
}
