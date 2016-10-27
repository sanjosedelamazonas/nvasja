package org.sanjose.util;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.FilterableSortableGridTreeContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.sanjose.MainUI;
import org.sanjose.converter.BigDecimalConverter;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.helper.PrintHelper;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.model.VsjItem;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.SaldoDelDia;

import javax.print.PrintException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * VASJA class
 * User: prubach
 * Date: 15.09.16
 */
public class ViewUtil {

    public static void printComprobante(VsjItem vcb) {
        try {
            printComprobanteAndThrow(vcb);
        } catch (JRException | PrintException e) {
            if (e instanceof PrintException)
                Notification.show("Problema de impresora de texto", "Problema: " + e.getMessage(), Notification.Type.TRAY_NOTIFICATION);
            else
                Notification.show("Problema de impresora", "Problema: " + e.getMessage(), Notification.Type.TRAY_NOTIFICATION);
            ReportHelper.generateComprobante(vcb);
        }
    }

    private static void printComprobanteAndThrow(VsjItem vcb) throws JRException, PrintException {
        JasperPrint jrPrint = ReportHelper.printComprobante(vcb);
        boolean isPrinted = false;
        PrintHelper ph = ((MainUI)MainUI.getCurrent()).getMainScreen().getPrintHelper();
        isPrinted = ph.print(jrPrint, true);
        if (!isPrinted)
            throw new JRException("Problema al consequir un servicio de imprimir");
    }

    private static void setColumnNames(Grid grid, String[] visible_col_names, String[] visible_col_ids) {

        grid.setColumns(visible_col_ids);
        grid.setColumnOrder(visible_col_ids);

        Map<String, String> colNames = new HashMap<>();
        for (int i=0;i<visible_col_names.length;i++) {
            colNames.put(visible_col_ids[i], visible_col_names[i]);
        }

        for (String colId : colNames.keySet()) {
            grid.getDefaultHeaderRow().getCell(colId).setText(colNames.get(colId));
        }
        grid.setColumnReorderingAllowed(true);

        // Allow column hiding
        for (Grid.Column c : grid.getColumns()) {
            c.setHidable(true);
        }

    }

    public static void setColumnNames(Grid grid, String[] visible_col_names, String[] visible_col_ids,
                                      String[] noneditable_cols) {
        setColumnNames(grid, visible_col_names, visible_col_ids);

        for (String colId : noneditable_cols) {
            grid.getColumn(colId).setEditable(false);
        }

    }

    public static void setDefaultsForNumberField(tm.kod.widgets.numberfield.NumberField numberField) {
        numberField.setConverter(new BigDecimalConverter());
        numberField.setLocale(ConfigurationUtil.getLocale());
        numberField.setDecimalLength(2);
        numberField.setUseGrouping(true);
        numberField.setDecimalSeparator(',');               // e.g. 1,5
        numberField.setNullRepresentation("");
        numberField.setGroupingSeparator('.');              // use '.' as grouping separator
        numberField.setSigned(false);
    }


    public static void alignMontosInGrid(Grid grid) {
        if (grid.getColumn("numHabersol")!=null)
            grid.getColumn("numHabersol").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numHaberdolar")!=null)
            grid.getColumn("numHaberdolar").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numDebedolar")!=null)
            grid.getColumn("numDebedolar").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numDebesol")!=null)
            grid.getColumn("numDebesol").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numHabermo") != null)
            grid.getColumn("numHabermo").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numDebemo") != null)
            grid.getColumn("numDebemo").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.2f", ConfigurationUtil.getLocale()));

        grid.setCellStyleGenerator(( Grid.CellReference cellReference ) -> {
            if ( "numHabersol".equals( cellReference.getPropertyId() ) ||
                    "numHaberdolar".equals( cellReference.getPropertyId() ) ||
                    "numDebedolar".equals( cellReference.getPropertyId() ) ||
                    "numDebemo".equals(cellReference.getPropertyId()) ||
                    "numHabermo".equals(cellReference.getPropertyId()) ||
                    "numDebesol".equals( cellReference.getPropertyId() )) {
                return "v-align-right";
            } else {
                return "v-align-left";
            }
        });
    }


