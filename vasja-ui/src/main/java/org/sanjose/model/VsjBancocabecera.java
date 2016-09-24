package org.sanjose.model;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.util.GenUtil;

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
public class VsjBancocabecera extends VsjBancoItem implements Serializable {
	private static final long serialVersionUID = 1L;


	@Override
	public VsjBancocabecera prepareToSave() throws FieldGroup.CommitException {
		VsjBancocabecera item = (VsjBancocabecera)super.prepareToSave();
/*
		if (!GenUtil.strNullOrEmpty(item.getCodProyecto())) {
			item.setIndTipocuenta('0');
		} else {
			item.setIndTipocuenta('1');
		}
*/
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
	@Column(name="cod_bancocabecera")
	private Integer codBancocabecera;

	@Column(name="cod_comprobanteenlace")
	private String codComprobanteenlace;

	@Column(name="cod_ctacontable")
	private String codCtacontable;

	@Column(name="cod_destino")
	private String codDestino;

	@Column(name="cod_origenenlace")
	private String codOrigenenlace;

	@Column(name="flg_enviado")
	private Character flgEnviado;

	@Column(name="flg_im")
	private Character flgIm;

	@Column(name="flg_saldo")
	private Character flgSaldo;

	@Column(name="txt_cheque")
	private String txtCheque;

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

	public String getCodOrigenenlace() {
		return this.codOrigenenlace;
	}

	public void setCodOrigenenlace(String codOrigenenlace) {
		this.codOrigenenlace = codOrigenenlace;
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

	public String getTxtCheque() {
		return this.txtCheque;
	}

	public void setTxtCheque(String txtCheque) {
		this.txtCheque = txtCheque;
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

	@Override
	public String toString() {
		return "VsjBancocabecera{" + super.toString() + " " +
				"codBancocabecera=" + codBancocabecera +
				", codComprobanteenlace='" + codComprobanteenlace + '\'' +
				", codCtacontable='" + codCtacontable + '\'' +
				", codDestino='" + codDestino + '\'' +
				", codOrigenenlace='" + codOrigenenlace + '\'' +
				", flgEnviado=" + flgEnviado +
				", flgIm=" + flgIm +
				", flgSaldo=" + flgSaldo +
				", txtCheque='" + txtCheque + '\'' +
				", txtGlosa='" + txtGlosa + '\'' +
				", vsjBancodetalles=" + vsjBancodetalles +
				'}';
	}
}