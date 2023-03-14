package org.sanjose.views.sys;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.model.ScpTipocambioPK;
import org.sanjose.repo.ScpTipocambioRep;
import org.sanjose.util.GenUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class TipoCambioLogic implements Serializable {

    private final TipoCambioView view;

    public static TipoCambioView openTipocambio(Date fecha, ScpTipocambioRep tipocambioRep) {
        Window tipoCambioWindow = new Window();

        tipoCambioWindow.setWindowMode(WindowMode.NORMAL);
        tipoCambioWindow.setDraggable(true);
        tipoCambioWindow.setWidth(720, Sizeable.Unit.PIXELS);
        tipoCambioWindow.setHeight(105, Sizeable.Unit.PIXELS);
        tipoCambioWindow.setModal(true);
        tipoCambioWindow.setResizable(false);
        tipoCambioWindow.setClosable(false);

        TipoCambioView tipoCambioView = new TipoCambioView(tipocambioRep);
        tipoCambioView.viewLogic.loadNewTipocambio(fecha);
        tipoCambioView.setTipoCambioWindow(tipoCambioWindow);
        tipoCambioWindow.setContent(tipoCambioView);
        UI.getCurrent().addWindow(tipoCambioWindow);
        return tipoCambioView;
    }
    
    public TipoCambioLogic(TipoCambioView TipoCambioView) {
        view = TipoCambioView;
    }

    public void init() {
    }

    public void loadNewTipocambio(Date fecha) {
        List<ScpTipocambio> tipocambios = view.getTipocambioRep().findById_FecFechacambio(
                GenUtil.getBeginningOfDay(fecha));
        if (!tipocambios.isEmpty()) {
            ScpTipocambio tipocambio = tipocambios.get(0);
            tipocambio.setFecFactualiza(new Timestamp(new Date().getTime()));
            tipocambio.setCodUactualiza(CurrentUser.get());
            view.bindForm(tipocambio);
        } else {
            ScpTipocambio tipocambio = new ScpTipocambio();
            tipocambio.prepareToSave();
            ScpTipocambioPK tipocambioId = new ScpTipocambioPK();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            tipocambioId.setTxtAnoproceso(sdf.format(fecha));
            tipocambioId.setFecFechacambio(GenUtil.getBeginningOfDay(fecha));
            tipocambio.setId(tipocambioId);
            view.bindForm(tipocambio);
        }
    }

    public ScpTipocambio saveTipoCambio() {
        try {
            ScpTipocambio tipocambio = view.getScpTipocambio();
            if (tipocambio.getNumTccdolar()==null)
                tipocambio.setNumTccdolar(new BigDecimal(0));
            if (tipocambio.getNumTcvdolar()==null)
                tipocambio.setNumTcvdolar(new BigDecimal(0));
            if (tipocambio.getNumTcceuro()==null)
                tipocambio.setNumTcceuro(new BigDecimal(0));
            if (tipocambio.getNumTcveuro()==null)
                tipocambio.setNumTcveuro(new BigDecimal(0));
            view.getTipocambioRep().save(tipocambio);
            return tipocambio;
        } catch (CommitException ce) {
            String errMsg = GenUtil.genErrorMessage(ce.getInvalidFields());
            MessageBox
                    .createError()
                    .withCaption("Problema al guardar tipo cambio")
                    .withMessage("!Error al guardar tipo cambio: " + errMsg)
                    .withOkButton(
                    )
                    .open();
            return null;
        }
    }

    public void enter(String productId) {
    }

}
