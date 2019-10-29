package org.sanjose.views.rendicion;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import tm.kod.widgets.numberfield.NumberField;

public class ImportedDetalleLineView extends ImportedDetalleLine {

    public TextField getNumItem() {
        return numItem;
    }

    public DateField getFecFechaDoc() {
        return fecFechaDoc;
    }

    public ComboBox getSelPartidaP() {
        return selPartidaP;
    }

    public ComboBox getSelDestino() {
        return selDestino;
    }

    public ComboBox getSelRubroInst() {
        return selRubroInst;
    }

    public TextField getTxtGlosaItem() {
        return txtGlosaItem;
    }

    public NumberField getNumMonto() {
        return numMonto;
    }
}
