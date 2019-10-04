package org.sanjose.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name="scp_chequependiente")
@NamedQuery(name="ScpChequependiente.findAll", query="SELECT s FROM ScpChequependiente s")
public class ScpChequependiente {
  @EmbeddedId
  private ScpChequependientePK id;
  private java.sql.Timestamp fecComprobante;
  private String txtGlosaitem;
  private String codDestino;
  private Character flgChequecobrado;
  private String codMescobrado;
  private java.sql.Timestamp fecComprobantepago;
  private String codProyecto;
  private String codFinanciera;
  private BigDecimal numTcvdolar;
  private BigDecimal numHabersol;
  private BigDecimal numHaberdolar;
  private BigDecimal numTcmo;
  private BigDecimal numHabermo;
  private String codMonedaoriginal;
  private Character flgIm;
  private java.sql.Timestamp fecFregistro;
  private String codUregistro;
  private java.sql.Timestamp fecFactualiza;
  private String codUactualiza;

  public ScpChequependientePK getId() {
    return id;
  }

  public void setId(ScpChequependientePK id) {
    this.id = id;
  }

  public java.sql.Timestamp getFecComprobante() {
    return fecComprobante;
  }

  public void setFecComprobante(java.sql.Timestamp fecComprobante) {
    this.fecComprobante = fecComprobante;
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


  public Character getFlgChequecobrado() {
    return flgChequecobrado;
  }

  public void setFlgChequecobrado(Character flgChequecobrado) {
    this.flgChequecobrado = flgChequecobrado;
  }


  public String getCodMescobrado() {
    return codMescobrado;
  }

  public void setCodMescobrado(String codMescobrado) {
    this.codMescobrado = codMescobrado;
  }


  public java.sql.Timestamp getFecComprobantepago() {
    return fecComprobantepago;
  }

  public void setFecComprobantepago(java.sql.Timestamp fecComprobantepago) {
    this.fecComprobantepago = fecComprobantepago;
  }


  public String getCodProyecto() {
    return codProyecto;
  }

  public void setCodProyecto(String codProyecto) {
    this.codProyecto = codProyecto;
  }


  public String getCodFinanciera() {
    return codFinanciera;
  }

  public void setCodFinanciera(String codFinanciera) {
    this.codFinanciera = codFinanciera;
  }


  public BigDecimal getNumTcvdolar() {
    return numTcvdolar;
  }

  public void setNumTcvdolar(BigDecimal numTcvdolar) {
    this.numTcvdolar = numTcvdolar;
  }


  public BigDecimal getNumHabersol() {
    return numHabersol;
  }

  public void setNumHabersol(BigDecimal numHabersol) {
    this.numHabersol = numHabersol;
  }


  public BigDecimal getNumHaberdolar() {
    return numHaberdolar;
  }

  public void setNumHaberdolar(BigDecimal numHaberdolar) {
    this.numHaberdolar = numHaberdolar;
  }


  public BigDecimal getNumTcmo() {
    return numTcmo;
  }

  public void setNumTcmo(BigDecimal numTcmo) {
    this.numTcmo = numTcmo;
  }


  public BigDecimal getNumHabermo() {
    return numHabermo;
  }

  public void setNumHabermo(BigDecimal numHabermo) {
    this.numHabermo = numHabermo;
  }


  public String getCodMonedaoriginal() {
    return codMonedaoriginal;
  }

  public void setCodMonedaoriginal(String codMonedaoriginal) {
    this.codMonedaoriginal = codMonedaoriginal;
  }


  public Character getFlgIm() {
    return flgIm;
  }

  public void setFlgIm(Character flgIm) {
    this.flgIm = flgIm;
  }


  public java.sql.Timestamp getFecFregistro() {
    return fecFregistro;
  }

  public void setFecFregistro(java.sql.Timestamp fecFregistro) {
    this.fecFregistro = fecFregistro;
  }


  public String getCodUregistro() {
    return codUregistro;
  }

  public void setCodUregistro(String codUregistro) {
    this.codUregistro = codUregistro;
  }


  public java.sql.Timestamp getFecFactualiza() {
    return fecFactualiza;
  }

  public void setFecFactualiza(java.sql.Timestamp fecFactualiza) {
    this.fecFactualiza = fecFactualiza;
  }


  public String getCodUactualiza() {
    return codUactualiza;
  }

  public void setCodUactualiza(String codUactualiza) {
    this.codUactualiza = codUactualiza;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScpChequependiente that = (ScpChequependiente) o;
    return Objects.equals(id, that.id) &&
            Objects.equals(fecComprobante, that.fecComprobante) &&
            Objects.equals(txtGlosaitem, that.txtGlosaitem) &&
            Objects.equals(codDestino, that.codDestino) &&
            Objects.equals(flgChequecobrado, that.flgChequecobrado) &&
            Objects.equals(codMescobrado, that.codMescobrado) &&
            Objects.equals(fecComprobantepago, that.fecComprobantepago) &&
            Objects.equals(codProyecto, that.codProyecto) &&
            Objects.equals(codFinanciera, that.codFinanciera) &&
            Objects.equals(numTcvdolar, that.numTcvdolar) &&
            Objects.equals(numHabersol, that.numHabersol) &&
            Objects.equals(numHaberdolar, that.numHaberdolar) &&
            Objects.equals(numTcmo, that.numTcmo) &&
            Objects.equals(numHabermo, that.numHabermo) &&
            Objects.equals(codMonedaoriginal, that.codMonedaoriginal) &&
            Objects.equals(flgIm, that.flgIm) &&
            Objects.equals(fecFregistro, that.fecFregistro) &&
            Objects.equals(codUregistro, that.codUregistro) &&
            Objects.equals(fecFactualiza, that.fecFactualiza) &&
            Objects.equals(codUactualiza, that.codUactualiza);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fecComprobante, txtGlosaitem, codDestino, flgChequecobrado, codMescobrado, fecComprobantepago, codProyecto, codFinanciera, numTcvdolar, numHabersol, numHaberdolar, numTcmo, numHabermo, codMonedaoriginal, flgIm, fecFregistro, codUregistro, fecFactualiza, codUactualiza);
  }
}
