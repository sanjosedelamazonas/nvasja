package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the scp_ProyectoPorFinanciera database table.
 * 
 */
@Embeddable
public class Scp_ProyectoPorFinancieraPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="cod_financiera")
	private String codFinanciera;

	public Scp_ProyectoPorFinancieraPK() {
	}
	public String getCodProyecto() {
		return this.codProyecto;
	}
	public void setCodProyecto(String codProyecto) {
		this.codProyecto = codProyecto;
	}
	public String getCodFinanciera() {
		return this.codFinanciera;
	}
	public void setCodFinanciera(String codFinanciera) {
		this.codFinanciera = codFinanciera;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Scp_ProyectoPorFinancieraPK)) {
			return false;
		}
		Scp_ProyectoPorFinancieraPK castOther = (Scp_ProyectoPorFinancieraPK)other;
		return 
			this.codProyecto.equals(castOther.codProyecto)
			&& this.codFinanciera.equals(castOther.codFinanciera);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.codProyecto.hashCode();
		hash = hash * prime + this.codFinanciera.hashCode();
		
		return hash;
	}
}