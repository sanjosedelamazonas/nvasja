package org.sanjose.model;


import com.vaadin.data.fieldgroup.FieldGroup;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Entity
@Table(name = "scp_rendiciondetalle")
@NamedQuery(name = "ScpRendiciondetalle.findAll", query = "SELECT v FROM ScpRendiciondetalle v")
public class ScpRendiciondetalle extends VsjItem {

    private static final long serialVersionUID = 1232342435798744L;

    @EmbeddedId
    private ScpRendiciondetallePK id;

    @Column(name = "cod_filial")
    private String codFilial;
    @Column(name = "cod_origen")
    private String codOrigen;
    @Column(name = "cod_comprobante")
    private String codComprobante;
    @Column(name = "fec_comprobante")
    private java.sql.Timestamp fecComprobante;

    @Column(name = "cod_tipomov")
    private Integer codTipomov;

    @NotBlank
    @Size(min = 2, max = 70)
    @Column(name = "txt_glosaitem")
    private String txtGlosaitem;
    @Column(name = "cod_destino")
    private String codDestino;
    @Column(name = "txt_cheque")
    private String txtCheque;

    @Column(name = "flg_chequecobrado")
    private String flgChequecobrado;
    @Column(name = "cod_mescobr")
    private String codMescobr;
    @Column(name = "cod_tipocomprobantepago")
    private String codTipocomprobantepago;
    @Column(name = "txt_refcomprobantepago")
    private String txtRefcomprobantepago;
    @Column(name = "txt_refseriecomprobantepago")
    private String txtRefseriecomprobantepago;
    @Column(name = "txt_seriecomprobantepago")
    private String txtSeriecomprobantepago;
    @Column(name = "txt_comprobantepago")
    private String txtComprobantepago;
    @Column(name = "fec_comprobantepago")
    private java.sql.Timestamp fecComprobantepago;
    @Column(name = "cod_evento")
    private String codEvento;
    @Column(name = "num_refnroitem")
    private String numRefnroitem;
    @Column(name = "fec_refcomprobante")
    private Timestamp fecRefcomprobante;
    @Column(name = "cod_proyecto")
    private String codProyecto;
    @Column(name = "cod_refcomprobante")
    private String codRefcomprobante;
    @Column(name = "cod_reforigen")
    private String codReforigen;
    @Column(name = "cod_reftipocomprobantepago")
    private String codReftipocomprobantepago;
    @Column(name = "cod_registrocompraventa")
    private String codRegistrocompraventa;
    @Column(name = "fec_pagocomprobantepago")
    private Timestamp fecPagocomprobantepago;
    @Column(name = "fec_refcomprobantepago")
    private Timestamp fecRefcomprobantepago;
    @Column(name = "cod_ctaactividad")
    private String codCtaactividad;
    @Column(name = "cod_ctaarea")
    private String codCtaarea;
    @Column(name = "cod_ctacontable")
    private String codCtacontable;
    @Column(name = "cod_ctacontable79")
    private String codCtacontable79;
    @Column(name = "cod_ctacontable9")
    private String codCtacontable9;
    @Column(name = "cod_ctaespecial")
    private String codCtaespecial;
    @Column(name = "cod_ctaproyecto")
    private String codCtaproyecto;
    @Column(name = "cod_financiera")
    private String codFinanciera;
    @Column(name = "cod_flujocaja")
    private String codFlujocaja;

    @Column(name = "num_tcmo")
    private double numTcmo;
    @Column(name = "num_tcvdolar")
    private double numTcvdolar;
    @Column(name = "num_debedolar")
    private BigDecimal numDebedolar;
    @Column(name = "num_debemo")
    private BigDecimal numDebemo;
    @Column(name = "num_debesol")
    private BigDecimal numDebesol;
    @Column(name = "num_haberdolar")
    private BigDecimal numHaberdolar;
    @Column(name = "num_habermo")
    private BigDecimal numHabermo;
    @Column(name = "num_habersol")
    private BigDecimal numHabersol;

