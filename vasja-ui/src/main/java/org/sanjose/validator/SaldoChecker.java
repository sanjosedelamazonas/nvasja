package org.sanjose.validator;

import com.vaadin.ui.TextField;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by pol on 20.10.16.
 */
public class SaldoChecker {

    private final TextField egresoField;
    private TextField saldoField;
    private TextField proyectoField;

    public SaldoChecker(TextField egresoField, TextField saldoField, TextField proyectoField) {
        this.egresoField = egresoField;
        this.saldoField = saldoField;
        this.proyectoField = proyectoField;
    }

    public void check() {
        String strVal = egresoField.getValue();
        if (GenUtil.strNullOrEmpty(strVal) || GenUtil.objNullOrEmpty(saldoField.getValue())) return;
        BigDecimal newVal = null;
        BigDecimal caja = null;
        BigDecimal proyecto = null;
        try {
            NumberFormat nf = NumberFormat.getInstance(ConfigurationUtil.getLocale());
            newVal = new BigDecimal(nf.parse(strVal).toString());
        } catch (ParseException e) {
        }
        try {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            caja = new BigDecimal(nf.parse(saldoField.getValue()).toString());
            proyecto = new BigDecimal(nf.parse(proyectoField.getValue()).toString());
        } catch (ParseException e) {
        }
        if (newVal != null && ((proyecto != null && newVal.compareTo(proyecto) > 0) || (caja != null && newVal.compareTo(caja) > 0))) {
            //Notification.show("El monto de egreso esta mas grande que el saldo disponible", Notification.Type.WARNING_MESSAGE);
            egresoField.addStyleName("warning");
        } else {
            egresoField.removeStyleName("warning");
        }
    }

    public TextField getSaldoField() {
        return saldoField;
    }

    public void setSaldoField(TextField saldoField) {
        this.saldoField = saldoField;
        check();
    }

    public TextField getProyectoField() {
        return proyectoField;
    }

    public void setProyectoField(TextField proyectoField) {
        this.proyectoField = proyectoField;
        check();
    }
}