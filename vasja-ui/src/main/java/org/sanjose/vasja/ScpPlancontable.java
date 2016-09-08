package org.sanjose.vasja;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_plancontable database table.
 * 
 */
@Entity
@Table(name="scp_plancontable")
//@NamedQuery(name="ScpPlancontable.findAll", query="SELECT s FROM ScpPlancontable s")
@NamedQuery(name="ScpPlancontable.findAllActual", query="SELECT s FROM ScpPlancontable s WHERE s.id.txtAnoproceso = '2016'")
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
	private String flgAjuste;

	@Column(name="flg_estadocuenta")
	private String flgEstadocuenta;

	@Column(name="flg_fifo")
	private String flgFifo;

	@Column(name="flg_gasto")
	private String flgGasto;

	@Column(name="flg_im")
	private String flgIm;

	@Column(name="flg_movimiento")
	private String flgMovimiento;

	@Column(name="flg_validaproyecto")
	private String flgValidaproyecto;

	@Column(name="ind_cierrecontable")
	private String indCierrecontable;

	@Column(name="ind_ctaenlace")
	private String indCtaenlace;

	@Column(name="ind_ingreso")
	private String indIngreso;

	@Column(name="ind_tipoajuste")
	private String indTipoajuste;

	@Column(name="ind_tipocuenta")
	private String indTipocuenta;

	@Column(name="ind_tipocuentabalance")
	private String indTipocuentabalance;

	@Column(name="ind_tipomoneda")
	private String indTipomoneda;

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

	public String getFlgAjuste() {
		return this.flgAjuste;
	}

	public void setFlgAjuste(String flgAjuste) {
		this.flgAjuste = flgAjuste;
	}

	public String getFlgEstadocuenta() {
		return this.flgEstadocuenta;
	}

	public void setFlgEstadocuenta(String flgEstadocuenta) {
		this.flgEstadocuenta = flgEstadocuenta;
	}

	public String getFlgFifo() {
		return this.flgFifo;
	}

	public void setFlgFifo(String flgFifo) {
		this.flgFifo = flgFifo;
	}

	public String getFlgGasto() {
		return this.flgGasto;
	}

	public void setFlgGasto(String flgGasto) {
		this.flgGasto = flgGasto;
	}

	public String getFlgIm() {
		return this.flgIm;
	}

	public void setFlgIm(String flgIm) {
		this.flgIm = flgIm;
	}

	public String getFlgMovimiento() {
		return this.flgMovimiento;
	}

	public void setFlgMovimiento(String flgMovimiento) {
		this.flgMovimiento = flgMovimiento;
	}

	public String getFlgValidaproyecto() {
		return this.flgValidaproyecto;
	}

	public void setFlgValidaproyecto(String flgValidaproyecto) {
		this.flgValidaproyecto = flgValidaproyecto;
	}

	public String getIndCierrecontable() {
		return this.indCierrecontable;
	}

	public void setIndCierrecontable(String indCierrecontable) {
		this.indCierrecontable = indCierrecontable;
	}

	public String getIndCtaenlace() {
		return this.indCtaenlace;
	}

	public void setIndCtaenlace(String indCtaenlace) {
		this.indCtaenlace = indCtaenlace;
	}

	public String getIndIngreso() {
		return this.indIngreso;
	}

	public void setIndIngreso(String indIngreso) {
		this.indIngreso = indIngreso;
	}

	public String getIndTipoajuste() {
		return this.indTipoajuste;
	}

	public void setIndTipoajuste(String indTipoajuste) {
		this.indTipoajuste = indTipoajuste;
	}

	public String getIndTipocuenta() {
		return this.indTipocuenta;
	}

	public void setIndTipocuenta(String indTipocuenta) {
		this.indTipocuenta = indTipocuenta;
	}

	public String getIndTipocuentabalance() {
		return this.indTipocuentabalance;
	}

	public void setIndTipocuentabalance(String indTipocuentabalance) {
		this.indTipocuentabalance = indTipocuentabalance;
	}

	public String getIndTipomoneda() {
		return this.indTipomoneda;
	}

	public void setIndTipomoneda(String indTipomoneda) {
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

	@Override
	public String toString() {
		return "ScpPlancontable [id=" + id + ", codCta1=" + codCta1
				+ ", codCta2=" + codCta2 + ", codCta3=" + codCta3
				+ ", codCta4=" + codCta4 + ", codCta5=" + codCta5
				+ ", codCta6=" + codCta6 + ", codCta7=" + codCta7
				+ ", codCta8=" + codCta8 + ", codCtaenlace=" + codCtaenlace
				+ ", codDestino=" + codDestino + ", codUactualiza="
				+ codUactualiza + ", codUregistro=" + codUregistro
				+ ", fecFactualiza=" + fecFactualiza + ", fecFregistro="
				+ fecFregistro + ", flgAjuste=" + flgAjuste
				+ ", flgEstadocuenta=" + flgEstadocuenta + ", flgFifo="
				+ flgFifo + ", flgGasto=" + flgGasto + ", flgIm=" + flgIm
				+ ", flgMovimiento=" + flgMovimiento + ", flgValidaproyecto="
				+ flgValidaproyecto + ", indCierrecontable="
				+ indCierrecontable + ", indCtaenlace=" + indCtaenlace
				+ ", indIngreso=" + indIngreso + ", indTipoajuste="
				+ indTipoajuste + ", indTipocuenta=" + indTipocuenta
				+ ", indTipocuentabalance=" + indTipocuentabalance
				+ ", indTipomoneda=" + indTipomoneda + ", txtDesccta1="
				+ txtDesccta1 + ", txtDesccta2=" + txtDesccta2
				+ ", txtDesccta3=" + txtDesccta3 + ", txtDesccta4="
				+ txtDesccta4 + ", txtDesccta5=" + txtDesccta5
				+ ", txtDesccta6=" + txtDesccta6 + ", txtDesccta7="
				+ txtDesccta7 + ", txtDesccta8=" + txtDesccta8
				+ ", txtDescctacontable=" + txtDescctacontable + "]";
	}

}