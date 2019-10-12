package org.sanjose.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * The primary key class for the scp_comprobantedetalle database table.
 */
@Embeddable
public class ScpRendiciondetallePK implements Serializable, Cloneable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 14357658632554L;

    @Column(name="cod_rendicioncabecera", insertable=false, updatable=false)
    private int codRendicioncabecera;

    @Column(name = "num_nroitem")
    private Long numNroitem;

    @Column(name = "cod_filial")
    private String codFilial = "01";
    @Column(name = "cod_origen")
    private String codOrigen;
    @Column(name = "cod_comprobante")
    private String codComprobante;

    @Column(name="cod_mes")
    private String codMes;

    @Column(name="txt_anoproceso")
    private String txtAnoproceso;

    public ScpRendiciondetallePK() {
    }

    public ScpRendiciondetallePK prepareToSave(ScpRendiciondetalle det) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        setCodMes(sdf.format(det.getFecComprobante()));
        sdf = new SimpleDateFormat("yyyy");
        setTxtAnoproceso(sdf.format(det.getFecComprobante()));
        return this;
    }

    public Long getNumNroitem() {
        return this.numNroitem;
    }

    public void setNumNroitem(long numNroitem) {
        this.numNroitem = numNroitem;
    }

    public int getCodRendicioncabecera() {
        return codRendicioncabecera;
    }

    public void setCodRendicioncabecera(int codRendicioncabecera) {
        this.codRendicioncabecera = codRendicioncabecera;
    }

    public String getCodFilial() {
        return codFilial;
    }

    public void setCodFilial(String codFilial) {
        this.codFilial = codFilial;
    }

    public String getCodOrigen() {
        return codOrigen;
    }

    public void setCodOrigen(String codOrigen) {
        this.codOrigen = codOrigen;
    }

    public String getCodComprobante() {
        return codComprobante;
    }

    public void setCodComprobante(String codComprobante) {
        this.codComprobante = codComprobante;
    }


    public void setNumNroitem(Long numNroitem) {
        this.numNroitem = numNroitem;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScpRendiciondetallePK that = (ScpRendiciondetallePK) o;
        return codRendicioncabecera == that.codRendicioncabecera &&
                numNroitem.equals(that.numNroitem);
    }

    public int hashCode() {
        final int prime = 31;
        long hash = 17;
        hash = hash * prime + this.numNroitem;
        hash = hash * prime + this.codRendicioncabecera;
        return Long.valueOf(hash).intValue();
    }

    @Override
    public String toString() {
        return "ScpRendiciondetallePK{" +
                "codRendicioncabecera=" + codRendicioncabecera +
                ", numNroitem=" + numNroitem +
                ", codFilial='" + codFilial + '\'' +
                ", codOrigen='" + codOrigen + '\'' +
                ", codComprobante='" + codComprobante + '\'' +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}