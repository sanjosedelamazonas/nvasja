package org.sanjose.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_comprobantedetalle database table.
 */
@Entity
@Table(name = "scp_comprobantedetalle")
@NamedQuery(name = "ScpComprobantedetalle.findAll", query = "SELECT s FROM ScpComprobantedetalle s")
public class ScpComprobantedetalle implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ScpComprobantedetallePK id;

    @Column(name = "cod_contraparte")
    private String codContraparte;

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

    @Column(name = "cod_departamento")
    private String codDepartamento;

    @Column(name = "cod_destino")
    private String codDestino;

    @Column(name = "cod_evento")
    private String codEvento;

    @Column(name = "cod_financiera")
    private String codFinanciera;

    @Column(name = "cod_flujocaja")
    private String codFlujocaja;

    @Column(name = "cod_gastofijo")
    private String codGastofijo;

    @Column(name = "cod_mescobr")
    private String codMescobr;

    @Column(name = "cod_monedaoriginal")
    private String codMonedaoriginal;

    @Column(name = "cod_pais")
    private String codPais;

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

    @Column(name = "cod_tercero")
    private String codTercero;

    @Column(name = "cod_tipocomprobantepago")
    private String codTipocomprobantepago;

    @Column(name = "cod_tipomoneda")
    private Character codTipomoneda;

    @Column(name = "cod_Tiporegistro")
    private String cod_Tiporegistro;

    @Column(name = "cod_uactualiza")
    private String codUactualiza;

    @Column(name = "cod_uregistro")
    private String codUregistro;

    @Column(name = "fec_comprobante")
    private Timestamp fecComprobante;

    @Column(name = "fec_comprobantepago")
    private Timestamp fecComprobantepago;

    @Column(name = "fec_factualiza")
    private Timestamp fecFactualiza;

    @Column(name = "fec_fregistro")
    private Timestamp fecFregistro;

    @Column(name = "fec_pagocomprobantepago")
    private Timestamp fecPagocomprobantepago;

    @Column(name = "fec_refcomprobante")
    private Timestamp fecRefcomprobante;

    @Column(name = "fec_refcomprobantepago")
    private Timestamp fecRefcomprobantepago;

    @Column(name = "fec_retencion")
    private Timestamp fecRetencion;

    @Column(name = "flg_chequecobrado")
    private Character flgChequecobrado;

    @Column(name = "flg_conversion")
    private Character flgConversion;

    @Column(name = "flg_distribuido")
    private Character flgDistribuido;

    @Column(name = "flg_distribuir")
    private Character flgDistribuir;

    @Column(name = "flg_esactivo")
    private Character flgEsactivo;

    @Column(name = "flg_im")
    private Character flgIm;

    @Column(name = "flg_recuperaigv")
    private Character flgRecuperaigv;

    private Character flg_Retienecuarta;

    @Column(name = "flg_tcreferencia")
    private Character flgTcreferencia;

    @Column(name = "num_debedolar")
    private BigDecimal numDebedolar;

    @Column(name = "num_debemc")
    private BigDecimal numDebemc;

    @Column(name = "num_debemo")
    private BigDecimal numDebemo;

    @Column(name = "num_debesol")
    private BigDecimal numDebesol;

    @Column(name = "num_haberdolar")
    private BigDecimal numHaberdolar;

    @Column(name = "num_habermc")
    private BigDecimal numHabermc;

    @Column(name = "num_habermo")
    private BigDecimal numHabermo;

    @Column(name = "num_habersol")
    private BigDecimal numHabersol;

    @Column(name = "num_nroitem2")
    private BigDecimal numNroitem2;

    @Column(name = "num_refnroitem")
    private BigDecimal numRefnroitem;

    @Column(name = "num_tcmc")
    private BigDecimal numTcmc;

    @Column(name = "num_tcmo")
    private double numTcmo;

    @Column(name = "num_tcvdolar")
    private double numTcvdolar;

    @Column(name = "por_ies")
    private double porIes;

    @Column(name = "por_igv")
    private double porIgv;

    @Column(name = "txt_cheque")
    private String txtCheque;

    @Column(name = "txt_comprobantepago")
    private String txtComprobantepago;

    @Column(name = "txt_glosaitem")
    private String txtGlosaitem;

    private String txt_Nrocompsujnodomi;

    @Column(name = "txt_nroretencion")
    private String txtNroretencion;

    @Column(name = "txt_refcomprobantepago")
    private String txtRefcomprobantepago;

    @Column(name = "txt_refseriecomprobantepago")
    private String txtRefseriecomprobantepago;

    @Column(name = "txt_seriecomprobantepago")
    private String txtSeriecomprobantepago;

