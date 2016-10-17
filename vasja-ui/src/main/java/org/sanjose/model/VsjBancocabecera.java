package org.sanjose.model;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
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
	@Size(max=20)
	@Column(name="txt_cheque")
	private String txtCheque;
	@NotBlank
	@Size(min=2, max=150)
	@Column(name="txt_glosa")
	private String txtGlosa;
	@Column(name="ind_cobrado")
	private Boolean flgCobrado;
	@Column(name="cod_mescobrado")
	private String codMescobrado;
	private Character flg_Anula;
	//bi-directional many-to-one association to VsjBancodetalle
	@OneToMany(mappedBy = "vsjBancocabecera")
	private List<VsjBancodetalle> vsjBancodetalles;

	@ManyToOne(targetEntity = ScpDestino.class)
	@JoinColumn(name = "cod_destino", insertable = false, updatable = false)
	private ScpDestino scpDestino;


	public VsjBancocabecera() {
		setFecFecha(new Timestamp(System.currentTimeMillis()));
		setFlgEnviado('0');
		setFlgIm('1');
		setFlgSaldo('0');
		setFlg_Anula('0');
	}

	@Override
	public VsjBancocabecera prepareToSave() throws FieldGroup.CommitException {
		VsjBancocabecera item = (VsjBancocabecera) super.prepareToSave();
		item.setIndTipocuenta('2');
		return item;
	}

	public boolean isAnula() {
		return flg_Anula != null && flg_Anula.equals('1');
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

	public Boolean getFlgCobrado() {
		return flgCobrado;
	}

	public void setFlgCobrado(Boolean flgCobrado) {
		this.flgCobrado = flgCobrado;
	}

	public String getCodMescobrado() {
		return codMescobrado;
	}

	public void setCodMescobrado(String codMescobrado) {
		this.codMescobrado = codMescobrado;
	}

	public Character getFlg_Anula() {
		return this.flg_Anula;
	}

	public void setFlg_Anula(Character flg_Anula) {
		this.flg_Anula = flg_Anula;
	}

	public ScpDestino getScpDestino() {
		return scpDestino;
	}

	public void setScpDestino(ScpDestino scpDestino) {
		this.scpDestino = scpDestino;
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

	public boolean isEnviado() {
		return flgEnviado!=null && flgEnviado.equals('1');
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VsjBancocabecera that = (VsjBancocabecera) o;

		return getCodBancocabecera() != null ? getCodBancocabecera().equals(that.getCodBancocabecera()) : that.getCodBancocabecera() == null;

	}

	@Override
	public int hashCode() {
		return getCodBancocabecera() != null ? getCodBancocabecera().hashCode() : 0;
	}

	@Override
	public String toString() {
		return "VsjBancocabecera{" +
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
				", flgCobrado=" + flgCobrado +
				", codMescobrado='" + codMescobrado + '\'' +
				'}';
	}
}