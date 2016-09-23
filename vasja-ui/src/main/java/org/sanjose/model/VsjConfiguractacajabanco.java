package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the vsj_configuractacajabanco database table.
 * 
 */
@Entity
@Table(name="vsj_configuractacajabanco")
@NamedQuery(name="VsjConfiguractacajabanco.findAll", query="SELECT v FROM VsjConfiguractacajabanco v")
public class VsjConfiguractacajabanco implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_tipocuenta")
	private Integer codTipocuenta;

	private boolean activo;

	@Column(name="cod_ctacontablecaja")
	private String codCtacontablecaja;

	@Column(name="cod_ctacontablegasto")
	private String codCtacontablegasto;

	@Column(name="cod_ctaespecial")
	private String codCtaespecial;

	@Column(name="para_banco")
	private boolean paraBanco;

	@Column(name="para_caja")
	private boolean paraCaja;

	@Column(name="para_proyecto")
	private boolean paraProyecto;

	@Column(name="para_tercero")
	private boolean paraTercero;

	@Column(name="txt_tipocuenta")
	private String txtTipocuenta;

	public VsjConfiguractacajabanco() {
	}

	public Integer getCodTipocuenta() {
		return this.codTipocuenta;
	}

	public void setCodTipocuenta(Integer codTipocuenta) {
		this.codTipocuenta = codTipocuenta;
	}

	public boolean getActivo() {
		return this.activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public String getCodCtacontablecaja() {
		return this.codCtacontablecaja;
	}

	public void setCodCtacontablecaja(String codCtacontablecaja) {
		this.codCtacontablecaja = codCtacontablecaja;
	}

	public String getCodCtacontablegasto() {
		return this.codCtacontablegasto;
	}

	public void setCodCtacontablegasto(String codCtacontablegasto) {
		this.codCtacontablegasto = codCtacontablegasto;
	}

	public String getCodCtaespecial() {
		return this.codCtaespecial;
	}

	public void setCodCtaespecial(String codCtaespecial) {
		this.codCtaespecial = codCtaespecial;
	}

	public boolean getParaBanco() {
		return this.paraBanco;
	}

	public void setParaBanco(boolean paraBanco) {
		this.paraBanco = paraBanco;
	}

	public boolean getParaCaja() {
		return this.paraCaja;
	}

	public void setParaCaja(boolean paraCaja) {
		this.paraCaja = paraCaja;
	}

	public boolean getParaProyecto() {
		return this.paraProyecto;
	}

	public void setParaProyecto(boolean paraProyecto) {
		this.paraProyecto = paraProyecto;
	}

	public boolean getParaTercero() {
		return this.paraTercero;
	}

	public void setParaTercero(boolean paraTercero) {
		this.paraTercero = paraTercero;
	}

	public String getTxtTipocuenta() {
		return this.txtTipocuenta;
	}

	public void setTxtTipocuenta(String txtTipocuenta) {
		this.txtTipocuenta = txtTipocuenta;
	}

}