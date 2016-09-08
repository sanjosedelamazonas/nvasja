package org.sanjose.vasja;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the scp_planespecial database table.
 * 
 */
@Embeddable
public class ScpPlanespecialPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Column(name="cod_ctaespecial")
	private String codCtaespecial;

	public ScpPlanespecialPK() {
	}
	public String getTxtAnoproceso() {
		return this.txtAnoproceso;
	}
	public void setTxtAnoproceso(String txtAnoproceso) {
		this.txtAnoproceso = txtAnoproceso;
	}
	public String getCodCtaespecial() {
		return this.codCtaespecial;
	}
	public void setCodCtaespecial(String codCtaespecial) {
		this.codCtaespecial = codCtaespecial;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScpPlanespecialPK)) {
			return false;
		}
		ScpPlanespecialPK castOther = (ScpPlanespecialPK)other;
		return 
			this.txtAnoproceso.equals(castOther.txtAnoproceso)
			&& this.codCtaespecial.equals(castOther.codCtaespecial);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.txtAnoproceso.hashCode();
		hash = hash * prime + this.codCtaespecial.hashCode();
		
		return hash;
	}
}