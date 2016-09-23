package org.sanjose.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the vsj_configuracioncaja database table.
 * 
 */
@Entity
@Table(name="vsj_configuracioncaja")
@NamedQuery(name="VsjConfiguracioncaja.findAll", query="SELECT v FROM VsjConfiguracioncaja v")
public class VsjConfiguracioncaja implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_configuracion")
	private Integer codConfiguracion;

	@Column(name="cod_categoriaproyecto")
	private String codCategoriaproyecto;

	@Column(name="cod_ctacontable")
	private String codCtacontable;

	@Column(name="cod_destino")
	private String codDestino;

	@Column(name="cod_proyecto")
	private String codProyecto;

	@Column(name="ind_tipomoneda")
	private Character indTipomoneda;

	@Column(name="txt_configuracion")
	private String txtConfiguracion;

	public VsjConfiguracioncaja() {
	}

	public Integer getCodConfiguracion() {
		return this.codConfiguracion;
	}

	public void setCodConfiguracion(Integer codConfiguracion) {
		this.codConfiguracion = codConfiguracion;
	}

	public String getCodCategoriaproyecto() {
		return this.codCategoriaproyecto;
	}

	public void setCodCategoriaproyecto(String codCategoriaproyecto) {
		this.codCategoriaproyecto = codCategoriaproyecto;
	}

	public String getCodCtacontable() {
		return this.codCtacontable;
	}

	public void setCodCtacontable(String codCtacontable) {
		this.codCtacontable = codCtacontable;
	}

	public String getCodDestino() {
		return this.codDestino;
	}

	public void setCodDestino(String codDestino) {
		this.codDestino = codDestino;
	}

	public String getCodProyecto() {
		return this.codProyecto;
	}

	public void setCodProyecto(String codProyecto) {
		this.codProyecto = codProyecto;
	}

	public Character getIndTipomoneda() {
		return this.indTipomoneda;
	}

	public void setIndTipomoneda(Character indTipomoneda) {
		this.indTipomoneda = indTipomoneda;
	}

	public String getTxtConfiguracion() {
		return this.txtConfiguracion;
	}

	public void setTxtConfiguracion(String txtConfiguracion) {
		this.txtConfiguracion = txtConfiguracion;
	}

}