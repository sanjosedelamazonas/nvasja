package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the scp_plancontable database table.
 * 
 */
@Embeddable
public class ScpPlancontablePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Column(name="cod_ctacontable")
	private String codCtacontable;

	public ScpPlancontablePK() {
	}
	public String getTxtAnoproceso() {
		return this.txtAnoproceso;
	}
	public void setTxtAnoproceso(String txtAnoproceso) {
		this.txtAnoproceso = txtAnoproceso;
	}
	public String getCodCtacontable() {
		return this.codCtacontable;
	}
	
	public void setCodCtacontable(String codCtacontable) {
		this.codCtacontable = codCtacontable;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScpPlancontablePK)) {
			return false;
		}
		ScpPlancontablePK castOther = (ScpPlancontablePK)other;
		return 
			this.txtAnoproceso.equals(castOther.txtAnoproceso)
			&& this.codCtacontable.equals(castOther.codCtacontable);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.txtAnoproceso.hashCode();
		hash = hash * prime + this.codCtacontable.hashCode();
		
		return hash;
	}
	@Override
	public String toString() {
		return "ScpPlancontablePK [txtAnoproceso=" + txtAnoproceso
				+ ", codCtacontable=" + codCtacontable + "]";
	}
	
	
}