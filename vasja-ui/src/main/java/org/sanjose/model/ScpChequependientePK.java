package org.sanjose.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * The primary key class for the ScpChequependientePK database table.
 */
@Embeddable
public class ScpChequependientePK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 10936432494763243L;

    @Column(name = "txt_anoproceso")
    private String txtAnoproceso;

    @Column(name = "cod_filial")
    private String codFilial;

    @Column(name = "cod_ctacontable")
    private String codCtacontable;

    @Column(name = "txt_cheque")
    private String txtCheque;

    @Column(name = "cod_origen")
    private String codOrigen;

    @Column(name = "cod_comprobante")
    private String codComprobante;

    @Column(name = "num_nroitem")
    private long numNroitem;

    public ScpChequependientePK() {
    }

    public String getTxtAnoproceso() {
        return this.txtAnoproceso;
    }

    public void setTxtAnoproceso(String txtAnoproceso) {
        this.txtAnoproceso = txtAnoproceso;
    }

    public String getCodFilial() {
        return this.codFilial;
    }

    public void setCodFilial(String codFilial) {
        this.codFilial = codFilial;
    }

    public String getCodOrigen() {
        return this.codOrigen;
    }

    public void setCodOrigen(String codOrigen) {
        this.codOrigen = codOrigen;
    }

    public String getCodComprobante() {
        return this.codComprobante;
    }

    public void setCodComprobante(String codComprobante) {
        this.codComprobante = codComprobante;
    }

    public long getNumNroitem() {
        return this.numNroitem;
    }

    public void setNumNroitem(long numNroitem) {
        this.numNroitem = numNroitem;
    }

    public String getCodCtacontable() {
        return codCtacontable;
    }

    public void setCodCtacontable(String codCtacontable) {
        this.codCtacontable = codCtacontable;
    }

    public String getTxtCheque() {
        return txtCheque;
    }

    public void setTxtCheque(String txtCheque) {
        this.txtCheque = txtCheque;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScpChequependientePK that = (ScpChequependientePK) o;
        return numNroitem == that.numNroitem &&
                Objects.equals(txtAnoproceso, that.txtAnoproceso) &&
                Objects.equals(codFilial, that.codFilial) &&
                Objects.equals(codCtacontable, that.codCtacontable) &&
                Objects.equals(txtCheque, that.txtCheque) &&
                Objects.equals(codOrigen, that.codOrigen) &&
                Objects.equals(codComprobante, that.codComprobante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(txtAnoproceso, codFilial, codCtacontable, txtCheque, codOrigen, codComprobante, numNroitem);
    }
}