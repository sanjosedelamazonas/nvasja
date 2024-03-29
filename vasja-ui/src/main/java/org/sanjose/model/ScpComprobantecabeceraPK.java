package org.sanjose.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The primary key class for the scp_comprobantedetalle database table.
 */
@Embeddable
public class ScpComprobantecabeceraPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "txt_anoproceso")
    private String txtAnoproceso;

    @Column(name = "cod_filial")
    private String codFilial;

    @Column(name = "cod_mes")
    private String codMes;

    @Column(name = "cod_origen")
    private String codOrigen;

    @Column(name = "cod_comprobante")
    private String codComprobante;

    public ScpComprobantecabeceraPK() {
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

    public String getCodMes() {
        return this.codMes;
    }

    public void setCodMes(String codMes) {
        this.codMes = codMes;
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

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ScpComprobantecabeceraPK)) {
            return false;
        }
        ScpComprobantecabeceraPK castOther = (ScpComprobantecabeceraPK) other;
        return
                this.txtAnoproceso.equals(castOther.txtAnoproceso)
                        && this.codFilial.equals(castOther.codFilial)
                        && this.codMes.equals(castOther.codMes)
                        && this.codOrigen.equals(castOther.codOrigen)
                        && this.codComprobante.equals(castOther.codComprobante);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.txtAnoproceso.hashCode();
        hash = hash * prime + this.codFilial.hashCode();
        hash = hash * prime + this.codMes.hashCode();
        hash = hash * prime + this.codOrigen.hashCode();
        hash = hash * prime + this.codComprobante.hashCode();
        return hash;
    }
}