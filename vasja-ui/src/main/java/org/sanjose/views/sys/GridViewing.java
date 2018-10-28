package org.sanjose.views.sys;

import java.util.Collection;
import java.util.Date;

/**
 * Created by pol on 27.10.16.
 */
public interface GridViewing {

    void filter(Date fechaDesde, Date fechaHasta);

    Date getFilterInitialDate();

    void setFilterInitialDate(Date fecha);
}
