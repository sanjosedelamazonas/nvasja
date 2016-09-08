package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_proyecto database table.
 * 
 */
@Entity
@Table(name="scp_proyecto")
@NamedQuery(name="ScpProyecto.findAll", query="SELECT s FROM ScpProyecto s")
public class ScpProyecto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="cod_categoriaproyecto")
	private String codCategoriaproyecto;

	@Column(name="cod_ctagastodolar")
	private String codCtagastodolar;

	@Column(name="cod_ctagastoeuro")
	private String codCtagastoeuro;

	@Column(name="cod_ctagastosol")
	private String codCtagastosol;

	@Column(name="cod_ctaingreso")
	private String codCtaingreso;

	@Column(name="cod_tipomoneda")
	private String codTipomoneda;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_final")
	private Timestamp fecFinal;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="fec_inicio")
	private Timestamp fecInicio;

	@Column(name="flg_conversion")
	private String flgConversion;

	@Column(name="flg_im")
	private String flgIm;

	private String flg_TCReferencia;

	@Column(name="num_presupuesto")
	private BigDecimal numPresupuesto;

	@Column(name="txt_descproyecto")
	private String txtDescproyecto;

	@Column(name="txt_responsable")
	private String txtResponsable;

	@Column(name="txt_sigla")
	private String txtSigla;

	public ScpProyecto() {
	}

	public String getCodProyecto() {
		return this.codProyecto;
	}

	public void setCodProyecto(String codProyecto) {
		this.codProyecto = codProyecto;
	}

	public String getCodCategoriaproyecto() {
		return this.codCategoriaproyecto;
	}

	public void setCodCategoriaproyecto(String codCategoriaproyecto) {
		this.codCategoriaproyecto = codCategoriaproyecto;
	}

	public String getCodCtagastodolar() {
		return this.codCtagastodolar;
	}

	public void setCodCtagastodolar(String codCtagastodolar) {
		this.codCtagastodolar = codCtagastodolar;
	}

	public String getCodCtagastoeuro() {
		return this.codCtagastoeuro;
	}

	public void setCodCtagastoeuro(String codCtagastoeuro) {
		this.codCtagastoeuro = codCtagastoeuro;
	}

	public String getCodCtagastosol() {
		return this.codCtagastosol;
	}

	public void setCodCtagastosol(String codCtagastosol) {
		this.codCtagastosol = codCtagastosol;
	}

	public String getCodCtaingreso() {
		return this.codCtaingreso;
	}

	public void setCodCtaingreso(String codCtaingreso) {
		this.codCtaingreso = codCtaingreso;
	}

	public String getCodTipomoneda() {
		return this.codTipomoneda;
	}

	public void setCodTipomoneda(String codTipomoneda) {
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

	public Timestamp getFecFinal() {
		return this.fecFinal;
	}

	public void setFecFinal(Timestamp fecFinal) {
		this.fecFinal = fecFinal;
	}

	public Timestamp getFecFregistro() {
		return this.fecFregistro;
	}

	public void setFecFregistro(Timestamp fecFregistro) {
		this.fecFregistro = fecFregistro;
	}

	public Timestamp getFecInicio() {
		return this.fecInicio;
	}

	public void setFecInicio(Timestamp fecInicio) {
		this.fecInicio = fecInicio;
	}

	public String getFlgConversion() {
		return this.flgConversion;
	}

	public void setFlgConversion(String flgConversion) {
		this.flgConversion = flgConversion;
	}

	public String getFlgIm() {
		return this.flgIm;
	}

	public void setFlgIm(String flgIm) {
		this.flgIm = flgIm;
	}

	public String getFlg_TCReferencia() {
		return this.flg_TCReferencia;
	}

	public void setFlg_TCReferencia(String flg_TCReferencia) {
		this.flg_TCReferencia = flg_TCReferencia;
	}

	public BigDecimal getNumPresupuesto() {
		return this.numPresupuesto;
	}

	public void setNumPresupuesto(BigDecimal numPresupuesto) {
		this.numPresupuesto = numPresupuesto;
	}

	public String getTxtDescproyecto() {
		return this.txtDescproyecto;
	}

	public void setTxtDescproyecto(String txtDescproyecto) {
		this.txtDescproyecto = txtDescproyecto;
	}

	public String getTxtResponsable() {
		return this.txtResponsable;
	}

	public void setTxtResponsable(String txtResponsable) {
		this.txtResponsable = txtResponsable;
	}

	public String getTxtSigla() {
		return this.txtSigla;
	}

	public void setTxtSigla(String txtSigla) {
		this.txtSigla = txtSigla;
	}

}