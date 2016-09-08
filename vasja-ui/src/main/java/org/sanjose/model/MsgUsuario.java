package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the msg_usuario database table.
 * 
 */
@Entity
@Table(name="msg_usuario")
@NamedQuery(name="MsgUsuario.findAll", query="SELECT m FROM MsgUsuario m")
public class MsgUsuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cod_usuario")
	private String codUsuario;

	@Column(name="cod_filial")
	private String codFilial;

	@Column(name="cod_rol")
	private String codRol;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="flg_autorizado")
	private String flgAutorizado;

	@Column(name="flg_cambiapassword")
	private String flgCambiapassword;

	@Column(name="flg_estado")
	private String flgEstado;

	@Column(name="txt_aplicacion")
	private String txtAplicacion;

	@Column(name="txt_correo")
	private String txtCorreo;

	@Column(name="txt_nombre")
	private String txtNombre;

	@Column(name="txt_password")
	private String txtPassword;

	@Column(name="txt_usuario")
	private String txtUsuario;

	public MsgUsuario() {
	}

	public String getCodUsuario() {
		return this.codUsuario;
	}

	public void setCodUsuario(String codUsuario) {
		this.codUsuario = codUsuario;
	}

	public String getCodFilial() {
		return this.codFilial;
	}

	public void setCodFilial(String codFilial) {
		this.codFilial = codFilial;
	}

	public String getCodRol() {
		return this.codRol;
	}

	public void setCodRol(String codRol) {
		this.codRol = codRol;
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

	public String getFlgAutorizado() {
		return this.flgAutorizado;
	}

	public void setFlgAutorizado(String flgAutorizado) {
		this.flgAutorizado = flgAutorizado;
	}

	public String getFlgCambiapassword() {
		return this.flgCambiapassword;
	}

	public void setFlgCambiapassword(String flgCambiapassword) {
		this.flgCambiapassword = flgCambiapassword;
	}

	public String getFlgEstado() {
		return this.flgEstado;
	}

	public void setFlgEstado(String flgEstado) {
		this.flgEstado = flgEstado;
	}

	public String getTxtAplicacion() {
		return this.txtAplicacion;
	}

	public void setTxtAplicacion(String txtAplicacion) {
		this.txtAplicacion = txtAplicacion;
	}

	public String getTxtCorreo() {
		return this.txtCorreo;
	}

	public void setTxtCorreo(String txtCorreo) {
		this.txtCorreo = txtCorreo;
	}

	public String getTxtNombre() {
		return this.txtNombre;
	}

	public void setTxtNombre(String txtNombre) {
		this.txtNombre = txtNombre;
	}

	public String getTxtPassword() {
		return this.txtPassword;
	}

	public void setTxtPassword(String txtPassword) {
		this.txtPassword = txtPassword;
	}

	public String getTxtUsuario() {
		return this.txtUsuario;
	}

	public void setTxtUsuario(String txtUsuario) {
		this.txtUsuario = txtUsuario;
	}

}