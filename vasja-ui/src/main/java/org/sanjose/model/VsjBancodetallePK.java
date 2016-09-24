package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the vsj_bancodetalle database table.
 * 
 */
@Embeddable
public class VsjBancodetallePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="num_item")
	private int numItem;

	//@JoinColumn(name="cod_bancocabecera")
	@Column(name="cod_bancocabecera", insertable=false, updatable=false)
	private int codBancocabecera;

	public VsjBancodetallePK() {
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
		if (!(other instanceof VsjBancodetallePK)) {
			return false;
		}
		VsjBancodetallePK castOther = (VsjBancodetallePK)other;
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
		return "VsjBancodetallePK{" +
				"numItem=" + numItem +
				", codBancocabecera=" + codBancocabecera +
				'}';
	}
}