package org.sanjose.vasja;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the scp_tipocambio database table.
 * 
 */
@Embeddable
public class ScpTipocambioPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fec_fechacambio")
	private java.util.Date fecFechacambio;

	public ScpTipocambioPK() {
	}
	public String getTxtAnoproceso() {
		return this.txtAnoproceso;
	}
	public void setTxtAnoproceso(String txtAnoproceso) {
		this.txtAnoproceso = txtAnoproceso;
	}
	public java.util.Date getFecFechacambio() {
		return this.fecFechacambio;
	}
	public void setFecFechacambio(java.util.Date fecFechacambio) {
		this.fecFechacambio = fecFechacambio;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScpTipocambioPK)) {
			return false;
		}
		ScpTipocambioPK castOther = (ScpTipocambioPK)other;
		return 
			this.txtAnoproceso.equals(castOther.txtAnoproceso)
			&& this.fecFechacambio.equals(castOther.fecFechacambio);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.txtAnoproceso.hashCode();
		hash = hash * prime + this.fecFechacambio.hashCode();
		
		return hash;
	}
}