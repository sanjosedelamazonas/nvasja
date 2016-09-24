package org.sanjose.model;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.util.GenUtil;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;


/**
 * The persistent class for the vsj_bancodetalle database table.
 * 
 */
@Entity
@Table(name="vsj_bancodetalle")
@NamedQuery(name="VsjBancodetalle.findAll", query="SELECT v FROM VsjBancodetalle v")
public class VsjBancodetalle extends VsjBancoItem implements Serializable{
	private static final long serialVersionUID = 1L;

	@Override
	public VsjBancodetalle prepareToSave() throws FieldGroup.CommitException {
		VsjBancodetalle item = (VsjBancodetalle)super.prepareToSave();
		if (GenUtil.strNullOrEmpty(item.getCodProyecto()) && GenUtil.strNullOrEmpty(item.getCodTercero()))
			throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");

		if (!GenUtil.strNullOrEmpty(item.getCodProyecto())) {
			item.setIndTipocuenta('0');
		} else {
			item.setIndTipocuenta('1');
		}
	/*	// Verify moneda and fields
		if (PEN.equals(item.getCodTipomoneda())) {
			if (GenUtil.isNullOrZero(item.getNumHabersol()) && GenUtil.isNullOrZero(item.getNumDebesol()))
				throw new FieldGroup.CommitException("Selected SOL but values are zeros or nulls");
			if (!GenUtil.isNullOrZero(item.getNumHaberdolar()) || !GenUtil.isNullOrZero(item.getNumDebedolar()))
				throw new FieldGroup.CommitException("Selected SOL but values for Dolar are not zeros or nulls");
			if (!GenUtil.isNullOrZero(item.getNumHabermo()) || !GenUtil.isNullOrZero(item.getNumDebemo()))
				throw new FieldGroup.CommitException("Selected SOL but values for EUR are not zeros or nulls");
			item.setNumHaberdolar(new BigDecimal(0.00));
			item.setNumDebedolar(new BigDecimal(0.00));
			item.setNumHabermo(new BigDecimal(0.00));
			item.setNumDebemo(new BigDecimal(0.00));
		} else if (USD.equals(item.getCodTipomoneda())) {
			if (GenUtil.isNullOrZero(item.getNumHaberdolar()) && GenUtil.isNullOrZero(item.getNumDebedolar()))
				throw new FieldGroup.CommitException("Selected USD but values are zeros or nulls");
			if (!GenUtil.isNullOrZero(item.getNumHabersol()) || !GenUtil.isNullOrZero(item.getNumDebesol()))
				throw new FieldGroup.CommitException("Selected USD but values for SOL are not zeros or nulls");
			if (!GenUtil.isNullOrZero(item.getNumHabermo()) || !GenUtil.isNullOrZero(item.getNumDebemo()))
				throw new FieldGroup.CommitException("Selected USD but values for EUR are not zeros or nulls");
			item.setNumHabersol(new BigDecimal(0.00));
			item.setNumDebesol(new BigDecimal(0.00));
			item.setNumHabermo(new BigDecimal(0.00));
			item.setNumDebemo(new BigDecimal(0.00));
		} else {
			if (GenUtil.isNullOrZero(item.getNumHabermo()) && GenUtil.isNullOrZero(item.getNumDebemo()))
				throw new FieldGroup.CommitException("Selected EUR but values are zeros or nulls");
			if (!GenUtil.isNullOrZero(item.getNumHabersol()) || !GenUtil.isNullOrZero(item.getNumDebesol()))
				throw new FieldGroup.CommitException("Selected EUR but values for SOL are not zeros or nulls");
			if (!GenUtil.isNullOrZero(item.getNumHaberdolar()) || !GenUtil.isNullOrZero(item.getNumDebedolar()))
				throw new FieldGroup.CommitException("Selected EUR but values for Dolar are not zeros or nulls");
			item.setNumHabersol(new BigDecimal(0.00));
			item.setNumDebesol(new BigDecimal(0.00));
			item.setNumHaberdolar(new BigDecimal(0.00));
			item.setNumDebedolar(new BigDecimal(0.00));
		}*/
		return item;
	}


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

	@Column(name="fec_comprobantepago")
	private Timestamp fecComprobantepago;

	private Character flg_Anula;

	@Column(name="flg_im")
	private Character flgIm;

	@Column(name="flg_saldo")
	private Character flgSaldo;

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

	@NotNull
	@Column(name="cod_tipomov")
	private Integer codTipomov;

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
/*
	public String getCodMes() {
		return this.codMes;
	}

	public void setCodMes(String codMes) {
		this.codMes = codMes;
	}*/

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

	public Integer getCodTipomov() {
		return codTipomov;
	}

	public void setCodTipomov(Integer codTipomov) {
		this.codTipomov = codTipomov;
	}

	public VsjBancocabecera getVsjBancocabecera() {
		return this.vsjBancocabecera;
	}

	public void setVsjBancocabecera(VsjBancocabecera vsjBancocabecera) {
		this.vsjBancocabecera = vsjBancocabecera;
	}

	@Override
	public String toString() {
		return "VsjBancodetalle{" + super.toString() + " " +
				"codBancodetalle=" + codBancodetalle +
				", codContracta='" + codContracta + '\'' +
				", codContraparte='" + codContraparte + '\'' +
				", codCtacontable='" + codCtacontable + '\'' +
				", codCtaespecial='" + codCtaespecial + '\'' +
				", codCtaproyecto='" + codCtaproyecto + '\'' +
				", codDestino='" + codDestino + '\'' +
				", codDestinoitem='" + codDestinoitem + '\'' +
				", codFinanciera='" + codFinanciera + '\'' +
				", codFormapago='" + codFormapago + '\'' +
				", codProyecto='" + codProyecto + '\'' +
				", codTercero='" + codTercero + '\'' +
				", codTipocomprobantepago='" + codTipocomprobantepago + '\'' +
				", codTipogasto='" + codTipogasto + '\'' +
				", codTipoingreso='" + codTipoingreso + '\'' +
				", fecComprobantepago=" + fecComprobantepago +
				", flg_Anula=" + flg_Anula +
				", flgIm=" + flgIm +
				", flgSaldo=" + flgSaldo +
				", numSaldodolar=" + numSaldodolar +
				", numSaldomo=" + numSaldomo +
				", numSaldosol=" + numSaldosol +
				", numTcmo=" + numTcmo +
				", numTcvdolar=" + numTcvdolar +
				", txtCheque='" + txtCheque + '\'' +
				", txtComprobantepago='" + txtComprobantepago + '\'' +
				", txtCorrelativo='" + txtCorrelativo + '\'' +
				", txtDetallepago='" + txtDetallepago + '\'' +
				", txtGlosaitem='" + txtGlosaitem + '\'' +
				", txtSeriecomprobantepago='" + txtSeriecomprobantepago + '\'' +
				", codTipomov=" + codTipomov +
				", vsjBancocabecera=" + vsjBancocabecera +
				'}';
	}
}