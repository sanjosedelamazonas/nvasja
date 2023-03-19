package org.sanjose.model;

import org.sanjose.authentication.CurrentUser;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The persistent class for the msg_usuario database table.
 *
 */
@Entity
@Table(name="msg_rol")
@NamedQuery(name="MsgRol.findAll", query="SELECT m FROM MsgRol m")
public class MsgRol implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	//@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "cod_rol")
	private String codRol;

	@Column(name = "txt_serie")
	private String txtSerie;

	@Column(name = "txt_descripcion")
	private String txtDescripcion;

	@Column(name = "txt_funcion")
	private String txtFuncion;

	@Column(name = "txt_observacion")
	private String txtObservacion;

	@Column(name = "cod_uactualiza")
	private String codUactualiza;

	@Column(name = "cod_uregistro")
	private String codUregistro;

	@Column(name = "fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name = "fec_fregistro")
	private Timestamp fecFregistro;

	public MsgRol() {
	}

	public String getCodRol() {
		return codRol;
	}

	public void setCodRol(String codRol) {
		this.codRol = codRol;
	}

	public String getTxtSerie() {
		return txtSerie;
	}

	public void setTxtSerie(String txtSerie) {
		this.txtSerie = txtSerie;
	}

	public String getTxtDescripcion() {
		return txtDescripcion;
	}

	public void setTxtDescripcion(String txtDescripcion) {
		this.txtDescripcion = txtDescripcion;
	}

	public String getTxtFuncion() {
		return txtFuncion;
	}

	public void setTxtFuncion(String txtFuncion) {
		this.txtFuncion = txtFuncion;
	}

	public String getTxtObservacion() {
		return txtObservacion;
	}

	public void setTxtObservacion(String txtObservacion) {
		this.txtObservacion = txtObservacion;
	}

	public String getCodUactualiza() {
		return codUactualiza;
	}

	public void setCodUactualiza(String codUactualiza) {
		this.codUactualiza = codUactualiza;
	}

	public String getCodUregistro() {
		return codUregistro;
	}

	public void setCodUregistro(String codUregistro) {
		this.codUregistro = codUregistro;
	}

	public Timestamp getFecFactualiza() {
		return fecFactualiza;
	}

	public void setFecFactualiza(Timestamp fecFactualiza) {
		this.fecFactualiza = fecFactualiza;
	}

	public Timestamp getFecFregistro() {
		return fecFregistro;
	}

	public void setFecFregistro(Timestamp fecFregistro) {
		this.fecFregistro = fecFregistro;
	}
}

