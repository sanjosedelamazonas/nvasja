package org.sanjose.model;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the vsj_bancocabecera database table.
 * 
 */
@Entity
@Table(name="scp_bancocabecera")
@NamedQuery(name="ScpBancocabecera.findAll", query="SELECT v FROM ScpBancocabecera v")
public class ScpBancocabecera extends VsjBancoItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_bancocabecera")
	private Integer codBancocabecera;
	@Column(name="cod_comprobanteenlace")
	private String codComprobanteenlace;
	@NotNull
	@Column(name="cod_ctacontable")
	private String codCtacontable;
	@NotNull
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
	//bi-directional many-to-one association to ScpBancodetalle
	@OneToMany(mappedBy = "vsjBancocabecera")
	private List<ScpBancodetalle> vsjBancodetalles;

	@ManyToOne(targetEntity = ScpDestino.class)
	@JoinColumn(name = "cod_destino", insertable = false, updatable = false)
	private ScpDestino scpDestino;


	public ScpBancocabecera() {
		setFecFecha(new Timestamp(System.currentTimeMillis()));
		setFlgEnviado('0');
		setFlgIm('1');
		setFlgSaldo('0');
		setFlg_Anula('0');
		setFlgCobrado(false);
	}

	@Override
	public ScpBancocabecera prepareToSave() throws FieldGroup.CommitException {
		ScpBancocabecera item = (ScpBancocabecera) super.prepareToSave();
		item.setIndTipocuenta('2');
		if (item.getTxtCheque()==null) item.setTxtCheque("");
		if (item.getCodOrigenenlace()==null) item.setCodOrigenenlace("");
		if (item.getCodComprobanteenlace()==null) item.setCodComprobanteenlace("");
		if (item.getCodMescobrado()==null) item.setCodMescobrado("");
		if (item.getCodDestino()==null) item.setCodDestino("");
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

	public List<ScpBancodetalle> getScpBancodetalles() {
		return this.vsjBancodetalles;
	}

	public void setScpBancodetalles(List<ScpBancodetalle> vsjBancodetalles) {
		this.vsjBancodetalles = vsjBancodetalles;
	}

	public ScpBancodetalle addScpBancodetalle(ScpBancodetalle vsjBancodetalle) {
		getScpBancodetalles().add(vsjBancodetalle);
		vsjBancodetalle.setScpBancocabecera(this);

		return vsjBancodetalle;
	}

	public ScpBancodetalle removeScpBancodetalle(ScpBancodetalle vsjBancodetalle) {
		getScpBancodetalles().remove(vsjBancodetalle);
		vsjBancodetalle.setScpBancocabecera(null);

		return vsjBancodetalle;
	}

	public boolean isEnviado() {
		return flgEnviado!=null && flgEnviado.equals('1');
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ScpBancocabecera that = (ScpBancocabecera) o;

		return getCodBancocabecera() != null ? getCodBancocabecera().equals(that.getCodBancocabecera()) : that.getCodBancocabecera() == null;

	}

	@Override
	public int hashCode() {
		return getCodBancocabecera() != null ? getCodBancocabecera().hashCode() : 0;
	}

	@Override
	public String toString() {
		return "ScpBancocabecera{" +
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