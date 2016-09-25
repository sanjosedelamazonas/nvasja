package org.sanjose.bean;

import java.math.BigDecimal;

/**
 * VASJA class
 * User: prubach
 * Date: 21.09.16
 */
public class Caja {

    private String codigo;

    private String descripcion;

    private BigDecimal soles;

    private BigDecimal dolares;

    private BigDecimal solesFinal;

    private BigDecimal dolaresFinal;

    public Caja(String codigo, String descripcion, BigDecimal soles, BigDecimal dolares) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.soles = soles;
        this.dolares = dolares;
    }


    public BigDecimal getSolesFinal() {
        return solesFinal;
    }

    public void setSolesFinal(BigDecimal solesFinal) {
        this.solesFinal = solesFinal;
    }

    public BigDecimal getDolaresFinal() {
        return dolaresFinal;
    }

    public void setDolaresFinal(BigDecimal dolaresFinal) {
        this.dolaresFinal = dolaresFinal;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getSoles() {
        return soles;
    }

    public void setSoles(BigDecimal soles) {
        this.soles = soles;
    }

    public BigDecimal getDolares() {
        return dolares;
    }

    public void setDolares(BigDecimal dolares) {
        this.dolares = dolares;
    }

    public Caja(String codigo, String descripcion, BigDecimal soles, BigDecimal dolares, BigDecimal solesFinal, BigDecimal dolaresFinal) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.soles = soles;
        this.dolares = dolares;
        this.solesFinal = solesFinal;
        this.dolaresFinal = dolaresFinal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Caja caja = (Caja) o;

        if (getCodigo() != null ? !getCodigo().equals(caja.getCodigo()) : caja.getCodigo() != null) return false;
        if (getDescripcion() != null ? !getDescripcion().equals(caja.getDescripcion()) : caja.getDescripcion() != null)
            return false;
        if (getSoles() != null ? !getSoles().equals(caja.getSoles()) : caja.getSoles() != null) return false;
        if (getDolares() != null ? !getDolares().equals(caja.getDolares()) : caja.getDolares() != null) return false;
        if (getSolesFinal() != null ? !getSolesFinal().equals(caja.getSolesFinal()) : caja.getSolesFinal() != null)
            return false;
        return getDolaresFinal() != null ? getDolaresFinal().equals(caja.getDolaresFinal()) : caja.getDolaresFinal() == null;

    }

    @Override
    public int hashCode() {
        int result = getCodigo() != null ? getCodigo().hashCode() : 0;
        result = 31 * result + (getDescripcion() != null ? getDescripcion().hashCode() : 0);
        result = 31 * result + (getSoles() != null ? getSoles().hashCode() : 0);
        result = 31 * result + (getDolares() != null ? getDolares().hashCode() : 0);
        result = 31 * result + (getSolesFinal() != null ? getSolesFinal().hashCode() : 0);
        result = 31 * result + (getDolaresFinal() != null ? getDolaresFinal().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Caja{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", soles=" + soles +
                ", dolares=" + dolares +
                ", solesFinal=" + solesFinal +
                ", dolaresFinal=" + dolaresFinal +
                '}';
    }
}
