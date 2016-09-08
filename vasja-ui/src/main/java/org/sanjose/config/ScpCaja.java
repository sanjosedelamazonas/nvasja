package org.sanjose.config;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_caja database table.
 * 
 */
@Entity
@Table(name="scp_caja")
//@NamedQuery(name="ScpCaja.findAll", query="SELECT s FROM ScpCaja s")
public class ScpCaja implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ScpCajaPK id;

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

	@Column(name="cod_financiera")
	private String codFinanciera;

	@Column(name="cod_operacion")
	private String codOperacion;

	@Column(name="cod_tipo")
	private String codTipo;

	@Column(name="cod_tipomoneda")
	private String codTipomoneda;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	private String flg_Anula;

	@Column(name="flg_im")
	private String flgIm;

	@Column(name="num_correlativo")
	private BigDecimal numCorrelativo;

	@Column(name="num_debedolar")
	private BigDecimal numDebedolar;

	@Column(name="num_debesol")
	private BigDecimal numDebesol;

	@Column(name="num_haberdolar")
	private BigDecimal numHaberdolar;

	@Column(name="num_habersol")
	private BigDecimal numHabersol;

	@Column(name="num_tcvdolar")
	private BigDecimal numTcvdolar;

	@Column(name="txt_cheque")
	private String txtCheque;

	@Column(name="txt_glosaitem")
	private String txtGlosaitem;

	public ScpCaja() {
	}

	public ScpCajaPK getId() {
		return this.id;
	}

	public void setId(ScpCajaPK id) {
		this.id = id;
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

	public String getCodFinanciera() {
		return this.codFinanciera;
	}

	public void setCodFinanciera(String codFinanciera) {
		this.codFinanciera = codFinanciera;
	}

	public String getCodOperacion() {
		return this.codOperacion;
	}

	public void setCodOperacion(String codOperacion) {
		this.codOperacion = codOperacion;
	}

	public String getCodTipo() {
		return this.codTipo;
	}

	public void setCodTipo(String codTipo) {
		this.codTipo = codTipo;
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

	public Timestamp getFecFregistro() {
		return this.fecFregistro;
	}

	public void setFecFregistro(Timestamp fecFregistro) {
		this.fecFregistro = fecFregistro;
	}

	public String getFlg_Anula() {
		return this.flg_Anula;
	}

	public void setFlg_Anula(String flg_Anula) {
		this.flg_Anula = flg_Anula;
	}

	public String getFlgIm() {
		return this.flgIm;
	}

	public void setFlgIm(String flgIm) {
		this.flgIm = flgIm;
	}

	public BigDecimal getNumCorrelativo() {
		return this.numCorrelativo;
	}

	public void setNumCorrelativo(BigDecimal numCorrelativo) {
		this.numCorrelativo = numCorrelativo;
	}

	public BigDecimal getNumDebedolar() {
		return this.numDebedolar;
	}

	public void setNumDebedolar(BigDecimal numDebedolar) {
		this.numDebedolar = numDebedolar;
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

	public BigDecimal getNumHabersol() {
		return this.numHabersol;
	}

	public void setNumHabersol(BigDecimal numHabersol) {
		this.numHabersol = numHabersol;
	}

	public BigDecimal getNumTcvdolar() {
		return this.numTcvdolar;
	}

	public void setNumTcvdolar(BigDecimal numTcvdolar) {
		this.numTcvdolar = numTcvdolar;
	}

	public String getTxtCheque() {
		return this.txtCheque;
	}

	public void setTxtCheque(String txtCheque) {
		this.txtCheque = txtCheque;
	}

	public String getTxtGlosaitem() {
		return this.txtGlosaitem;
	}

	public void setTxtGlosaitem(String txtGlosaitem) {
		this.txtGlosaitem = txtGlosaitem;
	}

	@Override
	public String toString() {
		return "ScpCaja{" +
				"id=" + id +
				", codContraparte='" + codContraparte + '\'' +
				", codCtacontable='" + codCtacontable + '\'' +
				", codCtaespecial='" + codCtaespecial + '\'' +
				", codCtaproyecto='" + codCtaproyecto + '\'' +
				", codDestino='" + codDestino + '\'' +
				", codFinanciera='" + codFinanciera + '\'' +
				", codOperacion='" + codOperacion + '\'' +
				", codTipo='" + codTipo + '\'' +
				", codTipomoneda='" + codTipomoneda + '\'' +
				", codUactualiza='" + codUactualiza + '\'' +
				", codUregistro='" + codUregistro + '\'' +
				", fecFactualiza=" + fecFactualiza +
				", fecFregistro=" + fecFregistro +
				", flg_Anula='" + flg_Anula + '\'' +
				", flgIm='" + flgIm + '\'' +
				", numCorrelativo=" + numCorrelativo +
				", numDebedolar=" + numDebedolar +
				", numDebesol=" + numDebesol +
				", numHaberdolar=" + numHaberdolar +
				", numHabersol=" + numHabersol +
				", numTcvdolar=" + numTcvdolar +
				", txtCheque='" + txtCheque + '\'' +
				", txtGlosaitem='" + txtGlosaitem + '\'' +
				'}';
	}
}