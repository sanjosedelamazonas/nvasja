package org.sanjose.util;

import java.math.BigDecimal;

/**
 * SORCER class
 * User: prubach
 * Date: 21.09.16
 */
public class Caja {

    private String codigo;

    private String descripcion;

    private BigDecimal soles;

    private BigDecimal dolares;


    public Caja(String codigo, String descripcion, BigDecimal soles, BigDecimal dolares) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.soles = soles;
        this.dolares = dolares;
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
}
