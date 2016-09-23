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

    private boolean isInicial;


    public Caja(String codigo, String descripcion, BigDecimal soles, BigDecimal dolares) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.soles = soles;
        this.dolares = dolares;
    }

    public Caja(String codigo, String descripcion, BigDecimal soles, BigDecimal dolares, boolean isInicial) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.soles = soles;
        this.dolares = dolares;
        this.isInicial = isInicial;
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

    public boolean isInicial() {
        return isInicial;
    }

    public void setInicial(boolean inicial) {
        isInicial = inicial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Caja caja = (Caja) o;

        if (isInicial() != caja.isInicial()) return false;
        if (getCodigo() != null ? !getCodigo().equals(caja.getCodigo()) : caja.getCodigo() != null) return false;
        if (getDescripcion() != null ? !getDescripcion().equals(caja.getDescripcion()) : caja.getDescripcion() != null)
            return false;
        if (getSoles() != null ? !getSoles().equals(caja.getSoles()) : caja.getSoles() != null) return false;
        return getDolares() != null ? getDolares().equals(caja.getDolares()) : caja.getDolares() == null;

    }

    @Override
    public int hashCode() {
        int result = getCodigo() != null ? getCodigo().hashCode() : 0;
        result = 31 * result + (getDescripcion() != null ? getDescripcion().hashCode() : 0);
        result = 31 * result + (getSoles() != null ? getSoles().hashCode() : 0);
        result = 31 * result + (getDolares() != null ? getDolares().hashCode() : 0);
        result = 31 * result + (isInicial() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Caja{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", soles=" + soles +
                ", dolares=" + dolares +
                ", isInicial=" + isInicial +
                '}';
    }
}
