package org.sanjose.views.dict;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Window;
import org.sanjose.converter.BigDecimalConverter;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.repo.ScpTipocambioRep;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.springframework.beans.factory.annotation.Autowired;
import tm.kod.widgets.numberfield.NumberField;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class TipoCambioView extends TipoCambioUI implements View {

    public static final String VIEW_NAME = "TipoCambio";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(TipoCambioView.class);
    public final TipoCambioLogic viewLogic = new TipoCambioLogic(this);
    public final ScpTipocambioRep tipocambioRep;
    public ScpTipocambio item;
    private BeanItem<ScpTipocambio> beanItem;
    private FieldGroup fieldGroup;

    private Window tipoCambioWindow;

    boolean isLoading = false;

    boolean isEdit = false;

    @Autowired
    public TipoCambioView(ScpTipocambioRep tipocambioRep) {
        this.tipocambioRep = tipocambioRep;
        setSizeFull();

        // Validators
        getNumTccdolar().addValidator(new LocalizedBeanValidator(ScpTipocambio.class, "numTccdolar"));
    }

    public void close() {
        if (tipoCambioWindow!=null)
            tipoCambioWindow.close();
    }


    public void bindForm(ScpTipocambio item) {
        isLoading = true;

        isEdit = !GenUtil.objNullOrEmpty(item.getId());
        beanItem = new BeanItem<>(item);
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        fecFecha.setValue(beanItem.getBean().getId().getFecFechacambio());
        fieldGroup.bind(numTccdolar, "numTccdolar");
        fieldGroup.bind(numTcvdolar, "numTcvdolar");
        fieldGroup.bind(numTccmo, "numTcceuro");
        fieldGroup.bind(numTcvmo, "numTcveuro");
        fieldGroup.getFields().stream().filter(f -> f instanceof NumberField).forEach(f -> {
            ViewUtil.setDefaultsForNumberField((NumberField) f);
            ((NumberField) f).setConverter(new BigDecimalConverter(4));
            ((NumberField) f).setDecimalLength(4);
        });
        isLoading = false;
        isEdit = false;
    }

    public void anularTipoCambio() {
        if (fieldGroup!=null) fieldGroup.discard();
    }

    public ScpTipocambio getScpTipocambio() throws FieldGroup.CommitException {
        fieldGroup.commit();
        return beanItem.getBean();
    }


    public NumberField getNumTccdolar() {
        return numTccdolar;
    }

    public NumberField getNumTcvdolar() {
        return numTcvdolar;
    }

    public NumberField getNumTccmo() {
        return numTccmo;
    }

    public NumberField getNumTcvmo() {
        return numTcvmo;
    }

    public Button getBtnGuardar() {
        return btnGuardar;
    }

    public Button getBtnIgnorar() {
        return btnIgnorar;
    }

    public Button getBtnAnular() {
        return btnAnular;
    }

    public DateField getFecFecha() {
        return fecFecha;
    }

    public ScpTipocambioRep getTipocambioRep() {
        return tipocambioRep;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }


    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    public Window getTipoCambioWindow() {
        return tipoCambioWindow;
    }

    public void setTipoCambioWindow(Window tipoCambioWindow) {
        this.tipoCambioWindow = tipoCambioWindow;
    }
}
