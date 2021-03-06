package org.sanjose.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The primary key class for the vsj_bancodetalle database table.
 * 
 */
@Embeddable
public class ScpBancodetallePK implements Serializable, Cloneable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="num_item")
	private int numItem;

	//@JoinColumn(name="cod_bancocabecera")
	@Column(name="cod_bancocabecera", insertable=false, updatable=false)
	private int codBancocabecera;

	public ScpBancodetallePK() {
	}
	public int getNumItem() {
		return this.numItem;
	}
	public void setNumItem(int numItem) {
		this.numItem = numItem;
	}
	public int getCodBancocabecera() {
		return this.codBancocabecera;
	}
	public void setCodBancocabecera(int codBancocabecera) {
		this.codBancocabecera = codBancocabecera;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScpBancodetallePK)) {
			return false;
		}
		ScpBancodetallePK castOther = (ScpBancodetallePK)other;
		return 
			(this.numItem == castOther.numItem)
			&& (this.codBancocabecera == castOther.codBancocabecera);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.numItem;
		hash = hash * prime + this.codBancocabecera;
		
		return hash;
	}

	@Override
	public String toString() {
		return "ScpBancodetallePK{" +
				"numItem=" + numItem +
				", codBancocabecera=" + codBancocabecera +
				'}';
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}