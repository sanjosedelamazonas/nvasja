package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the scp_planproyecto database table.
 * 
 */
@Embeddable
public class ScpPlanproyectoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="cod_ctaproyecto")
	private String codCtaproyecto;

	public ScpPlanproyectoPK() {
	}
	public String getTxtAnoproceso() {
		return this.txtAnoproceso;
	}
	public void setTxtAnoproceso(String txtAnoproceso) {
		this.txtAnoproceso = txtAnoproceso;
	}
	public String getCodProyecto() {
		return this.codProyecto;
	}
	public void setCodProyecto(String codProyecto) {
		this.codProyecto = codProyecto;
	}
	public String getCodCtaproyecto() {
		return this.codCtaproyecto;
	}
	public void setCodCtaproyecto(String codCtaproyecto) {
		this.codCtaproyecto = codCtaproyecto;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScpPlanproyectoPK)) {
			return false;
		}
		ScpPlanproyectoPK castOther = (ScpPlanproyectoPK)other;
		return 
			this.txtAnoproceso.equals(castOther.txtAnoproceso)
			&& this.codProyecto.equals(castOther.codProyecto)
			&& this.codCtaproyecto.equals(castOther.codCtaproyecto);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.txtAnoproceso.hashCode();
		hash = hash * prime + this.codProyecto.hashCode();
		hash = hash * prime + this.codCtaproyecto.hashCode();
		
		return hash;
	}
}