package org.sanjose.config;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_destino database table.
 * 
 */
@Entity
@Table(name="scp_destino")
@NamedQuery(name="ScpDestino.findAll", query="SELECT s FROM ScpDestino s")
public class ScpDestino implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cod_destino")
	private String codDestino;

	@Column(name="cod_cargo")
	private String codCargo;

	@Column(name="cod_filial")
	private String codFilial;

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

	@Column(name="ind_sexo")
	private String indSexo;

	@Column(name="ind_tipodctoidentidad")
	private String indTipodctoidentidad;

	@Column(name="ind_tipodestino")
	private String indTipodestino;

	@Column(name="ind_tipopersona")
	private String indTipopersona;

	@Column(name="txt_apellidomaterno")
	private String txtApellidomaterno;

	@Column(name="txt_apellidopaterno")
	private String txtApellidopaterno;

	@Column(name="txt_direccion")
	private String txtDireccion;

	@Column(name="txt_nombre")
	private String txtNombre;

	@Column(name="txt_nombredestino")
	private String txtNombredestino;

	@Column(name="txt_numerodctoidentidad")
	private String txtNumerodctoidentidad;

	@Column(name="txt_ruc")
	private String txtRuc;

	@Column(name="txt_telefono1")
	private String txtTelefono1;

	@Column(name="txt_telefono2")
	private String txtTelefono2;

	public ScpDestino() {
	}

	public String getCodDestino() {
		return this.codDestino;
	}

	public void setCodDestino(String codDestino) {
		this.codDestino = codDestino;
	}

	public String getCodCargo() {
		return this.codCargo;
	}

	public void setCodCargo(String codCargo) {
		this.codCargo = codCargo;
	}

	public String getCodFilial() {
		return this.codFilial;
	}

	public void setCodFilial(String codFilial) {
		this.codFilial = codFilial;
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

	public String getIndSexo() {
		return this.indSexo;
	}

	public void setIndSexo(String indSexo) {
		this.indSexo = indSexo;
	}

	public String getIndTipodctoidentidad() {
		return this.indTipodctoidentidad;
	}

	public void setIndTipodctoidentidad(String indTipodctoidentidad) {
		this.indTipodctoidentidad = indTipodctoidentidad;
	}

	public String getIndTipodestino() {
		return this.indTipodestino;
	}

	public void setIndTipodestino(String indTipodestino) {
		this.indTipodestino = indTipodestino;
	}

	public String getIndTipopersona() {
		return this.indTipopersona;
	}

	public void setIndTipopersona(String indTipopersona) {
		this.indTipopersona = indTipopersona;
	}

	public String getTxtApellidomaterno() {
		return this.txtApellidomaterno;
	}

	public void setTxtApellidomaterno(String txtApellidomaterno) {
		this.txtApellidomaterno = txtApellidomaterno;
	}

	public String getTxtApellidopaterno() {
		return this.txtApellidopaterno;
	}

	public void setTxtApellidopaterno(String txtApellidopaterno) {
		this.txtApellidopaterno = txtApellidopaterno;
	}

	public String getTxtDireccion() {
		return this.txtDireccion;
	}

	public void setTxtDireccion(String txtDireccion) {
		this.txtDireccion = txtDireccion;
	}

	public String getTxtNombre() {
		return this.txtNombre;
	}

	public void setTxtNombre(String txtNombre) {
		this.txtNombre = txtNombre;
	}

	public String getTxtNombredestino() {
		return this.txtNombredestino;
	}

	public void setTxtNombredestino(String txtNombredestino) {
		this.txtNombredestino = txtNombredestino;
	}

	public String getTxtNumerodctoidentidad() {
		return this.txtNumerodctoidentidad;
	}

	public void setTxtNumerodctoidentidad(String txtNumerodctoidentidad) {
		this.txtNumerodctoidentidad = txtNumerodctoidentidad;
	}

	public String getTxtRuc() {
		return this.txtRuc;
	}

	public void setTxtRuc(String txtRuc) {
		this.txtRuc = txtRuc;
	}

	public String getTxtTelefono1() {
		return this.txtTelefono1;
	}

	public void setTxtTelefono1(String txtTelefono1) {
		this.txtTelefono1 = txtTelefono1;
	}

	public String getTxtTelefono2() {
		return this.txtTelefono2;
	}

	public void setTxtTelefono2(String txtTelefono2) {
		this.txtTelefono2 = txtTelefono2;
	}

}