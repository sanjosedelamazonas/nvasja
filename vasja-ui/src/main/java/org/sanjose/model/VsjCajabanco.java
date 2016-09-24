package org.sanjose.model;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.hibernate.validator.constraints.NotBlank;
import org.sanjose.util.GenUtil;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.sanjose.util.GenUtil.PEN;


/**
 * The persistent class for the vsj_cajabanco database table.
 * 
 */
@Entity
@Table(name="vsj_cajabanco")
@NamedQuery(name="VsjCajabanco.findAll", query="SELECT v FROM VsjCajabanco v")
public class VsjCajabanco extends VsjItem implements Serializable {
	private static final long serialVersionUID = 1L;


	@Override
	public VsjCajabanco prepareToSave() throws FieldGroup.CommitException {
		VsjCajabanco item = (VsjCajabanco)super.prepareToSave();
		if (GenUtil.strNullOrEmpty(item.getCodProyecto()) && GenUtil.strNullOrEmpty(item.getCodTercero()))
			throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");
		if (!GenUtil.strNullOrEmpty(item.getCodProyecto())) {
			item.setIndTipocuenta('0');
		} else {
			item.setIndTipocuenta('1');
		}
		// Verify moneda and fields
		if (PEN.equals(item.getCodTipomoneda())) {
			if (GenUtil.isNullOrZero(item.getNumHabersol()) && GenUtil.isNullOrZero(item.getNumDebesol()))
				throw new FieldGroup.CommitException("Selected SOL but values are zeros or nulls");
			if (!GenUtil.isNullOrZero(item.getNumHaberdolar()) || !GenUtil.isNullOrZero(item.getNumDebedolar()))
				throw new FieldGroup.CommitException("Selected SOL but values for Dolar are not zeros or nulls");
			item.setNumHaberdolar(new BigDecimal(0.00));
			item.setNumDebedolar(new BigDecimal(0.00));
		} else {
			if (GenUtil.isNullOrZero(item.getNumHaberdolar()) && GenUtil.isNullOrZero(item.getNumDebedolar()))
				throw new FieldGroup.CommitException("Selected USD but values are zeros or nulls");
			if (!GenUtil.isNullOrZero(item.getNumHabersol()) || !GenUtil.isNullOrZero(item.getNumDebesol()))
				throw new FieldGroup.CommitException("Selected USD but values for SOL are not zeros or nulls");
			item.setNumHabersol(new BigDecimal(0.00));
			item.setNumDebesol(new BigDecimal(0.00));
		}
		return item;
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_cajabanco")
	private Integer codCajabanco;

	@Column(name="cod_comprobanteenlace")
	private String codComprobanteenlace;

	@NotBlank
	@Column(name="cod_contracta")
	private String codContracta;

	@Column(name="cod_contraparte")
	private String codContraparte;

	//@NotNull
	@NotBlank
	@Column(name="cod_ctacontable")
	private String codCtacontable;

	@Column(name="cod_ctaespecial")
	private String codCtaespecial;

	@Column(name="cod_ctaproyecto")
	private String codCtaproyecto;

	//@NotNull
	@NotBlank
	@Column(name="cod_destino")
	private String codDestino;

	//@NotNull
	@NotBlank
	@Column(name="cod_destinoitem")
	private String codDestinoitem;

	@Column(name="cod_financiera")
	private String codFinanciera;

	@Column(name="cod_origenenlace")
	private String codOrigenenlace;

	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="cod_tercero")
	private String codTercero;

	@Column(name="cod_tipocomprobantepago")
	private String codTipocomprobantepago;

	@NotNull
	@Column(name="cod_tipomov")
	private Integer codTipomov;

	@Column(name="cod_transcorrelativo")
	private String codTranscorrelativo;

	@Column(name="fec_comprobantepago")
	private Timestamp fecComprobantepago;

	private Character flg_Anula;

	@Column(name="flg_enviado")
	private Character flgEnviado;

	@Column(name="num_debedolar", columnDefinition="decimal(12,2)")
	private BigDecimal numDebedolar;

	@Column(name="num_debesol", columnDefinition="decimal(12,2)")
	private BigDecimal numDebesol;

	@Column(name="num_haberdolar", columnDefinition="decimal(12,2)")
	private BigDecimal numHaberdolar;

	@Column(name="num_habersol", columnDefinition="decimal(12,2)")
	private BigDecimal numHabersol;
/*

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;
*/

