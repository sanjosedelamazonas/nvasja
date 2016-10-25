package org.sanjose.views.caja;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import org.sanjose.model.VsjConfiguracioncaja;
import org.sanjose.repo.*;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.VsjView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class ConfiguracionCajaView extends ConfiguracionCajaUI implements VsjView {

    public static final String VIEW_NAME = "Cajas";
    private static final Logger log = LoggerFactory.getLogger(ConfiguracionCajaView.class);
    public final VsjConfiguracioncajaRep repo;
    private final ConfiguracionCajaLogic viewLogic = new ConfiguracionCajaLogic(this);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "codConfiguracion", "txtConfiguracion", "indTipomoneda",
            "codCtacontable", "codDestino", "codProyecto"
    };
    private final int[] FILTER_WIDTH = new int[]{
            6, 12, 2,
            6, 6, 6
    };
    private ScpPlancontableRep planRepo;
    private ScpDestinoRep destinoRepo;
    private ScpProyectoRep proyectoRepo;
    private ScpCategoriaproyectoRep categoriaproyectoRepo;

    @Autowired
    public ConfiguracionCajaView(VsjConfiguracioncajaRep repo, ScpPlancontableRep planRepo,
                                 ScpDestinoRep destinoRepo, ScpProyectoRep proyectoRepo, ScpCategoriaproyectoRep categoriaproyectoRepo) {
        this.repo = repo;
        this.planRepo = planRepo;
        this.destinoRepo = destinoRepo;
        this.proyectoRepo = proyectoRepo;
        this.categoriaproyectoRepo = categoriaproyectoRepo;
        setSizeFull();
    }

    @Override
    public void init() {
        @SuppressWarnings("unchecked") BeanItemContainer<VsjConfiguracioncaja> container = new BeanItemContainer(VsjConfiguracioncaja.class, repo.findAll());
        gridConfigCaja
        	.setContainerDataSource(container);
        gridConfigCaja.setColumnOrder(VISIBLE_COLUMN_IDS);
        
        gridConfigCaja.getDefaultHeaderRow().getCell("codConfiguracion").setText("Codigo");
        gridConfigCaja.getColumn("txtConfiguracion").setWidth(200);
        gridConfigCaja.getColumn("codConfiguracion").setEditable(false);
               
        gridConfigCaja.setSelectionMode(SelectionMode.MULTI);
        HeaderRow filterRow = gridConfigCaja.appendHeaderRow();
        
        gridConfigCaja.setEditorFieldGroup(
                new BeanFieldGroup<>(VsjConfiguracioncaja.class));

        ComboBox selCategoriaproy = new ComboBox();
        DataFilterUtil.bindComboBox(selCategoriaproy, "codCategoriaproyecto", categoriaproyectoRepo.findAll(), "Sel Cat Proyecto", "txtDescripcion");
        gridConfigCaja.getColumn("codCategoriaproyecto").setEditorField(selCategoriaproy);

        ComboBox selCtacontable = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontable, "id.codCtacontable", planRepo.findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                '0', 'N', GenUtil.getCurYear(), "101"), "Sel cta contable", "txtDescctacontable");
        gridConfigCaja.getColumn("codCtacontable").setEditorField(selCtacontable);
        

        ComboBox selDestino = new ComboBox();
        DataFilterUtil.bindComboBox(selDestino, "codDestino", destinoRepo.findByIndTipodestino('3'), "Sel Tercero", "txtNombredestino");
        gridConfigCaja.getColumn("codDestino").setEditorField(selDestino);

        ComboBox selProyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", proyectoRepo.findByFecFinalGreaterThan(new Date()), "Sel Proyecto", "txtDescproyecto");
        //selProyecto.addValidator(new TwoCombosValidator(selTercero, true, null));
        gridConfigCaja.getColumn("codProyecto").setEditorField(selProyecto);

        // Tipo Moneda
        ComboBox selTipomoneda = new ComboBox();
        DataFilterUtil.bindTipoMonedaComboBox(selTipomoneda, "indTipomoneda", "Moneda");
        gridConfigCaja.getColumn("indTipomoneda").setEditorField(selTipomoneda);

        ViewUtil.setupColumnFilters(gridConfigCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        viewLogic.init();
    }


    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public void clearSelection() {
        gridConfigCaja.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridConfigCaja.getSelectedRows();
    }

    public void removeRow(VsjConfiguracioncaja vsj) {
    	repo.delete(vsj);    	
    	gridConfigCaja.getContainerDataSource().removeItem(vsj);
    }
}
