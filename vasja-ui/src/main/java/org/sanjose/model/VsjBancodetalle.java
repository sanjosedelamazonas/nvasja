package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the vsj_bancodetalle database table.
 * 
 */
@Entity
@Table(name="vsj_bancodetalle")
@NamedQuery(name="VsjBancodetalle.findAll", query="SELECT v FROM VsjBancodetalle v")
public class VsjBancodetalle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_bancodetalle")
	private Integer codBancodetalle;

	@Column(name="cod_contracta")
	private String codContracta;

	@Column(name="cod_contraparte")
	private String codContraparte;

	@Column(name="cod_ctacontable")
	private String codCtacontable;

	@Column(name="cod_ctaespecial")
	private String codCtaespecial;

	@Column(name="cod_ctaproyecto")
	private String codCtaproyecto;

	@Column(name="cod_destino")
	private String codDestino;

	@Column(name="cod_destinoitem")
	private String codDestinoitem;

	@Column(name="cod_financiera")
	private String codFinanciera;

	@Column(name="cod_formapago")
	private String codFormapago;

	@Column(name="cod_mes")
	private String codMes;

	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="cod_tercero")
	private String codTercero;

	@Column(name="cod_tipocomprobantepago")
	private String codTipocomprobantepago;

	@Column(name="cod_tipogasto")
	private String codTipogasto;

	@Column(name="cod_tipoingreso")
	private String codTipoingreso;

	@Column(name="cod_tipomoneda")
	private Character codTipomoneda;

	@Column(name="cod_tipomov")
	private int codTipomov;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_comprobantepago")
	private Timestamp fecComprobantepago;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fecha")
	private Timestamp fecFecha;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	private Character flg_Anula;

	@Column(name="flg_im")
	private Character flgIm;

	@Column(name="flg_saldo")
	private Character flgSaldo;

	@Column(name="ind_tipocuenta")
	private Character indTipocuenta;

	@Column(name="num_debedolar", columnDefinition="decimal(12,2)")
	private BigDecimal numDebedolar;

	@Column(name="num_debemo", columnDefinition="decimal(12,2)")
	private BigDecimal numDebemo;

	@Column(name="num_debesol", columnDefinition="decimal(12,2)")
	private BigDecimal numDebesol;

	@Column(name="num_haberdolar", columnDefinition="decimal(12,2)")
	private BigDecimal numHaberdolar;

	@Column(name="num_habermo", columnDefinition="decimal(12,2)")
	private BigDecimal numHabermo;

	@Column(name="num_habersol", columnDefinition="decimal(12,2)")
	private BigDecimal numHabersol;

	@Column(name="num_saldodolar", columnDefinition="decimal(12,2)")
	private BigDecimal numSaldodolar;

	@Column(name="num_saldomo", columnDefinition="decimal(12,2)")
	private BigDecimal numSaldomo;

	@Column(name="num_saldosol", columnDefinition="decimal(12,2)")
	private BigDecimal numSaldosol;

	@Column(name="num_tcmo")
	private double numTcmo;

	@Column(name="num_tcvdolar", columnDefinition="decimal(12,2)")
	private BigDecimal numTcvdolar;

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Column(name="txt_cheque")
	private String txtCheque;

	@Column(name="txt_comprobantepago")
	private String txtComprobantepago;

	@Column(name="txt_correlativo")
	private String txtCorrelativo;

	@Column(name="txt_detallepago")
	private String txtDetallepago;

	@Column(name="txt_glosaitem")
	private String txtGlosaitem;

	@Column(name="txt_seriecomprobantepago")
	private String txtSeriecomprobantepago;

	//bi-directional many-to-one association to VsjBancocabecera
	@ManyToOne
	@JoinColumn(name="cod_bancocabecera")
	private VsjBancocabecera vsjBancocabecera;

	public VsjBancodetalle() {
	}

	public Integer getCodBancodetalle() {
		return this.codBancodetalle;
	}

	public void setCodBancodetalle(Integer codBancodetalle) {
		this.codBancodetalle = codBancodetalle;
	}

	public String getCodContracta() {
		return this.codContracta;
	}

	public void setCodContracta(String codContracta) {
		this.codContracta = codContracta;
	}

	public String getCodContraparte() {
		return this.codContraparte;
	}

	public void setCodContraparte(String codContraparte) {
		this.codContraparte = codContraparte;
	}

	public String getCodCtacontable() {
		return this.codCtacontable;
	}

	public void setCodCtacontable(String codCtacontable) {
		this.codCtacontable = codCtacontable;
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

	public String getCodDestino() {
		return this.codDestino;
	}

	public void setCodDestino(String codDestino) {
		this.codDestino = codDestino;
	}

	public String getCodDestinoitem() {
		return this.codDestinoitem;
	}

	public void setCodDestinoitem(String codDestinoitem) {
		this.codDestinoitem = codDestinoitem;
	}

	public String getCodFinanciera() {
		return this.codFinanciera;
	}

	public void setCodFinanciera(String codFinanciera) {
		this.codFinanciera = codFinanciera;
	}

	public String getCodFormapago() {
		return this.codFormapago;
	}

	public void setCodFormapago(String codFormapago) {
		this.codFormapago = codFormapago;
	}

	public String getCodMes() {
		return this.codMes;
	}

	public void setCodMes(String codMes) {
		this.codMes = codMes;
	}

	public String getCodProyecto() {
		return this.codProyecto;
	}

	public void setCodProyecto(String codProyecto) {
		this.codProyecto = codProyecto;
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

	public String getCodTipogasto() {
		return this.codTipogasto;
	}

	public void setCodTipogasto(String codTipogasto) {
		this.codTipogasto = codTipogasto;
	}

	public String getCodTipoingreso() {
		return this.codTipoingreso;
	}

	public void setCodTipoingreso(String codTipoingreso) {
		this.codTipoingreso = codTipoingreso;
	}

	public Character getCodTipomoneda() {
		return this.codTipomoneda;
	}

	public void setCodTipomoneda(Character codTipomoneda) {
		this.codTipomoneda = codTipomoneda;
	}

	public int getCodTipomov() {
		return this.codTipomov;
	}

	public void setCodTipomov(int codTipomov) {
		this.codTipomov = codTipomov;
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

	public Timestamp getFecFecha() {
		return this.fecFecha;
	}

	public void setFecFecha(Timestamp fecFecha) {
		this.fecFecha = fecFecha;
	}

	public Timestamp getFecFregistro() {
		return this.fecFregistro;
	}

	public void setFecFregistro(Timestamp fecFregistro) {
		this.fecFregistro = fecFregistro;
	}

	public Character getFlg_Anula() {
		return this.flg_Anula;
	}

	public void setFlg_Anula(Character flg_Anula) {
		this.flg_Anula = flg_Anula;
	}

	public Character getFlgIm() {
		return this.flgIm;
	}

	public void setFlgIm(Character flgIm) {
		this.flgIm = flgIm;
	}

	public Character getFlgSaldo() {
		return this.flgSaldo;
	}

	public void setFlgSaldo(Character flgSaldo) {
		this.flgSaldo = flgSaldo;
	}

	public Character getIndTipocuenta() {
		return this.indTipocuenta;
	}

	public void setIndTipocuenta(Character indTipocuenta) {
		this.indTipocuenta = indTipocuenta;
	}

	public BigDecimal getNumDebedolar() {
		return this.numDebedolar;
	}

	public void setNumDebedolar(BigDecimal numDebedolar) {
		this.numDebedolar = numDebedolar;
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

	public BigDecimal getNumSaldodolar() {
		return this.numSaldodolar;
	}

	public void setNumSaldodolar(BigDecimal numSaldodolar) {
		this.numSaldodolar = numSaldodolar;
	}

	public BigDecimal getNumSaldomo() {
		return this.numSaldomo;
	}

	public void setNumSaldomo(BigDecimal numSaldomo) {
		this.numSaldomo = numSaldomo;
	}

	public BigDecimal getNumSaldosol() {
		return this.numSaldosol;
	}

	public void setNumSaldosol(BigDecimal numSaldosol) {
		this.numSaldosol = numSaldosol;
	}

	public double getNumTcmo() {
		return this.numTcmo;
	}

	public void setNumTcmo(double numTcmo) {
		this.numTcmo = numTcmo;
	}

	public BigDecimal getNumTcvdolar() {
		return this.numTcvdolar;
	}

	public void setNumTcvdolar(BigDecimal numTcvdolar) {
		this.numTcvdolar = numTcvdolar;
	}

	public String getTxtAnoproceso() {
		return this.txtAnoproceso;
	}

	public void setTxtAnoproceso(String txtAnoproceso) {
		this.txtAnoproceso = txtAnoproceso;
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

	public String getTxtCorrelativo() {
		return this.txtCorrelativo;
	}

	public void setTxtCorrelativo(String txtCorrelativo) {
		this.txtCorrelativo = txtCorrelativo;
	}

	public String getTxtDetallepago() {
		return this.txtDetallepago;
	}

	public void setTxtDetallepago(String txtDetallepago) {
		this.txtDetallepago = txtDetallepago;
	}

	public String getTxtGlosaitem() {
		return this.txtGlosaitem;
	}

	public void setTxtGlosaitem(String txtGlosaitem) {
		this.txtGlosaitem = txtGlosaitem;
	}

	public String getTxtSeriecomprobantepago() {
		return this.txtSeriecomprobantepago;
	}

	public void setTxtSeriecomprobantepago(String txtSeriecomprobantepago) {
		this.txtSeriecomprobantepago = txtSeriecomprobantepago;
	}

	public VsjBancocabecera getVsjBancocabecera() {
		return this.vsjBancocabecera;
	}

	public void setVsjBancocabecera(VsjBancocabecera vsjBancocabecera) {
		this.vsjBancocabecera = vsjBancocabecera;
	}

}