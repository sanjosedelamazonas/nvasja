package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_plancontable database table.
 * 
 */
@Entity
@Table(name="scp_plancontable")
@NamedQuery(name="ScpPlancontable.findAll", query="SELECT s FROM ScpPlancontable s")
public class ScpPlancontable implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ScpPlancontablePK id;

	@Column(name="cod_cta1")
	private String codCta1;

	@Column(name="cod_cta2")
	private String codCta2;

	@Column(name="cod_cta3")
	private String codCta3;

	@Column(name="cod_cta4")
	private String codCta4;

	@Column(name="cod_cta5")
	private String codCta5;

	@Column(name="cod_cta6")
	private String codCta6;

	@Column(name="cod_cta7")
	private String codCta7;

	@Column(name="cod_cta8")
	private String codCta8;

	@Column(name="cod_ctaenlace")
	private String codCtaenlace;

	@Column(name="cod_destino")
	private String codDestino;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="flg_ajuste")
	private Character flgAjuste;

	@Column(name="flg_estadocuenta")
	private Character flgEstadocuenta;

	@Column(name="flg_fifo")
	private Character flgFifo;

	@Column(name="flg_gasto")
	private Character flgGasto;

	@Column(name="flg_im")
	private Character flgIm;

	@Column(name="flg_movimiento")
	private Character flgMovimiento;

	@Column(name="flg_validaproyecto")
	private Character flgValidaproyecto;

	@Column(name="ind_cierrecontable")
	private String indCierrecontable;

	@Column(name="ind_ctaenlace")
	private Character indCtaenlace;

	@Column(name="ind_ingreso")
	private Character indIngreso;

	@Column(name="ind_tipoajuste")
	private Character indTipoajuste;

	@Column(name="ind_tipocuenta")
	private Character indTipocuenta;

	@Column(name="ind_tipocuentabalance")
	private String indTipocuentabalance;

	@Column(name="ind_tipomoneda")
	private Character indTipomoneda;

	@Column(name="txt_desccta1")
	private String txtDesccta1;

	@Column(name="txt_desccta2")
	private String txtDesccta2;

	@Column(name="txt_desccta3")
	private String txtDesccta3;

	@Column(name="txt_desccta4")
	private String txtDesccta4;

	@Column(name="txt_desccta5")
	private String txtDesccta5;

	@Column(name="txt_desccta6")
	private String txtDesccta6;

	@Column(name="txt_desccta7")
	private String txtDesccta7;

	@Column(name="txt_desccta8")
	private String txtDesccta8;

	@Column(name="txt_descctacontable")
	private String txtDescctacontable;

	public ScpPlancontable() {
	}

	public ScpPlancontablePK getId() {
		return this.id;
	}

	public void setId(ScpPlancontablePK id) {
		this.id = id;
	}

	public String getCodCta1() {
		return this.codCta1;
	}

	public void setCodCta1(String codCta1) {
		this.codCta1 = codCta1;
	}

	public String getCodCta2() {
		return this.codCta2;
	}

	public void setCodCta2(String codCta2) {
		this.codCta2 = codCta2;
	}

	public String getCodCta3() {
		return this.codCta3;
	}

	public void setCodCta3(String codCta3) {
		this.codCta3 = codCta3;
	}

	public String getCodCta4() {
		return this.codCta4;
	}

	public void setCodCta4(String codCta4) {
		this.codCta4 = codCta4;
	}

	public String getCodCta5() {
		return this.codCta5;
	}

	public void setCodCta5(String codCta5) {
		this.codCta5 = codCta5;
	}

	public String getCodCta6() {
		return this.codCta6;
	}

	public void setCodCta6(String codCta6) {
		this.codCta6 = codCta6;
	}

	public String getCodCta7() {
		return this.codCta7;
	}

	public void setCodCta7(String codCta7) {
		this.codCta7 = codCta7;
	}

	public String getCodCta8() {
		return this.codCta8;
	}

	public void setCodCta8(String codCta8) {
		this.codCta8 = codCta8;
	}

	public String getCodCtaenlace() {
		return this.codCtaenlace;
	}

	public void setCodCtaenlace(String codCtaenlace) {
		this.codCtaenlace = codCtaenlace;
	}

	public String getCodDestino() {
		return this.codDestino;
	}

	public void setCodDestino(String codDestino) {
		this.codDestino = codDestino;
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

	public Timestamp getFecFregistro() {
		return this.fecFregistro;
	}

	public void setFecFregistro(Timestamp fecFregistro) {
		this.fecFregistro = fecFregistro;
	}

	public Character getFlgAjuste() {
		return this.flgAjuste;
	}

	public void setFlgAjuste(Character flgAjuste) {
		this.flgAjuste = flgAjuste;
	}

	public Character getFlgEstadocuenta() {
		return this.flgEstadocuenta;
	}

	public void setFlgEstadocuenta(Character flgEstadocuenta) {
		this.flgEstadocuenta = flgEstadocuenta;
	}

	public Character getFlgFifo() {
		return this.flgFifo;
	}

	public void setFlgFifo(Character flgFifo) {
		this.flgFifo = flgFifo;
	}

	public Character getFlgGasto() {
		return this.flgGasto;
	}

	public void setFlgGasto(Character flgGasto) {
		this.flgGasto = flgGasto;
	}

	public Character getFlgIm() {
		return this.flgIm;
	}

	public void setFlgIm(Character flgIm) {
		this.flgIm = flgIm;
	}

	public Character getFlgMovimiento() {
		return this.flgMovimiento;
	}

	public void setFlgMovimiento(Character flgMovimiento) {
		this.flgMovimiento = flgMovimiento;
	}

	public Character getFlgValidaproyecto() {
		return this.flgValidaproyecto;
	}

	public void setFlgValidaproyecto(Character flgValidaproyecto) {
		this.flgValidaproyecto = flgValidaproyecto;
	}

	public String getIndCierrecontable() {
		return this.indCierrecontable;
	}

	public void setIndCierrecontable(String indCierrecontable) {
		this.indCierrecontable = indCierrecontable;
	}

	public Character getIndCtaenlace() {
		return this.indCtaenlace;
	}

	public void setIndCtaenlace(Character indCtaenlace) {
		this.indCtaenlace = indCtaenlace;
	}

	public Character getIndIngreso() {
		return this.indIngreso;
	}

	public void setIndIngreso(Character indIngreso) {
		this.indIngreso = indIngreso;
	}

	public Character getIndTipoajuste() {
		return this.indTipoajuste;
	}

	public void setIndTipoajuste(Character indTipoajuste) {
		this.indTipoajuste = indTipoajuste;
	}

	public Character getIndTipocuenta() {
		return this.indTipocuenta;
	}

	public void setIndTipocuenta(Character indTipocuenta) {
		this.indTipocuenta = indTipocuenta;
	}

	public String getIndTipocuentabalance() {
		return this.indTipocuentabalance;
	}

	public void setIndTipocuentabalance(String indTipocuentabalance) {
		this.indTipocuentabalance = indTipocuentabalance;
	}

	public Character getIndTipomoneda() {
		return this.indTipomoneda;
	}

	public void setIndTipomoneda(Character indTipomoneda) {
		this.indTipomoneda = indTipomoneda;
	}

	public String getTxtDesccta1() {
		return this.txtDesccta1;
	}

	public void setTxtDesccta1(String txtDesccta1) {
		this.txtDesccta1 = txtDesccta1;
	}

	public String getTxtDesccta2() {
		return this.txtDesccta2;
	}

	public void setTxtDesccta2(String txtDesccta2) {
		this.txtDesccta2 = txtDesccta2;
	}

	public String getTxtDesccta3() {
		return this.txtDesccta3;
	}

	public void setTxtDesccta3(String txtDesccta3) {
		this.txtDesccta3 = txtDesccta3;
	}

	public String getTxtDesccta4() {
		return this.txtDesccta4;
	}

	public void setTxtDesccta4(String txtDesccta4) {
		this.txtDesccta4 = txtDesccta4;
	}

	public String getTxtDesccta5() {
		return this.txtDesccta5;
	}

	public void setTxtDesccta5(String txtDesccta5) {
		this.txtDesccta5 = txtDesccta5;
	}

	public String getTxtDesccta6() {
		return this.txtDesccta6;
	}

	public void setTxtDesccta6(String txtDesccta6) {
		this.txtDesccta6 = txtDesccta6;
	}

	public String getTxtDesccta7() {
		return this.txtDesccta7;
	}

	public void setTxtDesccta7(String txtDesccta7) {
		this.txtDesccta7 = txtDesccta7;
	}

	public String getTxtDesccta8() {
		return this.txtDesccta8;
	}

	public void setTxtDesccta8(String txtDesccta8) {
		this.txtDesccta8 = txtDesccta8;
	}

	public String getTxtDescctacontable() {
		return this.txtDescctacontable;
	}

	public void setTxtDescctacontable(String txtDescctacontable) {
		this.txtDescctacontable = txtDescctacontable;
	}

}