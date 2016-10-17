package org.sanjose.views.caja;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.model.VsjConfiguractacajabanco;
import org.sanjose.render.BooleanTrafficLight;
import org.sanjose.repo.ScpPlancontableRep;
import org.sanjose.repo.ScpPlanespecialRep;
import org.sanjose.repo.VsjConfiguractacajabancoRep;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.views.sys.VsjView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class ConfiguracionCtaCajaBancoView extends ConfiguracionCtaCajaBancoUI implements VsjView {

    public static final String VIEW_NAME = "Movimientos";
    private static final Logger log = LoggerFactory.getLogger(ConfiguracionCtaCajaBancoView.class);
    public final VsjConfiguractacajabancoRep repo;
    private final ConfiguracionCtaCajaBancoLogic viewLogic = new ConfiguracionCtaCajaBancoLogic(this);
    private ScpPlancontableRep planRepo;
    private ScpPlanespecialRep planEspRepo;

    @Autowired
    public ConfiguracionCtaCajaBancoView(VsjConfiguractacajabancoRep repo, ScpPlancontableRep planRepo, ScpPlanespecialRep planEspRepo) {
        this.repo = repo;
        this.planRepo = planRepo;
        this.planEspRepo = planEspRepo;
        setSizeFull();
    }

    public ConfiguracionCtaCajaBancoView() {
        this(null, null, null);
    }

    @Override
    public void init() {
        @SuppressWarnings("unchecked") BeanItemContainer<VsjConfiguractacajabanco> container = new BeanItemContainer(VsjConfiguractacajabanco.class, repo.findAll());
        gridConfigCtaCajaBanco
        	.setContainerDataSource(container);
        gridConfigCtaCajaBanco.setColumnOrder("activo", "codTipocuenta", "txtTipocuenta", "codCtacontablecaja",
                "codCtacontablegasto", "codCtaespecial", "paraCaja", "paraBanco", "paraProyecto", "paraTercero");


        gridConfigCtaCajaBanco.getDefaultHeaderRow().getCell("codTipocuenta").setText("Codigo");

        gridConfigCtaCajaBanco.getColumn("txtTipocuenta").setWidth(120);
        //gridConfigCtaCajaBanco.setCol

        gridConfigCtaCajaBanco.getColumn("codTipocuenta").setEditable(false);

        gridConfigCtaCajaBanco.setSelectionMode(SelectionMode.MULTI);
        HeaderRow filterRow = gridConfigCtaCajaBanco.appendHeaderRow();

        gridConfigCtaCajaBanco.setEditorFieldGroup(
                new BeanFieldGroup<>(VsjConfiguractacajabanco.class));

        ComboBox selCtacontablecaja = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", planRepo.
                findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        '0','N', GenUtil.getCurYear(), "101"), "Sel cta contable", "txtDescctacontable");
        gridConfigCtaCajaBanco.getColumn("codCtacontablecaja").setEditorField(selCtacontablecaja);

        ComboBox selCtacontablegasto = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontablegasto, "id.codCtacontable",
                planRepo.findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                        '0', 'N', GenUtil.getCurYear(), "101%", "102%", "104%", "106%")
                , "Sel cta contable", "txtDescctacontable");
        gridConfigCtaCajaBanco.getColumn("codCtacontablegasto").setEditorField(selCtacontablegasto);

        ComboBox selCtaespecial = new ComboBox();
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial", planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getCurYear()), "Sel cta especial", "txtDescctaespecial");
        gridConfigCtaCajaBanco.getColumn("codCtaespecial").setEditorField(selCtaespecial);

        gridConfigCtaCajaBanco.getColumn("activo").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraProyecto").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraTercero").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraBanco").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraCaja").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());

        // Grey out inactive rows
        gridConfigCtaCajaBanco.setRowStyleGenerator(rowRef -> {// Java 8
		  if (!(Boolean) rowRef.getItem()
                  .getItemProperty("activo")
                  .getValue())
		      return "grayed";
		  else
		      return null;
		});

        // Set up a filter for all columns
	     for (Object pid: gridConfigCtaCajaBanco.getContainerDataSource()
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

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public void clearSelection() {
        gridConfigCtaCajaBanco.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridConfigCtaCajaBanco.getSelectedRows();
    }

    public void removeRow(VsjConfiguractacajabanco vsj) {
    	repo.delete(vsj);    	
    	gridConfigCtaCajaBanco.getContainerDataSource().removeItem(vsj);
    }
}
