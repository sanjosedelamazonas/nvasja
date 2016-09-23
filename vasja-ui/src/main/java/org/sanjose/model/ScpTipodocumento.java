package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_tipodocumento database table.
 * 
 */
@Entity
@Table(name="scp_tipodocumento")
@NamedQuery(name="ScpTipodocumento.findAll", query="SELECT s FROM ScpTipodocumento s")
public class ScpTipodocumento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_tipodocumento")
	private String codTipodocumento;

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

	@Column(name="txt_descripcion")
	private String txtDescripcion;

	public ScpTipodocumento() {
	}

	public String getCodTipodocumento() {
		return this.codTipodocumento;
	}

	public void setCodTipodocumento(String codTipodocumento) {
		this.codTipodocumento = codTipodocumento;
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

	public String getTxtDescripcion() {
		return this.txtDescripcion;
	}

	public void setTxtDescripcion(String txtDescripcion) {
		this.txtDescripcion = txtDescripcion;
	}

}