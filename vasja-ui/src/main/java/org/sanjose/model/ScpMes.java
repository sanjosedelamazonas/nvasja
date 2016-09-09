package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the scp_meses database table.
 * 
 */
@Entity
@Table(name="scp_meses")
@NamedQuery(name="ScpMes.findAll", query="SELECT s FROM ScpMes s")
public class ScpMes implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cod_mes")
	private String codMes;

	@Column(name="txt_descripcion")
	private String txtDescripcion;

	public ScpMes() {
	}

	public String getCodMes() {
		return this.codMes;
	}

	public void setCodMes(String codMes) {
		this.codMes = codMes;
	}

	public String getTxtDescripcion() {
		return this.txtDescripcion;
	}

	public void setTxtDescripcion(String txtDescripcion) {
		this.txtDescripcion = txtDescripcion;
	}

}