    @Column(name = "cod_monedaoriginal")
    private String codMonedaoriginal;
    @Column(name = "flg_tcreferencia")
    private String flgTcreferencia;
    @Column(name = "flg_conversion")
    private String flgConversion;
    @Column(name = "cod_pais")
    private String codPais;
    @Column(name = "cod_departamento")
    private String codDepartamento;
    @Column(name = "flg_recuperaigv")
    private String flgRecuperaigv;
    @Column(name = "por_igv")
    private double porIgv;
    @Column(name = "por_ies")
    private double porIes;
    @Column(name = "num_nroitem2")
    private String numNroitem2;
    @Column(name = "cod_contraparte")
    private String codContraparte;
    @Column(name = "txt_nroretencion")
    private String txtNroretencion;
    @Column(name = "fec_retencion")
    private java.sql.Timestamp fecRetencion;
    @Column(name = "flg_esactivo")
    private String flgEsactivo;
    @Column(name = "txt_NroCompSujNoDomi")
    private String txtNroCompSujNoDomi;
    @Column(name = "flg_RetieneCuarta")
    private String flgRetieneCuarta;
    @Column(name = "cod_gastofijo")
    private String codGastofijo;
    @Column(name = "flg_distribuir")
    private String flgDistribuir;
    @Column(name = "flg_distribuido")
    private String flgDistribuido;
    @Column(name = "cod_TipoRegistro")
    private String codTipoRegistro;
    @Column(name = "num_tcmc")
    private double numTcmc;
    @Column(name = "num_debemc")
    private BigDecimal numDebemc;
    @Column(name = "num_habermc")
    private BigDecimal numHabermc;
    @Column(name = "txt_nombreactividad")
    private String txtNombreactividad;
    @Column(name = "fec_anticipo")
    private java.sql.Timestamp fecAnticipo;
    @Column(name = "num_importeanticipo")
    private double numImporteanticipo;
    @Column(name = "num_sumaanticipo")
    private double numSumaanticipo;
    @Column(name = "fec_rendicion")
    private java.sql.Timestamp fecRendicion;
    @Column(name = "num_saldo")
    private double numSaldo;

    @Column(name = "flg_im")
    private Character flgIm;

    //bi-directional many-to-one association to ScpBancocabecera
    @ManyToOne
    @JoinColumn(name = "cod_rendicioncabecera", insertable = false, updatable = false)
    private ScpRendicioncabecera scpRendicioncabecera;

    public ScpRendiciondetallePK getId() {
        return id;
    }

    public void setId(ScpRendiciondetallePK id) {
        this.id = id;
    }

    public Timestamp getFecComprobante() {
        return fecComprobante;
    }

