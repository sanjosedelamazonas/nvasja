package org.sanjose.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name="scp_rendicioncabecera")
@NamedQuery(name="ScpRendicioncabecera.findAll", query="SELECT v FROM ScpRendicioncabecera v")
public class ScpRendicioncabecera {

  private static final long serialVersionUID = 123234242655744L;

  @EmbeddedId
  private ScpRendicioncabeceraPK id;
  @Column(name = "fec_comprobante")
  private java.sql.Timestamp fecComprobante;
  @Column(name = "cod_tipooperacion")
  private String codTipooperacion;
  @Column(name = "cod_mediopago")
  private String codMediopago;
  @Column(name = "cod_tipomoneda")
  private String codTipomoneda;
  @Column(name = "txt_glosa")
  private String txtGlosa;

  @Column(name = "cod_destino")
  private String codDestino;

  @Column(name = "cod_banco")
  private String codBanco;
  @Column(name = "flg_enviado")
  private String flgEnviado;
  @Column(name = "cod_origenenlace")
  private String codOrigenenlace;
  @Column(name = "cod_comprobanteenlace")
  private String codComprobanteenlace;
  @Column(name = "num_totalanticipo")
  private double numTotalanticipo;
  @Column(name = "num_gastototal")
  private double numGastototal;
  @Column(name = "num_saldopendiente")
  private double numSaldopendiente;

  @Column(name = "flg_im")
  private Character flgIm;
  @Column(name = "cod_uactualiza")
  private String codUactualiza;
  @Column(name = "cod_uregistro")
  private String codUregistro;
  @Column(name = "fec_factualiza")
  private Timestamp fecFactualiza;
  @Column(name = "fec_fregistro")
  private Timestamp fecFregistro;




  public java.sql.Timestamp getFecComprobante() {
    return fecComprobante;
  }

  public void setFecComprobante(java.sql.Timestamp fecComprobante) {
    this.fecComprobante = fecComprobante;
  }


  public String getCodTipooperacion() {
    return codTipooperacion;
  }

  public void setCodTipooperacion(String codTipooperacion) {
    this.codTipooperacion = codTipooperacion;
  }


  public String getCodMediopago() {
    return codMediopago;
  }

  public void setCodMediopago(String codMediopago) {
    this.codMediopago = codMediopago;
  }


  public String getCodTipomoneda() {
    return codTipomoneda;
  }

  public void setCodTipomoneda(String codTipomoneda) {
    this.codTipomoneda = codTipomoneda;
  }


  public String getTxtGlosa() {
    return txtGlosa;
  }

  public void setTxtGlosa(String txtGlosa) {
    this.txtGlosa = txtGlosa;
  }


  public String getCodDestino() {
    return codDestino;
  }

  public void setCodDestino(String codDestino) {
    this.codDestino = codDestino;
  }


  public String getCodBanco() {
    return codBanco;
  }

  public void setCodBanco(String codBanco) {
    this.codBanco = codBanco;
  }


  public String getFlgEnviado() {
    return flgEnviado;
  }

  public void setFlgEnviado(String flgEnviado) {
    this.flgEnviado = flgEnviado;
  }


  public String getCodOrigenenlace() {
    return codOrigenenlace;
  }

  public void setCodOrigenenlace(String codOrigenenlace) {
    this.codOrigenenlace = codOrigenenlace;
  }


  public String getCodComprobanteenlace() {
    return codComprobanteenlace;
  }

  public void setCodComprobanteenlace(String codComprobanteenlace) {
    this.codComprobanteenlace = codComprobanteenlace;
  }


  public double getNumTotalanticipo() {
    return numTotalanticipo;
  }

  public void setNumTotalanticipo(double numTotalanticipo) {
    this.numTotalanticipo = numTotalanticipo;
  }


  public double getNumGastototal() {
    return numGastototal;
  }

  public void setNumGastototal(double numGastototal) {
    this.numGastototal = numGastototal;
  }


  public double getNumSaldopendiente() {
    return numSaldopendiente;
  }

  public void setNumSaldopendiente(double numSaldopendiente) {
    this.numSaldopendiente = numSaldopendiente;
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
    ScpRendicioncabecera that = (ScpRendicioncabecera) o;
    return Double.compare(that.numTotalanticipo, numTotalanticipo) == 0 &&
            Double.compare(that.numGastototal, numGastototal) == 0 &&
            Double.compare(that.numSaldopendiente, numSaldopendiente) == 0 &&
            Objects.equals(id, that.id) &&
            Objects.equals(fecComprobante, that.fecComprobante) &&
            Objects.equals(codTipooperacion, that.codTipooperacion) &&
            Objects.equals(codMediopago, that.codMediopago) &&
            Objects.equals(codTipomoneda, that.codTipomoneda) &&
            Objects.equals(txtGlosa, that.txtGlosa) &&
            Objects.equals(codDestino, that.codDestino) &&
            Objects.equals(codBanco, that.codBanco) &&
            Objects.equals(flgEnviado, that.flgEnviado) &&
            Objects.equals(codOrigenenlace, that.codOrigenenlace) &&
            Objects.equals(codComprobanteenlace, that.codComprobanteenlace) &&
            Objects.equals(flgIm, that.flgIm) &&
            Objects.equals(codUactualiza, that.codUactualiza) &&
            Objects.equals(codUregistro, that.codUregistro) &&
            Objects.equals(fecFactualiza, that.fecFactualiza) &&
            Objects.equals(fecFregistro, that.fecFregistro);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fecComprobante, codTipooperacion, codMediopago, codTipomoneda, txtGlosa, codDestino, codBanco, flgEnviado, codOrigenenlace, codComprobanteenlace, numTotalanticipo, numGastototal, numSaldopendiente, flgIm, codUactualiza, codUregistro, fecFactualiza, fecFregistro);
  }

  @Override
  public String toString() {
    return "ScpRendicioncabecera{" +
            "id=" + id +
            ", fecComprobante=" + fecComprobante +
            ", codTipooperacion='" + codTipooperacion + '\'' +
            ", codMediopago='" + codMediopago + '\'' +
            ", codTipomoneda='" + codTipomoneda + '\'' +
            ", txtGlosa='" + txtGlosa + '\'' +
            ", codDestino='" + codDestino + '\'' +
            ", codBanco='" + codBanco + '\'' +
            ", flgEnviado='" + flgEnviado + '\'' +
            ", codOrigenenlace='" + codOrigenenlace + '\'' +
            ", codComprobanteenlace='" + codComprobanteenlace + '\'' +
            ", numTotalanticipo=" + numTotalanticipo +
            ", numGastototal=" + numGastototal +
            ", numSaldopendiente=" + numSaldopendiente +
            ", flgIm=" + flgIm +
            ", codUactualiza='" + codUactualiza + '\'' +
            ", codUregistro='" + codUregistro + '\'' +
            ", fecFactualiza=" + fecFactualiza +
            ", fecFregistro=" + fecFregistro +
            '}';
  }
}
