package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_Contraparte database table.
 * 
 */
@Entity
@Table(name="scp_Contraparte")
@NamedQuery(name="Scp_Contraparte.findAll", query="SELECT s FROM Scp_Contraparte s")
public class Scp_Contraparte implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_contraparte")
	private String codContraparte;

	@Column(name="cod_categorialugargasto")
	private Character codCategorialugargasto;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="flg_im")
	private Character flgIm;

	@Column(name="txt_desccontraparte")
	private String txtDescContraparte;

	@Column(name="txt_direccion")
	private String txtDireccion;

	@Column(name="txt_telefono1")
	private String txtTelefono1;

	@Column(name="txt_telefono2")
	private String txtTelefono2;

	public Scp_Contraparte() {
	}

	public String getCodContraparte() {
		return this.codContraparte;
	}

	public void setCodContraparte(String codContraparte) {
		this.codContraparte = codContraparte;
	}

	public Character getCodCategorialugargasto() {
		return this.codCategorialugargasto;
	}

	public void setCodCategorialugargasto(Character codCategorialugargasto) {
		this.codCategorialugargasto = codCategorialugargasto;
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

	public Character getFlgIm() {
		return this.flgIm;
	}

	public void setFlgIm(Character flgIm) {
		this.flgIm = flgIm;
	}

	public String getTxtDescContraparte() {
		return this.txtDescContraparte;
	}

	public void setTxtDescContraparte(String txtDescContraparte) {
		this.txtDescContraparte = txtDescContraparte;
	}

	public String getTxtDireccion() {
		return this.txtDireccion;
	}

	public void setTxtDireccion(String txtDireccion) {
		this.txtDireccion = txtDireccion;
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