	@Column(name="txt_comprobantepago")
	private String txtComprobantepago;

	@NotBlank
	@Column(name="txt_glosaitem")
	private String txtGlosaitem;

	@Column(name="txt_seriecomprobantepago")
	private String txtSeriecomprobantepago;

	public VsjCajabanco() {
	}

	public Integer getCodCajabanco() {
		return this.codCajabanco;
	}

	public void setCodCajabanco(Integer codCajabanco) {
		this.codCajabanco = codCajabanco;
	}

	public String getCodComprobanteenlace() {
		return this.codComprobanteenlace;
	}

	public void setCodComprobanteenlace(String codComprobanteenlace) {
		this.codComprobanteenlace = codComprobanteenlace;
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

/*
	public String getCodMes() {
		return this.codMes;
	}

	public void setCodMes(String codMes) {
		this.codMes = codMes;
	}
*/

	public String getCodOrigenenlace() {
		return this.codOrigenenlace;
	}

	public void setCodOrigenenlace(String codOrigenenlace) {
		this.codOrigenenlace = codOrigenenlace;
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

	public Integer getCodTipomov() {
		return this.codTipomov;
	}

	public void setCodTipomov(Integer codTipomov) {
		this.codTipomov = codTipomov;
	}

	public String getCodTranscorrelativo() {
		return this.codTranscorrelativo;
	}

	public void setCodTranscorrelativo(String codTranscorrelativo) {
		this.codTranscorrelativo = codTranscorrelativo;
	}

	public Timestamp getFecComprobantepago() {
		return this.fecComprobantepago;
	}

	public void setFecComprobantepago(Timestamp fecComprobantepago) {
		this.fecComprobantepago = fecComprobantepago;
	}


	public Character getFlg_Anula() {
		return this.flg_Anula;
	}

	public void setFlg_Anula(Character flg_Anula) {
		this.flg_Anula = flg_Anula;
	}

	public Character getFlgEnviado() {
		return this.flgEnviado;
	}

	public void setFlgEnviado(Character flgEnviado) {
		this.flgEnviado = flgEnviado;
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
/*
	public String getTxtAnoproceso() {
		return this.txtAnoproceso;
	}

	public void setTxtAnoproceso(String txtAnoproceso) {
		this.txtAnoproceso = txtAnoproceso;
	}*/

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

	public String getTxtSeriecomprobantepago() {
		return this.txtSeriecomprobantepago;
	}

	public void setTxtSeriecomprobantepago(String txtSeriecomprobantepago) {
		this.txtSeriecomprobantepago = txtSeriecomprobantepago;
	}

	public boolean isAnula() {
		return flg_Anula!=null && flg_Anula.equals('1');
	}

	public boolean isEnviado() {
		return flgEnviado!=null && flgEnviado.equals('1');
	}

	@Override
	public String toString() {
		return "VsjCajabanco{" +
				"codCajabanco=" + codCajabanco +
				", codComprobanteenlace='" + codComprobanteenlace + '\'' +
				", codContracta='" + codContracta + '\'' +
				", codContraparte='" + codContraparte + '\'' +
				", codCtacontable='" + codCtacontable + '\'' +
				", codCtaespecial='" + codCtaespecial + '\'' +
				", codCtaproyecto='" + codCtaproyecto + '\'' +
				", codDestino='" + codDestino + '\'' +
				", codDestinoitem='" + codDestinoitem + '\'' +
				", codFinanciera='" + codFinanciera + '\'' +
				", codOrigenenlace='" + codOrigenenlace + '\'' +
				", codProyecto='" + codProyecto + '\'' +
				", codTercero='" + codTercero + '\'' +
				", codTipocomprobantepago='" + codTipocomprobantepago + '\'' +
				", codTipomov=" + codTipomov +
				", codTranscorrelativo='" + codTranscorrelativo + '\'' +
				", fecComprobantepago=" + fecComprobantepago +
				", flg_Anula=" + flg_Anula +
				", flgEnviado=" + flgEnviado +
				", numDebedolar=" + numDebedolar +
				", numDebesol=" + numDebesol +
				", numHaberdolar=" + numHaberdolar +
				", numHabersol=" + numHabersol +
				", txtComprobantepago='" + txtComprobantepago + '\'' +
				", txtGlosaitem='" + txtGlosaitem + '\'' +
				", txtSeriecomprobantepago='" + txtSeriecomprobantepago + '\'' +
				'}';
	}
}