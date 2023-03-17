package org.sanjose.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;


/**
 * The persistent class for tercero operations
 */
public class VsjOperaciontercero implements Serializable {
    private static final long serialVersionUID = 13636626357894949L;

    private Long id;
    private String codTercero;
    private Timestamp fecComprobante;
    private String codVoucher;
    private String txtGlosaitem;
    private String codDestino;
    private String txtDestinonombre;
    private String codCtacontable;
    private Character codTipomoneda;
    private BigDecimal numDebedolar;
    private BigDecimal numDebemc;
    private BigDecimal numDebemo;
    private BigDecimal numDebesol;
    private BigDecimal numHaberdolar;
    private BigDecimal numHabermc;
    private BigDecimal numHabermo;
    private BigDecimal numHabersol;
    private String codContraparte;
    private Boolean enviado;


    public VsjOperaciontercero() {
    }

    public VsjOperaciontercero(Long id, String codTercero, Timestamp fecComprobante, String codVoucher, String txtGlosaitem, String codDestino, String txtDestinonombre, String codCtacontable, Character codTipomoneda, BigDecimal numDebedolar, BigDecimal numDebemc, BigDecimal numDebemo, BigDecimal numDebesol, BigDecimal numHaberdolar, BigDecimal numHabermc, BigDecimal numHabermo, BigDecimal numHabersol, String codContraparte) {
        this.id = id;
        this.codTercero = codTercero;
        this.fecComprobante = fecComprobante;
        this.codVoucher = codVoucher;
        this.txtGlosaitem = txtGlosaitem;
        this.codDestino = codDestino;
        this.txtDestinonombre = txtDestinonombre;
        this.codCtacontable = codCtacontable;
        this.codTipomoneda = codTipomoneda;
        this.numDebedolar = numDebedolar;
        this.numDebemc = numDebemc;
        this.numDebemo = numDebemo;
        this.numDebesol = numDebesol;
        this.numHaberdolar = numHaberdolar;
        this.numHabermc = numHabermc;
        this.numHabermo = numHabermo;
        this.numHabersol = numHabersol;
        this.codContraparte = codContraparte;
    }

    public String getCodTercero() {
        return codTercero;
    }

    public void setCodTercero(String codTercero) {
        this.codTercero = codTercero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getFecComprobante() {
        return fecComprobante;
    }

    public void setFecComprobante(Timestamp fecComprobante) {
        this.fecComprobante = fecComprobante;
    }

    public String getCodVoucher() {
        return codVoucher;
    }

    public void setCodVoucher(String codVoucher) {
        this.codVoucher = codVoucher;
    }

    public String getTxtGlosaitem() {
        return txtGlosaitem;
    }

    public void setTxtGlosaitem(String txtGlosaitem) {
        this.txtGlosaitem = txtGlosaitem;
    }

    public String getCodDestino() {
        return codDestino;
    }

    public void setCodDestino(String codDestino) {
        this.codDestino = codDestino;
    }

    public String getTxtDestinonombre() {
        return txtDestinonombre;
    }

    public void setTxtDestinonombre(String txtDestinonombre) {
        this.txtDestinonombre = txtDestinonombre;
    }

    public String getCodCtacontable() {
        return codCtacontable;
    }

    public void setCodCtacontable(String codCtacontable) {
        this.codCtacontable = codCtacontable;
    }

    public Character getCodTipomoneda() {
        return codTipomoneda;
    }

    public void setCodTipomoneda(Character codTipomoneda) {
        this.codTipomoneda = codTipomoneda;
    }

    public BigDecimal getNumDebedolar() {
        return numDebedolar;
    }

    public void setNumDebedolar(BigDecimal numDebedolar) {
        this.numDebedolar = numDebedolar;
    }

    public BigDecimal getNumDebemc() {
        return numDebemc;
    }

    public void setNumDebemc(BigDecimal numDebemc) {
        this.numDebemc = numDebemc;
    }

    public BigDecimal getNumDebemo() {
        return numDebemo;
    }

    public void setNumDebemo(BigDecimal numDebemo) {
        this.numDebemo = numDebemo;
    }

    public BigDecimal getNumDebesol() {
        return numDebesol;
    }

    public void setNumDebesol(BigDecimal numDebesol) {
        this.numDebesol = numDebesol;
    }

    public BigDecimal getNumHaberdolar() {
        return numHaberdolar;
    }

    public void setNumHaberdolar(BigDecimal numHaberdolar) {
        this.numHaberdolar = numHaberdolar;
    }

    public BigDecimal getNumHabermc() {
        return numHabermc;
    }

    public void setNumHabermc(BigDecimal numHabermc) {
        this.numHabermc = numHabermc;
    }

    public BigDecimal getNumHabermo() {
        return numHabermo;
    }

    public void setNumHabermo(BigDecimal numHabermo) {
        this.numHabermo = numHabermo;
    }

    public BigDecimal getNumHabersol() {
        return numHabersol;
    }

    public void setNumHabersol(BigDecimal numHabersol) {
        this.numHabersol = numHabersol;
    }

    public String getCodContraparte() {
        return codContraparte;
    }

    public void setCodContraparte(String codContraparte) {
        this.codContraparte = codContraparte;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VsjOperaciontercero that = (VsjOperaciontercero) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(codTercero, that.codTercero) &&
                Objects.equals(fecComprobante, that.fecComprobante) &&
                Objects.equals(codVoucher, that.codVoucher) &&
                Objects.equals(txtGlosaitem, that.txtGlosaitem) &&
                Objects.equals(codDestino, that.codDestino) &&
                Objects.equals(txtDestinonombre, that.txtDestinonombre) &&
                Objects.equals(codCtacontable, that.codCtacontable) &&
                Objects.equals(codTipomoneda, that.codTipomoneda) &&
                Objects.equals(numDebedolar, that.numDebedolar) &&
                Objects.equals(numDebemc, that.numDebemc) &&
                Objects.equals(numDebemo, that.numDebemo) &&
                Objects.equals(numDebesol, that.numDebesol) &&
                Objects.equals(numHaberdolar, that.numHaberdolar) &&
                Objects.equals(numHabermc, that.numHabermc) &&
                Objects.equals(numHabermo, that.numHabermo) &&
                Objects.equals(numHabersol, that.numHabersol) &&
                Objects.equals(codContraparte, that.codContraparte) &&
                Objects.equals(enviado, that.enviado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codTercero, fecComprobante, codVoucher, txtGlosaitem, codDestino, txtDestinonombre, codCtacontable, codTipomoneda, numDebedolar, numDebemc, numDebemo, numDebesol, numHaberdolar, numHabermc, numHabermo, numHabersol, codContraparte, enviado);
    }
}