package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the view_cajabancos database table.
 * 
 */
//@Entity
//@Table(name="view_cajabancos")
//@NamedQuery(name="ViewCajabanco.findAll", query="SELECT v FROM ViewCajabanco v")
public class ViewCajabanco implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="cod_ctacontable")
	private String codCtacontable;

	@Column(name="cod_ctaespecial")
	private String codCtaespecial;

	@Column(name="cod_destino")
	private String codDestino;

	@Column(name="cod_mes")
	private String codMes;

	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="cod_tipomoneda")
	private String codTipomoneda;

	@Column(name="cta_cajabanco")
	private String ctaCajabanco;

	@Column(name="fec_comprobante")
	private Timestamp fecComprobante;

	@Column(name="flg_enviado")
	private int flgEnviado;

	@Column(name="nr_comprobante")
	private String nrComprobante;

	@Column(name="num_debedolar")
	private BigDecimal numDebedolar;

	@Column(name="num_debemo")
	private BigDecimal numDebemo;

	@Column(name="num_debesol")
	private BigDecimal numDebesol;

	@Column(name="num_haberdolar")
	private BigDecimal numHaberdolar;

	@Column(name="num_habermo")
	private BigDecimal numHabermo;

	@Column(name="num_habersol")
	private BigDecimal numHabersol;

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Column(name="txt_glosaitem")
	private String txtGlosaitem;

	public ViewCajabanco() {
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

	public String getCodProyecto() {
		return this.codProyecto;
	}

	public void setCodProyecto(String codProyecto) {
		this.codProyecto = codProyecto;
	}

	public String getCodTipomoneda() {
		return this.codTipomoneda;
	}

	public void setCodTipomoneda(String codTipomoneda) {
		this.codTipomoneda = codTipomoneda;
	}

	public String getCtaCajabanco() {
		return this.ctaCajabanco;
	}

	public void setCtaCajabanco(String ctaCajabanco) {
		this.ctaCajabanco = ctaCajabanco;
	}

	public Timestamp getFecComprobante() {
		return this.fecComprobante;
	}

	public void setFecComprobante(Timestamp fecComprobante) {
		this.fecComprobante = fecComprobante;
	}

	public int getFlgEnviado() {
		return this.flgEnviado;
	}

	public void setFlgEnviado(int flgEnviado) {
		this.flgEnviado = flgEnviado;
	}

	public String getNrComprobante() {
		return this.nrComprobante;
	}

	public void setNrComprobante(String nrComprobante) {
		this.nrComprobante = nrComprobante;
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

	public String getTxtGlosaitem() {
		return this.txtGlosaitem;
	}

	public void setTxtGlosaitem(String txtGlosaitem) {
		this.txtGlosaitem = txtGlosaitem;
	}

}