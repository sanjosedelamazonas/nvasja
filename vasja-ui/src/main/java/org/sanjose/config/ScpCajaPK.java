package org.sanjose.config;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the scp_caja database table.
 * 
 */
@Embeddable
public class ScpCajaPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Column(name="cod_mes")
	private String codMes;

	@Column(name="cod_dia")
	private String codDia;

	@Column(name="cod_origen")
	private String codOrigen;

	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="num_nroitem")
	private long numNroitem;

	public ScpCajaPK() {
	}
	public String getTxtAnoproceso() {
		return this.txtAnoproceso;
	}
	public void setTxtAnoproceso(String txtAnoproceso) {
		this.txtAnoproceso = txtAnoproceso;
	}
	public String getCodMes() {
		return this.codMes;
	}
	public void setCodMes(String codMes) {
		this.codMes = codMes;
	}
	public String getCodDia() {
		return this.codDia;
	}
	public void setCodDia(String codDia) {
		this.codDia = codDia;
	}
	public String getCodOrigen() {
		return this.codOrigen;
	}
	public void setCodOrigen(String codOrigen) {
		this.codOrigen = codOrigen;
	}
	public String getCodProyecto() {
		return this.codProyecto;
	}
	public void setCodProyecto(String codProyecto) {
		this.codProyecto = codProyecto;
	}
	public long getNumNroitem() {
		return this.numNroitem;
	}
	public void setNumNroitem(long numNroitem) {
		this.numNroitem = numNroitem;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScpCajaPK)) {
			return false;
		}
		ScpCajaPK castOther = (ScpCajaPK)other;
		return 
			this.txtAnoproceso.equals(castOther.txtAnoproceso)
			&& this.codMes.equals(castOther.codMes)
			&& this.codDia.equals(castOther.codDia)
			&& this.codOrigen.equals(castOther.codOrigen)
			&& this.codProyecto.equals(castOther.codProyecto)
			&& (this.numNroitem == castOther.numNroitem);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.txtAnoproceso.hashCode();
		hash = hash * prime + this.codMes.hashCode();
		hash = hash * prime + this.codDia.hashCode();
		hash = hash * prime + this.codOrigen.hashCode();
		hash = hash * prime + this.codProyecto.hashCode();
		hash = hash * prime + ((int) (this.numNroitem ^ (this.numNroitem >>> 32)));
		
		return hash;
	}
}