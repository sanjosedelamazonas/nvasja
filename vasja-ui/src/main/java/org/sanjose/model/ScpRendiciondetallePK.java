package org.sanjose.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * The primary key class for the scp_comprobantedetalle database table.
 */
@Embeddable
public class ScpRendiciondetallePK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 14357658632554L;

    @Column(name="cod_rendicioncabecera", insertable=false, updatable=false)
    private int codRendicioncabecera;

    @Column(name = "num_nroitem")
    private long numNroitem;

    public ScpRendiciondetallePK() {
    }

    public long getNumNroitem() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScpRendiciondetallePK that = (ScpRendiciondetallePK) o;
        return codRendicioncabecera == that.codRendicioncabecera &&
                numNroitem == that.numNroitem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codRendicioncabecera, numNroitem);
    }

    @Override
    public String toString() {
        return "ScpRendiciondetallePK{" +
                "codRendicioncabecera=" + codRendicioncabecera +
                ", numNroitem=" + numNroitem +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}