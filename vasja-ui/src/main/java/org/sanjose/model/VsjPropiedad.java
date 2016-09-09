package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the vsj_propiedad database table.
 * 
 */
@Entity
@Table(name="vsj_propiedad")
@NamedQuery(name="VsjPropiedad.findAll", query="SELECT v FROM VsjPropiedad v")
public class VsjPropiedad implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_propiedad")
	private int codPropiedad;

	private String nombre;

	private String valor;

	public VsjPropiedad() {
	}

	public int getCodPropiedad() {
		return this.codPropiedad;
	}

	public void setCodPropiedad(int codPropiedad) {
		this.codPropiedad = codPropiedad;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public VsjPropiedad(String nombre, String valor) {
		this.nombre = nombre;
		this.valor = valor;
	}

	@Override
	public String toString() {
		return "VsjPropiedad{" +
				"codPropiedad=" + codPropiedad +
				", nombre='" + nombre + '\'' +
				", valor='" + valor + '\'' +
				'}';
	}
}