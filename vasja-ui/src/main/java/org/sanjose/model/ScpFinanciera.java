package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_financiera database table.
 * 
 */
@Entity
@Table(name="scp_financiera")
public class ScpFinanciera implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_financiera")
	private String codFinanciera;

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

	@Column(name="txt_descfinanciera")
	private String txtDescfinanciera;

	@Column(name="txt_direccion")
	private String txtDireccion;

	@Column(name="txt_sigla")
	private String txtSigla;

	@Column(name="txt_telefono1")
	private String txtTelefono1;

	@Column(name="txt_telefono2")
	private String txtTelefono2;

	public ScpFinanciera() {
	}

	public String getCodFinanciera() {
		return this.codFinanciera;
	}

	public void setCodFinanciera(String codFinanciera) {
		this.codFinanciera = codFinanciera;
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

	public String getTxtDescfinanciera() {
		return this.txtDescfinanciera;
	}

	public void setTxtDescfinanciera(String txtDescfinanciera) {
		this.txtDescfinanciera = txtDescfinanciera;
	}

	public String getTxtDireccion() {
		return this.txtDireccion;
	}

	public void setTxtDireccion(String txtDireccion) {
		this.txtDireccion = txtDireccion;
	}

	public String getTxtSigla() {
		return this.txtSigla;
	}

	public void setTxtSigla(String txtSigla) {
		this.txtSigla = txtSigla;
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