//    @ManyToOne(targetEntity = ScpDestino.class)
//    @JoinColumn(name = "cod_destino", insertable = false, updatable = false)
//    private ScpDestino scpDestino;

    public ScpComprobantedetalle() {
    }

    public ScpComprobantedetallePK getId() {
        return this.id;
    }

    public void setId(ScpComprobantedetallePK id) {
        this.id = id;
    }

    public String getCodContraparte() {
        return this.codContraparte;
    }

    public void setCodContraparte(String codContraparte) {
        this.codContraparte = codContraparte;
    }

    public String getCodCtaactividad() {
        return this.codCtaactividad;
    }

    public void setCodCtaactividad(String codCtaactividad) {
        this.codCtaactividad = codCtaactividad;
    }

    public String getCodCtaarea() {
        return this.codCtaarea;
    }

    public void setCodCtaarea(String codCtaarea) {
        this.codCtaarea = codCtaarea;
    }

    public String getCodCtacontable() {
        return this.codCtacontable;
    }

    public void setCodCtacontable(String codCtacontable) {
        this.codCtacontable = codCtacontable;
    }

    public String getCodCtacontable79() {
        return this.codCtacontable79;
    }

    public void setCodCtacontable79(String codCtacontable79) {
        this.codCtacontable79 = codCtacontable79;
    }

    public String getCodCtacontable9() {
        return this.codCtacontable9;
    }

    public void setCodCtacontable9(String codCtacontable9) {
        this.codCtacontable9 = codCtacontable9;
    }

    public String getCodCtaespecial() {
        return this.codCtaespecial;
    }

    public void setCodCtaespecial(String codCtaespecial) {
        this.codCtaespecial = codCtaespecial;
    }

    public String getCodCtaproyecto() {
        return this.codCtaproyecto;
    }

    public void setCodCtaproyecto(String codCtaproyecto) {
        this.codCtaproyecto = codCtaproyecto;
    }

    public String getCodDepartamento() {
        return this.codDepartamento;
    }

    public void setCodDepartamento(String codDepartamento) {
        this.codDepartamento = codDepartamento;
    }

    public String getCodDestino() {
        return this.codDestino;
    }

    public void setCodDestino(String codDestino) {
        this.codDestino = codDestino;
    }

    public String getCodEvento() {
        return this.codEvento;
    }

    public void setCodEvento(String codEvento) {
        this.codEvento = codEvento;
    }

    public String getCodFinanciera() {
        return this.codFinanciera;
    }

    public void setCodFinanciera(String codFinanciera) {
        this.codFinanciera = codFinanciera;
    }

    public String getCodFlujocaja() {
        return this.codFlujocaja;
    }

    public void setCodFlujocaja(String codFlujocaja) {
        this.codFlujocaja = codFlujocaja;
    }

    public String getCodGastofijo() {
        return this.codGastofijo;
    }

    public void setCodGastofijo(String codGastofijo) {
        this.codGastofijo = codGastofijo;
    }

    public String getCodMescobr() {
        return this.codMescobr;
    }

    public void setCodMescobr(String codMescobr) {
        this.codMescobr = codMescobr;
    }

    public String getCodMonedaoriginal() {
        return this.codMonedaoriginal;
    }

    public void setCodMonedaoriginal(String codMonedaoriginal) {
        this.codMonedaoriginal = codMonedaoriginal;
    }

    public String getCodPais() {
        return this.codPais;
    }

    public void setCodPais(String codPais) {
        this.codPais = codPais;
    }

    public String getCodProyecto() {
        return this.codProyecto;
    }

    public void setCodProyecto(String codProyecto) {
        this.codProyecto = codProyecto;
    }

    public String getCodRefcomprobante() {
        return this.codRefcomprobante;
    }

    public void setCodRefcomprobante(String codRefcomprobante) {
        this.codRefcomprobante = codRefcomprobante;
    }

    public String getCodReforigen() {
        return this.codReforigen;
    }

    public void setCodReforigen(String codReforigen) {
        this.codReforigen = codReforigen;
    }

    public String getCodReftipocomprobantepago() {
        return this.codReftipocomprobantepago;
    }

    public void setCodReftipocomprobantepago(String codReftipocomprobantepago) {
        this.codReftipocomprobantepago = codReftipocomprobantepago;
    }

    public String getCodRegistrocompraventa() {
        return this.codRegistrocompraventa;
    }

    public void setCodRegistrocompraventa(String codRegistrocompraventa) {
        this.codRegistrocompraventa = codRegistrocompraventa;
    }

    public String getCodTercero() {
        return this.codTercero;
    }

    public void setCodTercero(String codTercero) {
        this.codTercero = codTercero;
    }

    public String getCodTipocomprobantepago() {
        return this.codTipocomprobantepago;
    }

    public void setCodTipocomprobantepago(String codTipocomprobantepago) {
        this.codTipocomprobantepago = codTipocomprobantepago;
    }

    public String getCodUactualiza() {
        return this.codUactualiza;
    }

    public void setCodUactualiza(String codUactualiza) {
        this.codUactualiza = codUactualiza;
    }

    public String getCodUregistro() {
        return this.codUregistro;
    }

    public void setCodUregistro(String codUregistro) {
        this.codUregistro = codUregistro;
    }

    public Timestamp getFecComprobante() {
        return this.fecComprobante;
    }

    public void setFecComprobante(Timestamp fecComprobante) {
        this.fecComprobante = fecComprobante;
    }

    public Timestamp getFecComprobantepago() {
        return this.fecComprobantepago;
    }

    public void setFecComprobantepago(Timestamp fecComprobantepago) {
        this.fecComprobantepago = fecComprobantepago;
    }

    public Timestamp getFecFactualiza() {
        return this.fecFactualiza;
    }

    public void setFecFactualiza(Timestamp fecFactualiza) {
        this.fecFactualiza = fecFactualiza;
    }

    public Timestamp getFecFregistro() {
        return this.fecFregistro;
    }

    public void setFecFregistro(Timestamp fecFregistro) {
        this.fecFregistro = fecFregistro;
    }

    public Timestamp getFecPagocomprobantepago() {
        return this.fecPagocomprobantepago;
    }

    public void setFecPagocomprobantepago(Timestamp fecPagocomprobantepago) {
        this.fecPagocomprobantepago = fecPagocomprobantepago;
    }

    public Timestamp getFecRefcomprobante() {
        return this.fecRefcomprobante;
    }

    public void setFecRefcomprobante(Timestamp fecRefcomprobante) {
        this.fecRefcomprobante = fecRefcomprobante;
    }

    public Timestamp getFecRefcomprobantepago() {
        return this.fecRefcomprobantepago;
    }

    public void setFecRefcomprobantepago(Timestamp fecRefcomprobantepago) {
        this.fecRefcomprobantepago = fecRefcomprobantepago;
    }

    public Timestamp getFecRetencion() {
        return this.fecRetencion;
    }

    public void setFecRetencion(Timestamp fecRetencion) {
        this.fecRetencion = fecRetencion;
    }

    public Character getFlgChequecobrado() {
        return this.flgChequecobrado;
    }

    public void setFlgChequecobrado(Character flgChequecobrado) {
        this.flgChequecobrado = flgChequecobrado;
    }

    public Character getCodTipomoneda() {
        return codTipomoneda;
    }

    public void setCodTipomoneda(Character codTipomoneda) {
        this.codTipomoneda = codTipomoneda;
    }

    public String getCod_Tiporegistro() {
        return cod_Tiporegistro;
    }

    public void setCod_Tiporegistro(String cod_Tiporegistro) {
        this.cod_Tiporegistro = cod_Tiporegistro;
    }

    public Character getFlgConversion() {
        return flgConversion;
    }

    public void setFlgConversion(Character flgConversion) {
        this.flgConversion = flgConversion;
    }

    public Character getFlgDistribuido() {
        return flgDistribuido;
    }

    public void setFlgDistribuido(Character flgDistribuido) {
        this.flgDistribuido = flgDistribuido;
    }

    public Character getFlgDistribuir() {
        return flgDistribuir;
    }

    public void setFlgDistribuir(Character flgDistribuir) {
        this.flgDistribuir = flgDistribuir;
    }

    public Character getFlgEsactivo() {
        return flgEsactivo;
    }

    public void setFlgEsactivo(Character flgEsactivo) {
        this.flgEsactivo = flgEsactivo;
    }

    public Character getFlgIm() {
        return flgIm;
    }

    public void setFlgIm(Character flgIm) {
        this.flgIm = flgIm;
    }

    public Character getFlgRecuperaigv() {
        return flgRecuperaigv;
    }

    public void setFlgRecuperaigv(Character flgRecuperaigv) {
        this.flgRecuperaigv = flgRecuperaigv;
    }

    public Character getFlg_Retienecuarta() {
        return flg_Retienecuarta;
    }

    public void setFlg_Retienecuarta(Character flg_Retienecuarta) {
        this.flg_Retienecuarta = flg_Retienecuarta;
    }

    public Character getFlgTcreferencia() {
        return flgTcreferencia;
    }

    public void setFlgTcreferencia(Character flgTcreferencia) {
        this.flgTcreferencia = flgTcreferencia;
    }

    public BigDecimal getNumDebedolar() {
        return this.numDebedolar;
    }

    public void setNumDebedolar(BigDecimal numDebedolar) {
        this.numDebedolar = numDebedolar;
    }

    public BigDecimal getNumDebemc() {
        return this.numDebemc;
    }

    public void setNumDebemc(BigDecimal numDebemc) {
        this.numDebemc = numDebemc;
    }

    public BigDecimal getNumDebemo() {
        return this.numDebemo;
    }

    public void setNumDebemo(BigDecimal numDebemo) {
        this.numDebemo = numDebemo;
    }

    public BigDecimal getNumDebesol() {
        return this.numDebesol;
    }

    public void setNumDebesol(BigDecimal numDebesol) {
        this.numDebesol = numDebesol;
    }

    public BigDecimal getNumHaberdolar() {
        return this.numHaberdolar;
    }

    public void setNumHaberdolar(BigDecimal numHaberdolar) {
        this.numHaberdolar = numHaberdolar;
    }

    public BigDecimal getNumHabermc() {
        return this.numHabermc;
    }

    public void setNumHabermc(BigDecimal numHabermc) {
        this.numHabermc = numHabermc;
    }

    public BigDecimal getNumHabermo() {
        return this.numHabermo;
    }

    public void setNumHabermo(BigDecimal numHabermo) {
        this.numHabermo = numHabermo;
    }

    public BigDecimal getNumHabersol() {
        return this.numHabersol;
    }

    public void setNumHabersol(BigDecimal numHabersol) {
        this.numHabersol = numHabersol;
    }

    public BigDecimal getNumNroitem2() {
        return this.numNroitem2;
    }

    public void setNumNroitem2(BigDecimal numNroitem2) {
        this.numNroitem2 = numNroitem2;
    }

    public BigDecimal getNumRefnroitem() {
        return this.numRefnroitem;
    }

    public void setNumRefnroitem(BigDecimal numRefnroitem) {
        this.numRefnroitem = numRefnroitem;
    }

    public BigDecimal getNumTcmc() {
        return this.numTcmc;
    }

    public void setNumTcmc(BigDecimal numTcmc) {
        this.numTcmc = numTcmc;
    }

    public double getNumTcmo() {
        return this.numTcmo;
    }

    public void setNumTcmo(double numTcmo) {
        this.numTcmo = numTcmo;
    }

    public double getNumTcvdolar() {
        return this.numTcvdolar;
    }

    public void setNumTcvdolar(double numTcvdolar) {
        this.numTcvdolar = numTcvdolar;
    }

    public double getPorIes() {
        return this.porIes;
    }

    public void setPorIes(double porIes) {
        this.porIes = porIes;
    }

    public double getPorIgv() {
        return this.porIgv;
    }

    public void setPorIgv(double porIgv) {
        this.porIgv = porIgv;
    }

    public String getTxtCheque() {
        return this.txtCheque;
    }

    public void setTxtCheque(String txtCheque) {
        this.txtCheque = txtCheque;
    }

    public String getTxtComprobantepago() {
        return this.txtComprobantepago;
    }

    public void setTxtComprobantepago(String txtComprobantepago) {
        this.txtComprobantepago = txtComprobantepago;
    }

    public String getTxtGlosaitem() {
        return this.txtGlosaitem;
    }

    public void setTxtGlosaitem(String txtGlosaitem) {
        this.txtGlosaitem = txtGlosaitem;
    }

    public String getTxt_Nrocompsujnodomi() {
        return this.txt_Nrocompsujnodomi;
    }

    public void setTxt_Nrocompsujnodomi(String txt_Nrocompsujnodomi) {
        this.txt_Nrocompsujnodomi = txt_Nrocompsujnodomi;
    }

    public String getTxtNroretencion() {
        return this.txtNroretencion;
    }

    public void setTxtNroretencion(String txtNroretencion) {
        this.txtNroretencion = txtNroretencion;
    }

    public String getTxtRefcomprobantepago() {
        return this.txtRefcomprobantepago;
    }

    public void setTxtRefcomprobantepago(String txtRefcomprobantepago) {
        this.txtRefcomprobantepago = txtRefcomprobantepago;
    }

    public String getTxtRefseriecomprobantepago() {
        return this.txtRefseriecomprobantepago;
    }

    public void setTxtRefseriecomprobantepago(String txtRefseriecomprobantepago) {
        this.txtRefseriecomprobantepago = txtRefseriecomprobantepago;
    }

    public String getTxtSeriecomprobantepago() {
        return this.txtSeriecomprobantepago;
    }

    public void setTxtSeriecomprobantepago(String txtSeriecomprobantepago) {
        this.txtSeriecomprobantepago = txtSeriecomprobantepago;
    }


//    public ScpDestino getScpDestino() {
//        return scpDestino;
//    }
//
//    public void setScpDestino(ScpDestino scpDestino) {
//        this.scpDestino = scpDestino;
//    }
}