package org.sanjose.vasja;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the scp_presupuestoproyecto database table.
 * 
 */
@Embeddable
public class ScpPresupuestoproyectoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="txt_anoproceso")
	private String txtAnoproceso;

	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="cod_ctaproyecto")
	private String codCtaproyecto;

	@Column(name="cod_financiera")
	private String codFinanciera;

	@Column(name="cod_contraparte")
	private String codContraparte;

	@Column(name="ind_tipoaporte")
	private String indTipoaporte;

	@Column(name="cod_tipomoneda")
	private String codTipomoneda;

	public ScpPresupuestoproyectoPK() {
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
	public String getCodFinanciera() {
		return this.codFinanciera;
	}
	public void setCodFinanciera(String codFinanciera) {
		this.codFinanciera = codFinanciera;
	}
	public String getCodContraparte() {
		return this.codContraparte;
	}
	public void setCodContraparte(String codContraparte) {
		this.codContraparte = codContraparte;
	}
	public String getIndTipoaporte() {
		return this.indTipoaporte;
	}
	public void setIndTipoaporte(String indTipoaporte) {
		this.indTipoaporte = indTipoaporte;
	}
	public String getCodTipomoneda() {
		return this.codTipomoneda;
	}
	public void setCodTipomoneda(String codTipomoneda) {
		this.codTipomoneda = codTipomoneda;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScpPresupuestoproyectoPK)) {
			return false;
		}
		ScpPresupuestoproyectoPK castOther = (ScpPresupuestoproyectoPK)other;
		return 
			this.txtAnoproceso.equals(castOther.txtAnoproceso)
			&& this.codProyecto.equals(castOther.codProyecto)
			&& this.codCtaproyecto.equals(castOther.codCtaproyecto)
			&& this.codFinanciera.equals(castOther.codFinanciera)
			&& this.codContraparte.equals(castOther.codContraparte)
			&& this.indTipoaporte.equals(castOther.indTipoaporte)
			&& this.codTipomoneda.equals(castOther.codTipomoneda);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.txtAnoproceso.hashCode();
		hash = hash * prime + this.codProyecto.hashCode();
		hash = hash * prime + this.codCtaproyecto.hashCode();
		hash = hash * prime + this.codFinanciera.hashCode();
		hash = hash * prime + this.codContraparte.hashCode();
		hash = hash * prime + this.indTipoaporte.hashCode();
		hash = hash * prime + this.codTipomoneda.hashCode();
		
		return hash;
	}
}