package org.sanjose.model;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.authentication.CurrentUser;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * VASJA class
 * User: prubach
 * Date: 23.09.16
 */
@MappedSuperclass
public abstract class VsjCajaBancoItem extends VsjItem {

    public VsjItem prepareToSave() throws FieldGroup.CommitException {
        super.prepareToSave();
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        this.setCodMes(sdf.format(this.getFecFecha()));
        sdf = new SimpleDateFormat("yyyy");
        this.setTxtAnoproceso(sdf.format(this.getFecFecha()));
        return this;
    }

    @Column(name="txt_correlativo")
    private String txtCorrelativo="";

    @Column(name="ind_tipocuenta")
    private Character indTipocuenta;

    @NotNull
    @Column(name="fec_fecha")
    private Timestamp fecFecha;

    public String getTxtCorrelativo() {
        return txtCorrelativo;
    }

    public void setTxtCorrelativo(String txtCorrelativo) {
        this.txtCorrelativo = txtCorrelativo;
    }

    public Character getIndTipocuenta() {
        return indTipocuenta;
    }

    public void setIndTipocuenta(Character indTipocuenta) {
        this.indTipocuenta = indTipocuenta;
    }

    public Timestamp getFecFecha() {
        return fecFecha;
    }

    public void setFecFecha(Timestamp fecFecha) {
        this.fecFecha = fecFecha;
    }

    @Override
    public String toString() {
        return "VsjCajaBancoItem{" +
                "txtCorrelativo='" + txtCorrelativo + '\'' +
                ", indTipocuenta=" + indTipocuenta +
                ", fecFecha=" + fecFecha +
                '}';
    }
}