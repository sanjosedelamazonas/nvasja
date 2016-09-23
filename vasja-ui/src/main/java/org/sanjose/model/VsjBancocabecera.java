package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the vsj_bancocabecera database table.
 * 
 */
@Entity
@Table(name="vsj_bancocabecera")
@NamedQuery(name="VsjBancocabecera.findAll", query="SELECT v FROM VsjBancocabecera v")
public class VsjBancocabecera implements Serializable, IVsjBancoItem {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_bancocabecera")
	private Integer codBancocabecera;

	@Column(name="cod_comprobanteenlace")
	private String codComprobanteenlace;

	@Column(name="cod_ctacontable")
	private String codCtacontable;

	@Column(name="cod_destino")
	private String codDestino;

	@Column(name="cod_mes")
	private String codMes;

	@Column(name="cod_origenenlace")
	private String codOrigenenlace;

	@Column(name="cod_tipomoneda")
	private Character codTipomoneda;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fecha")
	private Timestamp fecFecha;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="flg_enviado")
	private Character flgEnviado;

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

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Column(name="txt_cheque")
	private String txtCheque;

	@Column(name="txt_correlativo")
	private String txtCorrelativo;

	@Column(name="txt_glosa")
	private String txtGlosa;

	//bi-directional many-to-one association to VsjBancodetalle
	@OneToMany(mappedBy="vsjBancocabecera")
	private List<VsjBancodetalle> vsjBancodetalles;

	public VsjBancocabecera() {
	}

	public Integer getCodBancocabecera() {
		return this.codBancocabecera;
	}

	public void setCodBancocabecera(Integer codBancocabecera) {
		this.codBancocabecera = codBancocabecera;
	}

	public String getCodComprobanteenlace() {
		return this.codComprobanteenlace;
	}

	public void setCodComprobanteenlace(String codComprobanteenlace) {
		this.codComprobanteenlace = codComprobanteenlace;
	}

	public String getCodCtacontable() {
		return this.codCtacontable;
	}

	public void setCodCtacontable(String codCtacontable) {
		this.codCtacontable = codCtacontable;
	}

	public String getCodDestino() {
		return this.codDestino;
	}

	public void setCodDestino(String codDestino) {
		this.codDestino = codDestino;
	}

	public String getCodMes() {
		return this.codMes;
	}

	public void setCodMes(String codMes) {
		this.codMes = codMes;
	}

	public String getCodOrigenenlace() {
		return this.codOrigenenlace;
	}

	public void setCodOrigenenlace(String codOrigenenlace) {
		this.codOrigenenlace = codOrigenenlace;
	}

	public Character getCodTipomoneda() {
		return this.codTipomoneda;
	}

	public void setCodTipomoneda(Character codTipomoneda) {
		this.codTipomoneda = codTipomoneda;
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

	public Character getFlgEnviado() {
		return this.flgEnviado;
	}

	public void setFlgEnviado(Character flgEnviado) {
		this.flgEnviado = flgEnviado;
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

	public String getTxtCorrelativo() {
		return this.txtCorrelativo;
	}

	public void setTxtCorrelativo(String txtCorrelativo) {
		this.txtCorrelativo = txtCorrelativo;
	}

	public String getTxtGlosa() {
		return this.txtGlosa;
	}

	public void setTxtGlosa(String txtGlosa) {
		this.txtGlosa = txtGlosa;
	}

	public List<VsjBancodetalle> getVsjBancodetalles() {
		return this.vsjBancodetalles;
	}

	public void setVsjBancodetalles(List<VsjBancodetalle> vsjBancodetalles) {
		this.vsjBancodetalles = vsjBancodetalles;
	}

	public VsjBancodetalle addVsjBancodetalle(VsjBancodetalle vsjBancodetalle) {
		getVsjBancodetalles().add(vsjBancodetalle);
		vsjBancodetalle.setVsjBancocabecera(this);

		return vsjBancodetalle;
	}

	public VsjBancodetalle removeVsjBancodetalle(VsjBancodetalle vsjBancodetalle) {
		getVsjBancodetalles().remove(vsjBancodetalle);
		vsjBancodetalle.setVsjBancocabecera(null);

		return vsjBancodetalle;
	}

}