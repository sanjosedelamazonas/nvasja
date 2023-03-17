package org.sanjose.util;

import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.FilterableSortableGridTreeContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.sanjose.MainUI;
import org.sanjose.converter.*;
import org.sanjose.helper.PrintHelper;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.*;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.views.caja.*;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.SaldoDelDia;
import org.sanjose.views.sys.SubWindowing;
import org.sanjose.views.sys.PersistanceService;

import javax.print.PrintException;
import java.sql.Timestamp;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VASJA class
 * User: prubach
 * Date: 15.09.16
 */
public class ViewUtil {

    public static void printComprobante(VsjCajaBancoItem vcb) {
        if (ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT")) {
            ViewUtil.doPrintComprobante(vcb);
        } else if (ConfigurationUtil.is("REPORTS_COMPROBANTE_OPEN")) {
            ReportHelper.generateComprobante(vcb);
        }
    }

    public static void doPrintComprobante(VsjCajaBancoItem vcb) {
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

    public static boolean isPrinterReady() {
        return ((MainUI)MainUI.getCurrent()).getMainScreen()!=null
                && ((MainUI)MainUI.getCurrent()).getMainScreen().getPrintHelper()!=null
                && ((MainUI)MainUI.getCurrent()).getMainScreen().getPrintHelper().isReady();
    }

    private static void printComprobanteAndThrow(VsjCajaBancoItem vcb) throws JRException, PrintException {
        JasperPrint jrPrint = ReportHelper.printComprobante(vcb);
        boolean isPrinted = false;
        PrintHelper ph = ((MainUI)MainUI.getCurrent()).getMainScreen().getPrintHelper();
        isPrinted = ph.print(jrPrint, vcb instanceof ScpCajabanco);
        if (!isPrinted)
            throw new JRException("Problema al consequir un servicio de imprimir");
        else
            Notification.show("Impression Correcta", "El comprobante numero: " + vcb.getTxtCorrelativo() +
                    " ha sido enviado a la impresora correctamente", Notification.Type.TRAY_NOTIFICATION);
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
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(ConfigurationUtil.getLocale());
        numberField.setDecimalLength(2);
        numberField.setUseGrouping(true);
        numberField.setDecimalSeparator(dfs.getDecimalSeparator());               // e.g. 1,5
        //numberField.setDecimalSeparator(',');               // e.g. 1,5
        numberField.setNullRepresentation("");
        numberField.setGroupingSeparator(dfs.getGroupingSeparator());              // use '.' as grouping separator
        //numberField.setGroupingSeparator('.');              // use '.' as grouping separator
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
        if (grid.getColumn("numTcvdolar") != null)
            grid.getColumn("numTcvdolar").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.4f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numTcmo") != null)
            grid.getColumn("numTcmo").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.4f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numGastototal") != null)
            grid.getColumn("numGastototal").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numAnticipo") != null)
            grid.getColumn("numAnticipo").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numSaldosol") != null)
            grid.getColumn("numSaldosol").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numSaldodolar") != null)
            grid.getColumn("numSaldodolar").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.2f", ConfigurationUtil.getLocale()));
        if (grid.getColumn("numSaldomo") != null)
            grid.getColumn("numSaldomo").setRenderer(new EmptyZeroNumberRendrer(
                    "%02.2f", ConfigurationUtil.getLocale()));

        grid.setCellStyleGenerator(( Grid.CellReference cellReference ) -> {
            if ( "numHabersol".equals( cellReference.getPropertyId() ) ||
                    "numHaberdolar".equals( cellReference.getPropertyId() ) ||
                    "numDebedolar".equals( cellReference.getPropertyId() ) ||
                    "numDebemo".equals(cellReference.getPropertyId()) ||
                    "numHabermo".equals(cellReference.getPropertyId()) ||
                    "numTcvdolar".equals(cellReference.getPropertyId()) ||
                    "numTcmo".equals(cellReference.getPropertyId()) ||
                    "numGastototal".equals(cellReference.getPropertyId()) ||
                    "numAnticipo".equals(cellReference.getPropertyId()) ||
                    "numSaldosol".equals(cellReference.getPropertyId()) ||
                    "numSaldodolar".equals(cellReference.getPropertyId()) ||
                    "numSaldomo".equals(cellReference.getPropertyId()) ||
                    "numDebesol".equals( cellReference.getPropertyId() )) {
                return "v-align-right";
            } else {
                return "v-align-left";
            }
        });
    }


    public static void setupColumnFilters(Grid grid, String[] visible_cols, int[] filter_cols_width) {
        setupColumnFilters(grid, visible_cols, filter_cols_width, null, null);
    }

    public static void setupColumnFilters(Grid grid, String[] visible_cols, int[] filter_cols_width, SaldoDelDia saldoDelDia) {
        Map<String, Integer> filCols = new HashMap<>();
        for (int i = 0; i < filter_cols_width.length; i++) {
            filCols.put(visible_cols[i], filter_cols_width[i]);
        }
        setupColumnFilters(grid, filCols, saldoDelDia);
    }

    public static void setupColumnFilters(Grid grid, String[] visible_cols, int[] filter_cols_width, SaldoDelDia saldoDelDia, PersistanceService service) {
        Map<String, Integer> filCols = new HashMap<>();
        for (int i = 0; i < filter_cols_width.length; i++) {
            filCols.put(visible_cols[i], filter_cols_width[i]);
        }
        setupColumnFilters(grid, filCols, saldoDelDia, service);
    }


    private static void setupColumnFilters(Grid grid, Map<String, Integer> filCols, SaldoDelDia saldoDelDia) {
        setupColumnFilters(grid, filCols, saldoDelDia, null);
    }

    public static void setupColumnFilters(Grid grid, Map<String, Integer> filCols, PersistanceService service) {
        setupColumnFilters(grid, filCols, null, service);
    }


    private static void setupColumnFilters(Grid grid, Map<String, Integer> filCols, SaldoDelDia saldoDelDia, PersistanceService service) {
        Map<Object, Container.Filter> columnFilters = new HashMap<>();
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

                    // Regular container
                    if (grid.getContainerDataSource() instanceof Container.SimpleFilterable) {
                        ((Container.SimpleFilterable) grid.getContainerDataSource()).removeContainerFilters(pid);
                    } else {
                        // Generated property container
                        Container.Filter f = columnFilters.get(pid);
                        if (f!=null)
                            ((Container.Filterable) grid.getContainerDataSource()).removeContainerFilter(f);
                    }
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                        columnFilters.put(pid, new Compare.Equal(pid, event.getProperty().getValue()));
                        ((Container.Filterable) grid.getContainerDataSource()).addContainerFilter(
                                columnFilters.get(pid));
                    }
                    if (saldoDelDia != null) {
                        saldoDelDia.setSaldoDelDia();
                        saldoDelDia.calcFooterSums();
                    }
                });
                cell.setComponent(filterCombo);
            } else if (column.getConverter() instanceof TipoDocumentoConverter ||
                    column.getConverter() instanceof CargoCuartaConverter ||
                    column.getConverter() instanceof TipoDestinoConverter) {

                ComboBox filterCombo = new ComboBox();
                if (column.getConverter() instanceof TipoDocumentoConverter) {

                    DataFilterUtil.bindComboBox(filterCombo, "codTipodocumento", service.getTipodocumentoRepo().findAll(),
                            "Sel Tipo documento", "txtDescripcion");
                } else if (column.getConverter() instanceof TipoDestinoConverter) {
                    DataFilterUtil.bindTipoDestinoComboBox(filterCombo, "indTipodestino", "Sel Clasificacion");
                } else {
                    DataFilterUtil.bindComboBox(filterCombo, "codCargo", service.getCargocuartaRepo().findAll(), "Sel Cargo 4ta",
                            "txtDescripcion");
                }
                filterCombo.addValueChangeListener(event -> {

                    // Regular container
                    if (grid.getContainerDataSource() instanceof Container.SimpleFilterable) {
                        ((Container.SimpleFilterable) grid.getContainerDataSource()).removeContainerFilters(pid);
                    } else {
                        // Generated property container
                        Container.Filter f = columnFilters.get(pid);
                        if (f!=null)
                            ((Container.Filterable) grid.getContainerDataSource()).removeContainerFilter(f);
                    }
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                        columnFilters.put(pid, new Compare.Equal(pid, event.getProperty().getValue()));
                        ((Container.Filterable) grid.getContainerDataSource()).addContainerFilter(
                                columnFilters.get(pid));
                    }
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
                    if (grid.getContainerDataSource() instanceof Container.SimpleFilterable) {
                        ((Container.SimpleFilterable) grid.getContainerDataSource()).removeContainerFilters(pid);
                    } else {
                        // Generated property container
                        Container.Filter f = columnFilters.get(pid);
                        if (f!=null)
                            ((Container.Filterable) grid.getContainerDataSource()).removeContainerFilter(f);
                    }
                    // (Re)create the filter if necessary
                    if (!change.getText().isEmpty()) {
                        columnFilters.put(pid, new SimpleStringFilter(pid,
                                change.getText(), true, false));
                        ((Container.Filterable) grid.getContainerDataSource()).addContainerFilter(columnFilters.get(pid));
                    }
                    if (saldoDelDia != null) {
                        saldoDelDia.setSaldoDelDia();
                        saldoDelDia.calcFooterSums();
                    }
                });
                cell.setComponent(filterField);
            }
        }
    }


    public static void setupDateFilters(BeanItemContainer container, DateField fechaDesde, DateField fechaHasta, Date defDesde, Date defHasta) {
        setupDateFilters(container, "fecFecha", fechaDesde, fechaHasta, defDesde, defHasta, null);
    }

    public static void setupDateFiltersRendicionesThisMonth(BeanItemContainer container, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        setupDateFilters(container, "fecComprobante", fechaDesde, fechaHasta, GenUtil.getBeginningOfMonth(new Date()), GenUtil.getEndOfDay(new Date()), viewing);
    }

    public static void setupDateFiltersThisMonth(BeanItemContainer container, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        setupDateFilters(container, "fecFecha", fechaDesde, fechaHasta, GenUtil.getBeginningOfMonth(new Date()), GenUtil.getEndOfDay(new Date()), viewing);
    }

    public static void setupDateFiltersPreviousMonth(Container.Filterable container, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        setupDateFilters(container, "fecFecha", fechaDesde, fechaHasta, GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -60)), GenUtil.getEndOfDay(new Date()), viewing);
    }

    public static void setupDateFiltersRendicionesPreviousMonth(Container.Filterable container, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        setupDateFilters(container, "fecComprobante", fechaDesde, fechaHasta, GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -60)), GenUtil.getEndOfDay(new Date()), viewing);
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
        fechaDesde.setInvalidAllowed(false);

        ts = new Timestamp(System.currentTimeMillis());
        prop = new ObjectProperty<>(ts);
        fechaHasta.setPropertyDataSource(prop);
        fechaHasta.setConverter(DateToTimestampConverter.INSTANCE);
        fechaHasta.setResolution(Resolution.DAY);
        fechaHasta.setInvalidAllowed(false);

        fechaHasta.setValue(defHasta);
        fechaHasta.addValueChangeListener(valueChangeEvent -> filterComprobantes(container, propertyId, fechaDesde, fechaHasta, viewing));
    }

    public static void filterComprobantes(Container.Filterable container, String propertyId, DateField fechaDesde, DateField fechaHasta, GridViewing viewing) {
        ((Container.SimpleFilterable) container).removeContainerFilters(propertyId);
        Date from, to = null;
        from = (fechaDesde.getValue() != null ? fechaDesde.getValue() : new Date(0));
        to = (fechaHasta.getValue() != null ? fechaHasta.getValue() : new Date(Long.MAX_VALUE));
        if (fechaDesde.getValue() != null && viewing != null && viewing.getFilterInitialDate().compareTo(fechaDesde.getValue()) > 0) {
            viewing.filter(fechaDesde.getValue(), new Date());
        }
        container.addContainerFilter(
                new Between(propertyId,
                        from, to));
    }

    public static boolean isParent(Grid.RowReference rowReference) {
        return ((FilterableSortableGridTreeContainer) rowReference.getGrid().getContainerDataSource()).getHierachical().getParent(rowReference.getItemId()) == null;
    }

    public static void colorizeRows(Grid grid, Class clas) {
        grid.setRowStyleGenerator(rowReference -> {
            if (clas.equals(ScpBancocabecera.class) && ((ScpBancocabecera)rowReference.getItemId()).isEnviado()) {
                return "enviado";
            }
            if (clas.equals(ScpBancocabecera.class) && ((ScpBancocabecera) rowReference.getItemId()).isAnula())
                return "anulado";
            if (clas.equals(ScpBancodetalle.class) && ((ScpBancodetalle) rowReference.getItemId()).isAnula())
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

    public static void colorizeRowsRendiciones(Grid grid) {
        grid.setRowStyleGenerator(rowReference -> {
            if (((ScpRendicioncabecera)rowReference.getItemId()).isEnviado()) {
                return "enviado";
            }
            return "";
        });
    }


    public static void colorizeRows(Grid grid) {
        grid.setRowStyleGenerator(rowReference -> {
            if (((ScpCajabanco)rowReference.getItemId()).isEnviado()) {
                return "enviado";
            }
            if (((ScpCajabanco) rowReference.getItemId()).isAnula())
                return "anulado";
            return "";
        });
    }

    private static void allColumnsHide(Grid grid, boolean isHide) {
        for (int i=0;i<3;i++) {
            String mon = GenUtil.getDescMoneda(Character.forDigit(i, 10));
            for (String col : new String[] { "numDebe", "numHaber", "numSaldo"}) {
                if (grid.getColumn(col + mon)!=null) grid.getColumn(col + mon).setHidden(isHide);
            }
        }
    }

    public static void filterColumnsByMoneda(Grid grid, Character moneda) {
        if (moneda=='A') {
            allColumnsHide(grid, false);
            if (grid.getColumn("numTcvdolar")!=null) grid.getColumn("numTcvdolar").setHidden(false);
            if (grid.getColumn("numTcmo")!=null) grid.getColumn("numTcmo").setHidden(false);
            if (grid.getColumn("codTipomoneda")!=null) grid.getColumn("codTipomoneda").setHidden(false);
            return;
        }
        allColumnsHide(grid, true);
        for (String col : new String[] { "numDebe", "numHaber", "numSaldo"}) {
            if (grid.getColumn(col + GenUtil.getDescMoneda(moneda))!=null)
                grid.getColumn(col + GenUtil.getDescMoneda(moneda)).setHidden(false);
        }
        if (grid.getColumn("numTcvdolar")!=null) grid.getColumn("numTcvdolar").setHidden(true);
        if (grid.getColumn("numTcmo")!=null) grid.getColumn("numTcmo").setHidden(true);
        if (grid.getColumn("codTipomoneda")!=null) grid.getColumn("codTipomoneda").setHidden(true);

    }

    private static void allColumnsEnable(Grid grid, boolean isEnabled) {
        for (int i=0;i<3;i++) {
            String mon = GenUtil.getDescMoneda(Character.forDigit(i, 10));
            for (String col : new String[] { "numDebe", "numHaber"}) {
                grid.getColumn(col + mon).setEditable(isEnabled);
            }
        }
    }

    public static void filterColumnsDisableByMoneda(Grid grid, Character moneda) {
        if (moneda=='A') {
            allColumnsEnable(grid, true);
            return;
        }
        allColumnsEnable(grid, false);
        for (String col : new String[] { "numDebe", "numHaber"}) {
            grid.getColumn(col + GenUtil.getDescMoneda(moneda)).setEditable(true);
        }
    }

    public static void setFieldsNullRepresentation(FieldGroup fieldGroup) {
        for (Field f : fieldGroup.getFields()) {
            if (f instanceof TextField)
                ((TextField) f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox) f).setPageLength(20);
        }
    }

    public static void clearFields(FieldGroup fieldGroup) {
        if (fieldGroup != null) {
            new ArrayList<>(fieldGroup.getFields()).stream().forEach(f -> {
                f.removeAllValidators();
                fieldGroup.unbind(f);
                f.setValue(null);
            });
        }
    }

    public static void openInNewWindow(ComprobanteViewing component) {
        Window subWindow = new Window();
        subWindow.setWindowMode(WindowMode.NORMAL);
        subWindow.setDraggable(true);
        int width = component instanceof TransferenciaView ? 1280 : 990;
        int height = component instanceof TransferenciaView ? 600 : 500;
        String caption = component instanceof TransferenciaView ? "Cargo/Abono" : "Comprobante";
        subWindow.setWidth(width, Sizeable.Unit.PIXELS);
        subWindow.setHeight(height, Sizeable.Unit.PIXELS);
        subWindow.setModal(true);
        subWindow.setContent((Component)component);
        if (!ConfigurationUtil.is("DEV_MODE"))
            subWindow.setClosable(false);
        subWindow.setDraggable(true);
        subWindow.setCaption(caption);
        component.setSubWindow(subWindow);
        // Don't show navigation buttons if opened in subwindow Nuevo Comprobante
        if (component instanceof ComprobanteView) {
            component.getNuevoComprobante().setVisible(false);
            component.getModificarBtn().setVisible(false);
            component.getCerrarBtn().setVisible(false);
        } else {
            ((TransferenciaView)component).getNuevaTransBtn().setVisible(false);
        }
        UI.getCurrent().addWindow(subWindow);
    }

    public static void openCajaSaldosInNewWindow(CajaSaldoView component, Date fromDate, Date toDate) {
        if (fromDate==null || toDate==null) {
            MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
            MessageBox
                    .createQuestion()
                    .withMessage("Por favor rellena las fechas del inicio y final")
                    .open();
            return;
        }
        Window subWindow = new Window();
        subWindow.setWindowMode(WindowMode.NORMAL);
        int width = 1280;
        int height = 400;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        String caption = "Saldos de Caja: <b>" + sdf.format(fromDate) + "</b> - <b>" + sdf.format(toDate) +"</b>";
        subWindow.setWidth(width, Sizeable.Unit.PIXELS);
        subWindow.setHeight(height, Sizeable.Unit.PIXELS);
        subWindow.setModal(true);
        subWindow.setContent((Component)component);
        subWindow.setClosable(false);
        subWindow.setDraggable(true);
        subWindow.setCaptionAsHtml(true);
        subWindow.setCaption("<center>"+caption+"</center>");
        component.setSubWindow(subWindow);
        UI.getCurrent().addWindow(subWindow);
    }


    public static void openViewInNewWindowBanco(SubWindowing component) {
        openViewInNewWindow(component, 1150, 640);
    }

    public static void openViewInNewWindow(SubWindowing component) {
        openViewInNewWindow(component, 0, 0);
    }

    public static void openViewInNewWindow(SubWindowing component, int width, int height) {
        Window subWindow = new Window();
        subWindow.setWindowMode(WindowMode.NORMAL);
        if (width==0)
            subWindow.setWidth(100, Sizeable.Unit.PERCENTAGE);
        else
            subWindow.setWidth(width, Sizeable.Unit.PIXELS);
        if (height==0)
            subWindow.setHeight(100, Sizeable.Unit.PERCENTAGE);
        else
            subWindow.setHeight(height, Sizeable.Unit.PIXELS);
        subWindow.setModal(true);
        subWindow.setContent((Component)component);
        if (!ConfigurationUtil.is("DEV_MODE"))
            subWindow.setClosable(false);
        subWindow.setDraggable(true);
        component.setSubWindow(subWindow);
        //subWindow.setCaption(caption);
        //component.setSubWindow(subWindow);
        UI.getCurrent().addWindow(subWindow);
    }

}