    public static void setupColumnFilters(Grid grid, String[] visible_cols, int[] filter_cols_width, SaldoDelDia saldoDelDia) {
        Map<String, Integer> filCols = new HashMap<>();
        for (int i = 0; i < filter_cols_width.length; i++) {
            filCols.put(visible_cols[i], filter_cols_width[i]);
        }
        setupColumnFilters(grid, filCols, saldoDelDia);
    }

    public static void setupColumnFilters(Grid grid, String[] visible_cols, int[] filter_cols_width) {
        Map<String, Integer> filCols = new HashMap<>();
        for (int i=0;i<filter_cols_width.length;i++) {
            filCols.put(visible_cols[i], filter_cols_width[i]);
        }
        setupColumnFilters(grid, filCols, null);
    }

    private static void setupColumnFilters(Grid grid, Map<String, Integer> filCols, SaldoDelDia saldoDelDia) {
        Grid.HeaderRow filterRow = grid.appendHeaderRow();
        for (Grid.Column column: grid.getColumns()) {
            Object pid = column.getPropertyId();
            Grid.HeaderCell cell = filterRow.getCell(pid);
            // Have an input field to use for filter
            if (column.getConverter() instanceof ZeroOneTrafficLightConverter
                    || column.getConverter() instanceof BooleanTrafficLightConverter) {
                ComboBox filterCombo = new ComboBox();
                filterCombo.setWidth(40, Sizeable.Unit.PIXELS);
                if (column.getConverter() instanceof ZeroOneTrafficLightConverter)
                    DataFilterUtil.bindZeroOneComboBox(filterCombo, pid.toString(), "");
                else
                    DataFilterUtil.bindBooleanComboBox(filterCombo, pid.toString(), "", new String[]{"1", "0"});
                filterCombo.addValueChangeListener(event -> {
                    ((Container.SimpleFilterable) grid.getContainerDataSource()).removeContainerFilters(pid);
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue()))
                        ((Container.Filterable) grid.getContainerDataSource()).addContainerFilter(
                                new Compare.Equal(pid, event.getProperty().getValue()));
                    if (saldoDelDia != null)
                        saldoDelDia.setSaldoDelDia();
                });
                cell.setComponent(filterCombo);
            } else {
                TextField filterField = new TextField();
                // Set filter width according to table
                if (filCols != null && filCols.get(pid) != null)
                    filterField.setColumns(filCols.get(pid));
                else
                    filterField.setColumns(Integer.parseInt(ConfigurationUtil.get("DEFAULT_FILTER_WIDTH")));
                // Update filter When the filter input is changed
                filterField.addTextChangeListener(change -> {
                    // Can't modify filters so need to replace
                    ((Container.SimpleFilterable) grid.getContainerDataSource()).removeContainerFilters(pid);

                    // (Re)create the filter if necessary
                    if (!change.getText().isEmpty()) {
                        ((Container.Filterable) grid.getContainerDataSource()).addContainerFilter(
                                new SimpleStringFilter(pid,
                                        change.getText(), true, false));
                    }
                    if (saldoDelDia != null)
                        saldoDelDia.setSaldoDelDia();
                });
                cell.setComponent(filterField);
            }
        }
    }


    public static void setupDateFilters(BeanItemContainer container, DateField fechaDesde, DateField fechaHasta, Date defDesde, Date defHasta) {
        setupDateFilters(container, "fecFecha", fechaDesde, fechaHasta, defDesde, defHasta, null);
    }

    public static void setupDateFiltersThisMonth(BeanItemContainer container, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        setupDateFilters(container, "fecFecha", fechaDesde, fechaHasta, GenUtil.getBeginningOfMonth(new Date()), GenUtil.getEndOfDay(new Date()), viewing);
    }

    public static void setupDateFiltersPreviousMonth(Container.Filterable container, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        setupDateFilters(container, "fecFecha", fechaDesde, fechaHasta, GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -60)), GenUtil.getEndOfDay(new Date()), viewing);
    }

    public static void setupDateFiltersThisDay(BeanItemContainer container, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        setupDateFilters(container, "fecFecha", fechaDesde, fechaHasta, GenUtil.getBeginningOfDay(new Date()), GenUtil.getEndOfDay(new Date()), viewing);
    }

    private static void setupDateFilters(Container.Filterable container, String propertyId, DateField fechaDesde, DateField fechaHasta, Date defDesde, Date defHasta, GridViewing viewing) {
        // Fecha Desde
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<>(ts);
        fechaDesde.setPropertyDataSource(prop);
        fechaDesde.setConverter(DateToTimestampConverter.INSTANCE);
        fechaDesde.setResolution(Resolution.DAY);
        fechaDesde.setValue(defDesde);
        fechaDesde.addValueChangeListener(valueChangeEvent -> filterComprobantes(container, propertyId, fechaDesde, fechaHasta, viewing));

        ts = new Timestamp(System.currentTimeMillis());
        prop = new ObjectProperty<>(ts);
        fechaHasta.setPropertyDataSource(prop);
        fechaHasta.setConverter(DateToTimestampConverter.INSTANCE);
        fechaHasta.setResolution(Resolution.DAY);

        fechaHasta.setValue(defHasta);
        fechaHasta.addValueChangeListener(valueChangeEvent -> filterComprobantes(container, propertyId, fechaDesde, fechaHasta, viewing));
    }


    public static void filterComprobantes(Container.Filterable container, String propertyId, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        ((Container.SimpleFilterable) container).removeContainerFilters(propertyId);
        Date from, to = null;
        if (fechaDesde.getValue() != null && viewing != null && viewing.getFilterInitialDate().compareTo(fechaDesde.getValue()) > 0) {
            viewing.filter(fechaDesde.getValue(), new Date());
        } else {
            if (fechaDesde.getValue() != null || fechaHasta.getValue() != null) {
                from = (fechaDesde.getValue() != null ? fechaDesde.getValue() : new Date(0));
                to = (fechaHasta.getValue() != null ? fechaHasta.getValue() : new Date(Long.MAX_VALUE));
                container.addContainerFilter(
                        new Between(propertyId,
                                from, to));
            }
        }
    }

    public static boolean isParent(Grid.RowReference rowReference) {
        return ((FilterableSortableGridTreeContainer) rowReference.getGrid().getContainerDataSource()).getHierachical().getParent(rowReference.getItemId()) == null;
    }

    public static void colorizeRows(Grid grid, Class clas) {
        grid.setRowStyleGenerator(rowReference -> {
            if (clas.equals(VsjBancocabecera.class) && ((VsjBancocabecera)rowReference.getItemId()).isEnviado()) {
                return "enviado";
            }
            if (clas.equals(VsjBancocabecera.class) && ((VsjBancocabecera) rowReference.getItemId()).isAnula())
                return "anulado";
            if (clas.equals(VsjBancodetalle.class) && ((VsjBancodetalle) rowReference.getItemId()).isAnula())
                return "anulado";

            if (clas.equals(FilterableSortableGridTreeContainer.class)) {
                if (rowReference.getItem().getItemProperty("flgEnviado").getValue().equals('1')) {
                    return (isParent(rowReference) ? "parentenviado" : "enviado");
                }

                if (rowReference.getItem().getItemProperty("flg_Anula").getValue().equals('1')) {
                    rowReference.getItem().getItemProperty("flgCobrado").setReadOnly(true);
                    return (isParent(rowReference) ? "parentanulado" : "anulado");
                }
                if (isParent(rowReference)) return "parent";
                else rowReference.getItem().getItemProperty("flgCobrado").setReadOnly(true);
            }
            return "";
        });
    }

    public static void colorizeRows(Grid grid) {
        grid.setRowStyleGenerator(rowReference -> {
            if (((VsjCajabanco)rowReference.getItemId()).isEnviado()) {
                return "enviado";
            }
            if (((VsjCajabanco) rowReference.getItemId()).isAnula())
                return "anulado";
            return "";
        });
    }
}
