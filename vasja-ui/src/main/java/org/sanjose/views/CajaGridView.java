package org.sanjose.views;

import java.sql.Timestamp;
import java.util.Collection;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.PopupDateField;
import org.sanjose.helper.BooleanTrafficLight;
import org.sanjose.helper.DataFilterUtil;
import org.sanjose.helper.DateToTimestampConverter;
import org.sanjose.helper.GenUtil;
import org.sanjose.model.ScpPlancontableRep;
import org.sanjose.model.ScpPlanespecialRep;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.model.VsjCajabancoRep;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.model.VsjCajabancoRep;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.HtmlRenderer;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class CajaGridView extends CajaGridUI implements View {

	private static final Logger log = LoggerFactory.getLogger(CajaGridView.class);
	
    public static final String VIEW_NAME = "Operaciones de Caja";

    private CajaGridLogic viewLogic = new CajaGridLogic(this);
    
    public VsjCajabancoRep repo;
    
    @Autowired
    public CajaGridView(VsjCajabancoRep repo, VsjCajabancoRep configRepo, ScpPlancontableRep planRepo, ScpPlanespecialRep planEspRepo) {
    	this.repo = repo;
        setSizeFull();
        addStyleName("crud-view");

        BeanItemContainer<VsjCajabanco> container = new BeanItemContainer(VsjCajabanco.class, repo.findAll());
        
        gridCaja
        	.setContainerDataSource(container);
///        gridCaja.setColumnOrder("activo", "codTipocuenta", "txtTipocuenta", "codCtacontablecaja",
   //             "codCtacontablegasto", "codCtaespecial", "paraCaja", "paraBanco", "paraProyecto", "paraTercero");
        
        
      //  gridCaja.getDefaultHeaderRow().getCell("codTipocuenta").setText("Codigo");
        
     //   gridCaja.getColumn("txtTipocuenta").setWidth(120);
        //gridCaja.setCol
        
        //gridCaja.getColumn("codTipocuenta").setEditable(false);
               
        gridCaja.setSelectionMode(SelectionMode.MULTI);
        HeaderRow filterRow = gridCaja.appendHeaderRow();
        
        gridCaja.setEditorFieldGroup(
        	    new BeanFieldGroup<VsjCajabanco>(VsjCajabanco.class));



        PopupDateField pdf = new PopupDateField();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        gridCaja.getColumn("fecFecha").setEditorField(pdf);

       /* ComboBox selCtacontablecaja = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), "101"), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codCtacontablecaja").setEditorField(selCtacontablecaja);
        
        ComboBox selCtacontablegasto = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtacontablegasto, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codCtacontablegasto").setEditorField(selCtacontablegasto);
        
        ComboBox selCtaespecial = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial", planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()), "Sel cta especial", "txtDescctaespecial");
        gridCaja.getColumn("codCtaespecial").setEditorField(selCtaespecial);
         */
        // Set up a filter for all columns
	     for (Object pid: gridCaja.getContainerDataSource()
	                          .getContainerPropertyIds()) {
	         HeaderCell cell = filterRow.getCell(pid);
	
	         // Have an input field to use for filter
	         TextField filterField = new TextField();
	         if (pid.toString().contains("para") || pid.toString().contains("activo"))
	        	 filterField.setColumns(2);
	         else
	        	 filterField.setColumns(6);
	
	         // Update filter When the filter input is changed
	         filterField.addTextChangeListener(change -> {
	             // Can't modify filters so need to replace
	        	 container.removeContainerFilters(pid);
	
	             // (Re)create the filter if necessary
	             if (! change.getText().isEmpty())
	                 container.addContainerFilter(
	                     new SimpleStringFilter(pid,
	                         change.getText(), true, false));
	         });
	         cell.setComponent(filterField);
	     }
        viewLogic.init();
    }
    
    public CajaGridView() {
        this(null, null, null, null);
    }


    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void clearSelection() {
        gridCaja.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridCaja.getSelectedRows();
    }

    public void removeRow(VsjCajabanco vsj) {
    	repo.delete(vsj);    	
    	gridCaja.getContainerDataSource().removeItem(vsj);
    }
}