    public void setFecComprobante(Timestamp fecComprobante) {
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

    public String getTxtCheque() {
        return txtCheque;
    }

    public void setTxtCheque(String txtCheque) {
        this.txtCheque = txtCheque;
    }

    public String getFlgChequecobrado() {
        return flgChequecobrado;
    }

    public void setFlgChequecobrado(String flgChequecobrado) {
        this.flgChequecobrado = flgChequecobrado;
    }

    public String getCodMescobr() {
        return codMescobr;
    }

    public void setCodMescobr(String codMescobr) {
        this.codMescobr = codMescobr;
    }

    public String getCodTipocomprobantepago() {
        return codTipocomprobantepago;
    }

    public void setCodTipocomprobantepago(String codTipocomprobantepago) {
        this.codTipocomprobantepago = codTipocomprobantepago;
    }

    public String getTxtRefcomprobantepago() {
        return txtRefcomprobantepago;
    }

    public void setTxtRefcomprobantepago(String txtRefcomprobantepago) {
        this.txtRefcomprobantepago = txtRefcomprobantepago;
    }

    public String getTxtRefseriecomprobantepago() {
        return txtRefseriecomprobantepago;
    }

    public void setTxtRefseriecomprobantepago(String txtRefseriecomprobantepago) {
        this.txtRefseriecomprobantepago = txtRefseriecomprobantepago;
    }

    public String getTxtSeriecomprobantepago() {
        return txtSeriecomprobantepago;
    }

    public void setTxtSeriecomprobantepago(String txtSeriecomprobantepago) {
        this.txtSeriecomprobantepago = txtSeriecomprobantepago;
    }

    public String getTxtComprobantepago() {
        return txtComprobantepago;
    }

    public void setTxtComprobantepago(String txtComprobantepago) {
        this.txtComprobantepago = txtComprobantepago;
    }

    public Timestamp getFecComprobantepago() {
        return fecComprobantepago;
    }

    public void setFecComprobantepago(Timestamp fecComprobantepago) {
        this.fecComprobantepago = fecComprobantepago;
    }

    public String getCodEvento() {
        return codEvento;
    }

    public void setCodEvento(String codEvento) {
        this.codEvento = codEvento;
    }

    public String getNumRefnroitem() {
        return numRefnroitem;
    }

    public void setNumRefnroitem(String numRefnroitem) {
        this.numRefnroitem = numRefnroitem;
    }

    public Timestamp getFecRefcomprobante() {
        return fecRefcomprobante;
    }

    public void setFecRefcomprobante(Timestamp fecRefcomprobante) {
        this.fecRefcomprobante = fecRefcomprobante;
    }

    public String getCodProyecto() {
        return codProyecto;
    }

    public void setCodProyecto(String codProyecto) {
        this.codProyecto = codProyecto;
    }

    public String getCodRefcomprobante() {
        return codRefcomprobante;
    }

    public void setCodRefcomprobante(String codRefcomprobante) {
        this.codRefcomprobante = codRefcomprobante;
    }

    public String getCodReforigen() {
        return codReforigen;
    }

    public void setCodReforigen(String codReforigen) {
        this.codReforigen = codReforigen;
    }

    public String getCodReftipocomprobantepago() {
        return codReftipocomprobantepago;
    }

    public void setCodReftipocomprobantepago(String codReftipocomprobantepago) {
        this.codReftipocomprobantepago = codReftipocomprobantepago;
    }

    public String getCodRegistrocompraventa() {
        return codRegistrocompraventa;
    }

    public void setCodRegistrocompraventa(String codRegistrocompraventa) {
        this.codRegistrocompraventa = codRegistrocompraventa;
    }

    public Timestamp getFecPagocomprobantepago() {
        return fecPagocomprobantepago;
    }

    public void setFecPagocomprobantepago(Timestamp fecPagocomprobantepago) {
        this.fecPagocomprobantepago = fecPagocomprobantepago;
    }

    public Timestamp getFecRefcomprobantepago() {
        return fecRefcomprobantepago;
    }

    public void setFecRefcomprobantepago(Timestamp fecRefcomprobantepago) {
        this.fecRefcomprobantepago = fecRefcomprobantepago;
    }

    public String getCodCtaactividad() {
        return codCtaactividad;
    }

    public void setCodCtaactividad(String codCtaactividad) {
        this.codCtaactividad = codCtaactividad;
    }

    public String getCodCtaarea() {
        return codCtaarea;
    }

    public void setCodCtaarea(String codCtaarea) {
        this.codCtaarea = codCtaarea;
    }

    public String getCodCtacontable() {
        return codCtacontable;
    }

    public void setCodCtacontable(String codCtacontable) {
        this.codCtacontable = codCtacontable;
    }

    public String getCodCtacontable79() {
        return codCtacontable79;
    }

    public void setCodCtacontable79(String codCtacontable79) {
        this.codCtacontable79 = codCtacontable79;
    }

    public String getCodCtacontable9() {
        return codCtacontable9;
    }

    public void setCodCtacontable9(String codCtacontable9) {
        this.codCtacontable9 = codCtacontable9;
    }

    public String getCodCtaespecial() {
        return codCtaespecial;
    }

    public void setCodCtaespecial(String codCtaespecial) {
        this.codCtaespecial = codCtaespecial;
    }

    public String getCodCtaproyecto() {
        return codCtaproyecto;
    }

    public void setCodCtaproyecto(String codCtaproyecto) {
        this.codCtaproyecto = codCtaproyecto;
    }

    public String getCodFinanciera() {
        return codFinanciera;
    }

    public void setCodFinanciera(String codFinanciera) {
        this.codFinanciera = codFinanciera;
    }

    public String getCodFlujocaja() {
        return codFlujocaja;
    }

    public void setCodFlujocaja(String codFlujocaja) {
        this.codFlujocaja = codFlujocaja;
    }

    public double getNumTcmo() {
        return numTcmo;
    }

    public void setNumTcmo(double numTcmo) {
        this.numTcmo = numTcmo;
    }

    public double getNumTcvdolar() {
        return numTcvdolar;
    }

    public void setNumTcvdolar(double numTcvdolar) {
        this.numTcvdolar = numTcvdolar;
    }

    public BigDecimal getNumDebedolar() {
        return numDebedolar;
    }

    public void setNumDebedolar(BigDecimal numDebedolar) {
        this.numDebedolar = numDebedolar;
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

    public String getCodMonedaoriginal() {
        return codMonedaoriginal;
    }

    public void setCodMonedaoriginal(String codMonedaoriginal) {
        this.codMonedaoriginal = codMonedaoriginal;
    }

    public String getFlgTcreferencia() {
        return flgTcreferencia;
    }

    public void setFlgTcreferencia(String flgTcreferencia) {
        this.flgTcreferencia = flgTcreferencia;
    }

    public String getFlgConversion() {
        return flgConversion;
    }

    public void setFlgConversion(String flgConversion) {
        this.flgConversion = flgConversion;
    }

    public String getCodPais() {
        return codPais;
    }

    public void setCodPais(String codPais) {
        this.codPais = codPais;
    }

    public String getCodDepartamento() {
        return codDepartamento;
    }

    public void setCodDepartamento(String codDepartamento) {
        this.codDepartamento = codDepartamento;
    }

    public String getFlgRecuperaigv() {
        return flgRecuperaigv;
    }

    public void setFlgRecuperaigv(String flgRecuperaigv) {
        this.flgRecuperaigv = flgRecuperaigv;
    }

    public double getPorIgv() {
        return porIgv;
    }

    public void setPorIgv(double porIgv) {
        this.porIgv = porIgv;
    }

    public double getPorIes() {
        return porIes;
    }

    public void setPorIes(double porIes) {
        this.porIes = porIes;
    }

    public String getNumNroitem2() {
        return numNroitem2;
    }

    public void setNumNroitem2(String numNroitem2) {
        this.numNroitem2 = numNroitem2;
    }

    public String getCodContraparte() {
        return codContraparte;
    }

    public void setCodContraparte(String codContraparte) {
        this.codContraparte = codContraparte;
    }

    public String getTxtNroretencion() {
        return txtNroretencion;
    }

    public void setTxtNroretencion(String txtNroretencion) {
        this.txtNroretencion = txtNroretencion;
    }

    public Timestamp getFecRetencion() {
        return fecRetencion;
    }

    public void setFecRetencion(Timestamp fecRetencion) {
        this.fecRetencion = fecRetencion;
    }

    public String getFlgEsactivo() {
        return flgEsactivo;
    }

    public void setFlgEsactivo(String flgEsactivo) {
        this.flgEsactivo = flgEsactivo;
    }

    public String getTxtNroCompSujNoDomi() {
        return txtNroCompSujNoDomi;
    }

    public void setTxtNroCompSujNoDomi(String txtNroCompSujNoDomi) {
        this.txtNroCompSujNoDomi = txtNroCompSujNoDomi;
    }

    public String getFlgRetieneCuarta() {
        return flgRetieneCuarta;
    }

    public void setFlgRetieneCuarta(String flgRetieneCuarta) {
        this.flgRetieneCuarta = flgRetieneCuarta;
    }

    public String getCodGastofijo() {
        return codGastofijo;
    }

    public void setCodGastofijo(String codGastofijo) {
        this.codGastofijo = codGastofijo;
    }

    public String getFlgDistribuir() {
        return flgDistribuir;
    }

    public void setFlgDistribuir(String flgDistribuir) {
        this.flgDistribuir = flgDistribuir;
    }

    public String getFlgDistribuido() {
        return flgDistribuido;
    }

    public void setFlgDistribuido(String flgDistribuido) {
        this.flgDistribuido = flgDistribuido;
    }

    public String getCodTipoRegistro() {
        return codTipoRegistro;
    }

    public void setCodTipoRegistro(String codTipoRegistro) {
        this.codTipoRegistro = codTipoRegistro;
    }

    public double getNumTcmc() {
        return numTcmc;
    }

    public void setNumTcmc(double numTcmc) {
        this.numTcmc = numTcmc;
    }

    public BigDecimal getNumDebemc() {
        return numDebemc;
    }

    public void setNumDebemc(BigDecimal numDebemc) {
        this.numDebemc = numDebemc;
    }

    public BigDecimal getNumHabermc() {
        return numHabermc;
    }

    public void setNumHabermc(BigDecimal numHabermc) {
        this.numHabermc = numHabermc;
    }

    public String getTxtNombreactividad() {
        return txtNombreactividad;
    }

    public void setTxtNombreactividad(String txtNombreactividad) {
        this.txtNombreactividad = txtNombreactividad;
    }

    public Timestamp getFecAnticipo() {
        return fecAnticipo;
    }

    public void setFecAnticipo(Timestamp fecAnticipo) {
        this.fecAnticipo = fecAnticipo;
    }

    public double getNumImporteanticipo() {
        return numImporteanticipo;
    }

    public void setNumImporteanticipo(double numImporteanticipo) {
        this.numImporteanticipo = numImporteanticipo;
    }

    public double getNumSumaanticipo() {
        return numSumaanticipo;
    }

    public void setNumSumaanticipo(double numSumaanticipo) {
        this.numSumaanticipo = numSumaanticipo;
    }

    public Timestamp getFecRendicion() {
        return fecRendicion;
    }

    public void setFecRendicion(Timestamp fecRendicion) {
        this.fecRendicion = fecRendicion;
    }

    public double getNumSaldo() {
        return numSaldo;
    }

    public void setNumSaldo(double numSaldo) {
        this.numSaldo = numSaldo;
    }

    public Character getFlgIm() {
        return flgIm;
    }

    public void setFlgIm(Character flgIm) {
        this.flgIm = flgIm;
    }

    public ScpRendicioncabecera getScpRendicioncabecera() {
        return scpRendicioncabecera;
    }

    public Integer getCodTipomov() {
        return codTipomov;
    }

    public void setCodTipomov(Integer codTipomov) {
        this.codTipomov = codTipomov;
    }

    public void setScpRendicioncabecera(ScpRendicioncabecera scpRendicioncabecera) {
        this.scpRendicioncabecera = scpRendicioncabecera;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScpRendiciondetalle that = (ScpRendiciondetalle) o;
        return Double.compare(that.numTcmo, numTcmo) == 0 &&
                Double.compare(that.numTcvdolar, numTcvdolar) == 0 &&
                Double.compare(that.porIgv, porIgv) == 0 &&
                Double.compare(that.porIes, porIes) == 0 &&
                Double.compare(that.numTcmc, numTcmc) == 0 &&
                Double.compare(that.numImporteanticipo, numImporteanticipo) == 0 &&
                Double.compare(that.numSumaanticipo, numSumaanticipo) == 0 &&
                Double.compare(that.numSaldo, numSaldo) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(codFilial, that.codFilial) &&
                Objects.equals(codOrigen, that.codOrigen) &&
                Objects.equals(codComprobante, that.codComprobante) &&
                Objects.equals(fecComprobante, that.fecComprobante) &&
                Objects.equals(txtGlosaitem, that.txtGlosaitem) &&
                Objects.equals(codDestino, that.codDestino) &&
                Objects.equals(txtCheque, that.txtCheque) &&
                Objects.equals(flgChequecobrado, that.flgChequecobrado) &&
                Objects.equals(codMescobr, that.codMescobr) &&
                Objects.equals(codTipocomprobantepago, that.codTipocomprobantepago) &&
                Objects.equals(txtRefcomprobantepago, that.txtRefcomprobantepago) &&
                Objects.equals(txtRefseriecomprobantepago, that.txtRefseriecomprobantepago) &&
                Objects.equals(txtSeriecomprobantepago, that.txtSeriecomprobantepago) &&
                Objects.equals(txtComprobantepago, that.txtComprobantepago) &&
                Objects.equals(fecComprobantepago, that.fecComprobantepago) &&
                Objects.equals(codEvento, that.codEvento) &&
                Objects.equals(numRefnroitem, that.numRefnroitem) &&
                Objects.equals(fecRefcomprobante, that.fecRefcomprobante) &&
                Objects.equals(codProyecto, that.codProyecto) &&
                Objects.equals(codRefcomprobante, that.codRefcomprobante) &&
                Objects.equals(codReforigen, that.codReforigen) &&
                Objects.equals(codReftipocomprobantepago, that.codReftipocomprobantepago) &&
                Objects.equals(codRegistrocompraventa, that.codRegistrocompraventa) &&
                Objects.equals(fecPagocomprobantepago, that.fecPagocomprobantepago) &&
                Objects.equals(fecRefcomprobantepago, that.fecRefcomprobantepago) &&
                Objects.equals(codCtaactividad, that.codCtaactividad) &&
                Objects.equals(codCtaarea, that.codCtaarea) &&
                Objects.equals(codCtacontable, that.codCtacontable) &&
                Objects.equals(codCtacontable79, that.codCtacontable79) &&
                Objects.equals(codCtacontable9, that.codCtacontable9) &&
                Objects.equals(codCtaespecial, that.codCtaespecial) &&
                Objects.equals(codCtaproyecto, that.codCtaproyecto) &&
                Objects.equals(codFinanciera, that.codFinanciera) &&
                Objects.equals(codFlujocaja, that.codFlujocaja) &&
                Objects.equals(numDebedolar, that.numDebedolar) &&
                Objects.equals(numDebemo, that.numDebemo) &&
                Objects.equals(numDebesol, that.numDebesol) &&
                Objects.equals(numHaberdolar, that.numHaberdolar) &&
                Objects.equals(numHabermo, that.numHabermo) &&
                Objects.equals(numHabersol, that.numHabersol) &&
                Objects.equals(codMonedaoriginal, that.codMonedaoriginal) &&
                Objects.equals(flgTcreferencia, that.flgTcreferencia) &&
                Objects.equals(flgConversion, that.flgConversion) &&
                Objects.equals(codPais, that.codPais) &&
                Objects.equals(codDepartamento, that.codDepartamento) &&
                Objects.equals(flgRecuperaigv, that.flgRecuperaigv) &&
                Objects.equals(numNroitem2, that.numNroitem2) &&
                Objects.equals(codContraparte, that.codContraparte) &&
                Objects.equals(txtNroretencion, that.txtNroretencion) &&
                Objects.equals(fecRetencion, that.fecRetencion) &&
                Objects.equals(flgEsactivo, that.flgEsactivo) &&
                Objects.equals(txtNroCompSujNoDomi, that.txtNroCompSujNoDomi) &&
                Objects.equals(flgRetieneCuarta, that.flgRetieneCuarta) &&
                Objects.equals(codGastofijo, that.codGastofijo) &&
                Objects.equals(flgDistribuir, that.flgDistribuir) &&
                Objects.equals(flgDistribuido, that.flgDistribuido) &&
                Objects.equals(codTipoRegistro, that.codTipoRegistro) &&
                Objects.equals(numDebemc, that.numDebemc) &&
                Objects.equals(numHabermc, that.numHabermc) &&
                Objects.equals(txtNombreactividad, that.txtNombreactividad) &&
                Objects.equals(fecAnticipo, that.fecAnticipo) &&
                Objects.equals(fecRendicion, that.fecRendicion) &&
                Objects.equals(flgIm, that.flgIm) &&
                Objects.equals(scpRendicioncabecera, that.scpRendicioncabecera);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codFilial, codOrigen, codComprobante, fecComprobante, txtGlosaitem, codDestino, txtCheque, flgChequecobrado, codMescobr, codTipocomprobantepago, txtRefcomprobantepago, txtRefseriecomprobantepago, txtSeriecomprobantepago, txtComprobantepago, fecComprobantepago, codEvento, numRefnroitem, fecRefcomprobante, codProyecto, codRefcomprobante, codReforigen, codReftipocomprobantepago, codRegistrocompraventa, fecPagocomprobantepago, fecRefcomprobantepago, codCtaactividad, codCtaarea, codCtacontable, codCtacontable79, codCtacontable9, codCtaespecial, codCtaproyecto, codFinanciera, codFlujocaja, numTcmo, numTcvdolar, numDebedolar, numDebemo, numDebesol, numHaberdolar, numHabermo, numHabersol, codMonedaoriginal, flgTcreferencia, flgConversion, codPais, codDepartamento, flgRecuperaigv, porIgv, porIes, numNroitem2, codContraparte, txtNroretencion, fecRetencion, flgEsactivo, txtNroCompSujNoDomi, flgRetieneCuarta, codGastofijo, flgDistribuir, flgDistribuido, codTipoRegistro, numTcmc, numDebemc, numHabermc, txtNombreactividad, fecAnticipo, numImporteanticipo, numSumaanticipo, fecRendicion, numSaldo, flgIm, scpRendicioncabecera);
    }

    @Override
    public String toString() {
        return "ScpRendiciondetalle{" +
                "id=" + id +
                ", codFilial='" + codFilial + '\'' +
                ", codOrigen='" + codOrigen + '\'' +
                ", codComprobante='" + codComprobante + '\'' +
                ", fecComprobante=" + fecComprobante +
                ", txtGlosaitem='" + txtGlosaitem + '\'' +
                ", codDestino='" + codDestino + '\'' +
                ", txtCheque='" + txtCheque + '\'' +
                ", flgChequecobrado='" + flgChequecobrado + '\'' +
                ", codMescobr='" + codMescobr + '\'' +
                ", codTipocomprobantepago='" + codTipocomprobantepago + '\'' +
                ", txtRefcomprobantepago='" + txtRefcomprobantepago + '\'' +
                ", txtRefseriecomprobantepago='" + txtRefseriecomprobantepago + '\'' +
                ", txtSeriecomprobantepago='" + txtSeriecomprobantepago + '\'' +
                ", txtComprobantepago='" + txtComprobantepago + '\'' +
                ", fecComprobantepago=" + fecComprobantepago +
                ", codEvento='" + codEvento + '\'' +
                ", numRefnroitem='" + numRefnroitem + '\'' +
                ", fecRefcomprobante=" + fecRefcomprobante +
                ", codProyecto='" + codProyecto + '\'' +
                ", codRefcomprobante='" + codRefcomprobante + '\'' +
                ", codReforigen='" + codReforigen + '\'' +
                ", codReftipocomprobantepago='" + codReftipocomprobantepago + '\'' +
                ", codRegistrocompraventa='" + codRegistrocompraventa + '\'' +
                ", fecPagocomprobantepago=" + fecPagocomprobantepago +
                ", fecRefcomprobantepago=" + fecRefcomprobantepago +
                ", codCtaactividad='" + codCtaactividad + '\'' +
                ", codCtaarea='" + codCtaarea + '\'' +
                ", codCtacontable='" + codCtacontable + '\'' +
                ", codCtacontable79='" + codCtacontable79 + '\'' +
                ", codCtacontable9='" + codCtacontable9 + '\'' +
                ", codCtaespecial='" + codCtaespecial + '\'' +
                ", codCtaproyecto='" + codCtaproyecto + '\'' +
                ", codFinanciera='" + codFinanciera + '\'' +
                ", codFlujocaja='" + codFlujocaja + '\'' +
                ", numTcmo=" + numTcmo +
                ", numTcvdolar=" + numTcvdolar +
                ", numDebedolar=" + numDebedolar +
                ", numDebemo=" + numDebemo +
                ", numDebesol=" + numDebesol +
                ", numHaberdolar=" + numHaberdolar +
                ", numHabermo=" + numHabermo +
                ", numHabersol=" + numHabersol +
                ", codMonedaoriginal='" + codMonedaoriginal + '\'' +
                ", flgTcreferencia='" + flgTcreferencia + '\'' +
                ", flgConversion='" + flgConversion + '\'' +
                ", codPais='" + codPais + '\'' +
                ", codDepartamento='" + codDepartamento + '\'' +
                ", flgRecuperaigv='" + flgRecuperaigv + '\'' +
                ", porIgv=" + porIgv +
                ", porIes=" + porIes +
                ", numNroitem2='" + numNroitem2 + '\'' +
                ", codContraparte='" + codContraparte + '\'' +
                ", txtNroretencion='" + txtNroretencion + '\'' +
                ", fecRetencion=" + fecRetencion +
                ", flgEsactivo='" + flgEsactivo + '\'' +
                ", txtNroCompSujNoDomi='" + txtNroCompSujNoDomi + '\'' +
                ", flgRetieneCuarta='" + flgRetieneCuarta + '\'' +
                ", codGastofijo='" + codGastofijo + '\'' +
                ", flgDistribuir='" + flgDistribuir + '\'' +
                ", flgDistribuido='" + flgDistribuido + '\'' +
                ", codTipoRegistro='" + codTipoRegistro + '\'' +
                ", numTcmc=" + numTcmc +
                ", numDebemc=" + numDebemc +
                ", numHabermc=" + numHabermc +
                ", txtNombreactividad='" + txtNombreactividad + '\'' +
                ", fecAnticipo=" + fecAnticipo +
                ", numImporteanticipo=" + numImporteanticipo +
                ", numSumaanticipo=" + numSumaanticipo +
                ", fecRendicion=" + fecRendicion +
                ", numSaldo=" + numSaldo +
                ", flgIm=" + flgIm +
                ", scpRendicioncabecera=" + scpRendicioncabecera +
                '}';
    }

}
