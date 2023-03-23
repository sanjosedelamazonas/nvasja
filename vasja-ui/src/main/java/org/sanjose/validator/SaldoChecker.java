package org.sanjose.validator;

import com.vaadin.ui.TextField;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.views.sys.ComprobanteWarnGuardar;

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

    public boolean check() {
        String strVal = egresoField.getValue();
        if (GenUtil.strNullOrEmpty(strVal) || GenUtil.objNullOrEmpty(saldoField.getValue())) return false;
        BigDecimal newVal = null;
        BigDecimal caja = null;
        BigDecimal proyecto = null;
        try {
            NumberFormat nf = NumberFormat.getInstance(ConfigurationUtil.getLocale());
            newVal = new BigDecimal(nf.parse(strVal).toString());
        } catch (ParseException e) {
        }
        try {
            NumberFormat nf = NumberFormat.getInstance(ConfigurationUtil.getLocale());
            caja = new BigDecimal(nf.parse(saldoField.getValue()).toString());
            proyecto = new BigDecimal(nf.parse(proyectoField.getValue()).toString());
        } catch (ParseException e) {
        }
        boolean isWarn = false;
        if (newVal != null && newVal.compareTo(new BigDecimal(0))>0 && proyecto != null && newVal.compareTo(proyecto) > 0) {
            //Notification.show("El monto de egreso esta mas grande que el saldo disponible", Notification.Type.WARNING_MESSAGE);
            proyectoField.addStyleName("yield");
            isWarn = true;
        } else {
            proyectoField.removeStyleName("yield");
        }
        if (newVal != null && newVal.compareTo(new BigDecimal(0))>0 && (caja != null && newVal.compareTo(caja) > 0)) {
            saldoField.addStyleName("yield");
            isWarn = true;
        } else {
            saldoField.removeStyleName("yield");
        }
        if (isWarn) {
            egresoField.addStyleName("warning");
        } else {
            egresoField.removeStyleName("warning");
        }
        return isWarn;
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
