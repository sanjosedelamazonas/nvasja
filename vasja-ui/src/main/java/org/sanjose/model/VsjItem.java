package org.sanjose.model;

import com.vaadin.v7.data.fieldgroup.FieldGroup;
import org.sanjose.authentication.CurrentUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * VASJA class
 * User: prubach
 * Date: 23.09.16
 */
@MappedSuperclass
public abstract class VsjItem {

    public VsjItem prepareToSave() throws FieldGroup.CommitException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        this.setCodMes(sdf.format(this.getFecFecha()));
        sdf = new SimpleDateFormat("yyyy");
        this.setTxtAnoproceso(sdf.format(this.getFecFecha()));
        if (this.getCodUregistro() == null) this.setCodUregistro(CurrentUser.get());
        if (this.getFecFregistro() == null) this.setFecFregistro(new Timestamp(System.currentTimeMillis()));
        this.setCodUactualiza(CurrentUser.get());
        this.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
        return this;
    }

    @Column(name="cod_mes")
    private String codMes;

    @Column(name="txt_anoproceso")
    private String txtAnoproceso;

    @Column(name="cod_uactualiza")
    private String codUactualiza;

    @Column(name="cod_uregistro")
    private String codUregistro;

    @Column(name="fec_factualiza")
    private Timestamp fecFactualiza;

    @NotNull
    @Column(name="fec_fecha")
    private Timestamp fecFecha;

    @Column(name="fec_fregistro")
    private Timestamp fecFregistro;

    @Column(name="txt_correlativo")
    private String txtCorrelativo="";

    @Column(name="ind_tipocuenta")
    private Character indTipocuenta;
    
    @NotNull
    @Column(name="cod_tipomoneda")
    private Character codTipomoneda;

    public String getCodMes() {
        return codMes;
    }

    public void setCodMes(String codMes) {
        this.codMes = codMes;
    }

    public String getTxtAnoproceso() {
        return txtAnoproceso;
    }

    public void setTxtAnoproceso(String txtAnoproceso) {
        this.txtAnoproceso = txtAnoproceso;
    }


    public String getCodUactualiza() {
        return codUactualiza;
    }

    public void setCodUactualiza(String codUactualiza) {
        this.codUactualiza = codUactualiza;
    }

    public String getCodUregistro() {
        return codUregistro;
    }

    public void setCodUregistro(String codUregistro) {
        this.codUregistro = codUregistro;
    }

    public Timestamp getFecFactualiza() {
        return fecFactualiza;
    }

    public void setFecFactualiza(Timestamp fecFactualiza) {
        this.fecFactualiza = fecFactualiza;
    }

    public Timestamp getFecFecha() {
        return fecFecha;
    }

    public void setFecFecha(Timestamp fecFecha) {
        this.fecFecha = fecFecha;
    }

    public Timestamp getFecFregistro() {
        return fecFregistro;
    }

    public void setFecFregistro(Timestamp fecFregistro) {
        this.fecFregistro = fecFregistro;
    }

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

    public Character getCodTipomoneda() {
        return codTipomoneda;
    }

    public void setCodTipomoneda(Character codTipomoneda) {
        this.codTipomoneda = codTipomoneda;
    }

    @Override
    public String toString() {
        return "VsjItem{" +
                "codMes='" + codMes + '\'' +
                ", txtAnoproceso='" + txtAnoproceso + '\'' +
                ", codUactualiza='" + codUactualiza + '\'' +
                ", codUregistro='" + codUregistro + '\'' +
                ", fecFactualiza=" + fecFactualiza +
                ", fecFecha=" + fecFecha +
                ", fecFregistro=" + fecFregistro +
                ", txtCorrelativo='" + txtCorrelativo + '\'' +
                ", indTipocuenta=" + indTipocuenta +
                ", codTipomoneda=" + codTipomoneda +
                '}';
    }
}