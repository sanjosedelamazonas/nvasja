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

    public ScpRendiciondetallePK(ScpRendicioncabecera cabecera) {
        if (cabecera!=null) {
            setTxtAnoproceso(cabecera.getTxtAnoproceso());
            setCodFilial(cabecera.getCodFilial());
            setCodMes(cabecera.getCodMes());
            setCodComprobante(cabecera.getCodComprobante());
            setCodOrigen(cabecera.getCodOrigen());
        }
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
        return Objects.equals(numNroitem, that.numNroitem) && Objects.equals(codFilial, that.codFilial) && Objects.equals(codOrigen, that.codOrigen) && Objects.equals(codComprobante, that.codComprobante) && Objects.equals(codMes, that.codMes) && Objects.equals(txtAnoproceso, that.txtAnoproceso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numNroitem, codFilial, codOrigen, codComprobante, codMes, txtAnoproceso);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "ScpRendiciondetallePK{" +
                "numNroitem=" + numNroitem +
                ", codFilial='" + codFilial + '\'' +
                ", codOrigen='" + codOrigen + '\'' +
                ", codComprobante='" + codComprobante + '\'' +
                ", codMes='" + codMes + '\'' +
                ", txtAnoproceso='" + txtAnoproceso + '\'' +
                '}';
    }
}