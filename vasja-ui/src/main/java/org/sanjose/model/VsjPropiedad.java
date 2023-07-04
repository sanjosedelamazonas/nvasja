package org.sanjose.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the vsj_propiedad database table.
 * 
 */
@Entity
@Table(name="vsj_propiedad")
@NamedQuery(name="VsjPropiedad.findAll", query="SELECT v FROM VsjPropiedad v")
@NamedStoredProcedureQueries(value = {
		@NamedStoredProcedureQuery(
				name = "getEnviarContabilidad",
				procedureName = "usp_scp_vsj_enviarAContabilidad",
				parameters = {
						@StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, type = Character.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, type = BigDecimal.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = BigDecimal.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = String.class)
				}
		),
		@NamedStoredProcedureQuery(
				name = "getEnviarBanco",
				procedureName = "usp_scp_vsj_enviarAContabilidadBanco",
				parameters = {
						@StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = Character.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = String.class)
				}
		),
		@NamedStoredProcedureQuery(
				name = "getEnviarRendicion",
				procedureName = "usp_scp_vsj_enviarAContabilidadRendicion",
				parameters = {
						@StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = Character.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = String.class)
				}
		),
		@NamedStoredProcedureQuery(
				name = "getSaldoAlDia",
				procedureName = "usp_scp_vsj_getSaldoAlDia",
				parameters = {
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class)
				}
		),
		@NamedStoredProcedureQuery(
				name = "getSaldoAlDiaCaja",
				procedureName = "usp_scp_vsj_GetSaldoAlDiaCaja",
				parameters = {
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class)
				}
		),
		@NamedStoredProcedureQuery(
				name = "getSaldoAlDiaBanco",
				procedureName = "usp_scp_vsj_GetSaldoAlDiaBanco",
				parameters = {
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class),
						@StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class)
				}
		),
		@NamedStoredProcedureQuery(
				name = "FixCodRendicionCabeceraCodDestino",
				procedureName = "usp_scp_vsj_FixCodRendicionCabecera",
				parameters = {
						@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class)
				}
		),
		@NamedStoredProcedureQuery(
				name = "FixCodRendicionCabecera",
				procedureName = "usp_scp_vsj_FixCodRendicionCabeceraCod",
				parameters = {
						@StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class)
				}
		)
}
)
public class VsjPropiedad implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cod_propiedad")
	private Integer codPropiedad;

	private String nombre;

	private String valor;

	public VsjPropiedad() {
	}

	public VsjPropiedad(String nombre, String valor) {
		this.nombre = nombre;
		this.valor = valor;
	}

	public Integer getCodPropiedad() {
		return this.codPropiedad;
	}

	public void setCodPropiedad(Integer codPropiedad) {
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
}