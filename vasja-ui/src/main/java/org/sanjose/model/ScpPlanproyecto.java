package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_planproyecto database table.
 * 
 */
@Entity
@Table(name="scp_planproyecto")
@NamedQuery(name="ScpPlanproyecto.findAll", query="SELECT s FROM ScpPlanproyecto s")
public class ScpPlanproyecto implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ScpPlanproyectoPK id;

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

	@Column(name="cod_ctaactividad")
	private String codCtaactividad;

	@Column(name="cod_ctaarea")
	private String codCtaarea;

	@Column(name="cod_ctacontabledebe")
	private String codCtacontabledebe;

	@Column(name="cod_ctacontablehaber")
	private String codCtacontablehaber;

	@Column(name="cod_ctaespecial")
	private String codCtaespecial;

	@Column(name="cod_rubrogasto")
	private String codRubrogasto;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="flg_im")
	private String flgIm;

	@Column(name="flg_movimiento")
	private String flgMovimiento;

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

	@Column(name="txt_descctaproyecto")
	private String txtDescctaproyecto;

	public ScpPlanproyecto() {
	}

	public ScpPlanproyectoPK getId() {
		return this.id;
	}

	public void setId(ScpPlanproyectoPK id) {
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

	public String getCodCtacontabledebe() {
		return this.codCtacontabledebe;
	}

	public void setCodCtacontabledebe(String codCtacontabledebe) {
		this.codCtacontabledebe = codCtacontabledebe;
	}

	public String getCodCtacontablehaber() {
		return this.codCtacontablehaber;
	}

	public void setCodCtacontablehaber(String codCtacontablehaber) {
		this.codCtacontablehaber = codCtacontablehaber;
	}

	public String getCodCtaespecial() {
		return this.codCtaespecial;
	}

	public void setCodCtaespecial(String codCtaespecial) {
		this.codCtaespecial = codCtaespecial;
	}

	public String getCodRubrogasto() {
		return this.codRubrogasto;
	}

	public void setCodRubrogasto(String codRubrogasto) {
		this.codRubrogasto = codRubrogasto;
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

	public String getTxtDescctaproyecto() {
		return this.txtDescctaproyecto;
	}

	public void setTxtDescctaproyecto(String txtDescctaproyecto) {
		this.txtDescctaproyecto = txtDescctaproyecto;
	}

}