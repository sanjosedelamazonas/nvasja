package org.sanjose.config;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_ProyectoPorFinanciera database table.
 * 
 */
@Entity
@Table(name="scp_ProyectoPorFinanciera")
@NamedQuery(name="Scp_ProyectoPorFinanciera.findAll", query="SELECT s FROM Scp_ProyectoPorFinanciera s")
public class Scp_ProyectoPorFinanciera implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private Scp_ProyectoPorFinancieraPK id;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="flg_im")
	private String flgIm;

	@Column(name="num_orden")
	private BigDecimal numOrden;

	public Scp_ProyectoPorFinanciera() {
	}

	public Scp_ProyectoPorFinancieraPK getId() {
		return this.id;
	}

	public void setId(Scp_ProyectoPorFinancieraPK id) {
		this.id = id;
	}

	public String getCodUactualiza() {
		return this.codUactualiza;
	}

	public void setCodUactualiza(String codUactualiza) {
		this.codUactualiza = codUactualiza;
	}

	public String getCodUregistro() {
		return this.codUregistro;
	}

	public void setCodUregistro(String codUregistro) {
		this.codUregistro = codUregistro;
	}

	public Timestamp getFecFactualiza() {
		return this.fecFactualiza;
	}

	public void setFecFactualiza(Timestamp fecFactualiza) {
		this.fecFactualiza = fecFactualiza;
	}

	public Timestamp getFecFregistro() {
		return this.fecFregistro;
	}

	public void setFecFregistro(Timestamp fecFregistro) {
		this.fecFregistro = fecFregistro;
	}

	public String getFlgIm() {
		return this.flgIm;
	}

	public void setFlgIm(String flgIm) {
		this.flgIm = flgIm;
	}

	public BigDecimal getNumOrden() {
		return this.numOrden;
	}

	public void setNumOrden(BigDecimal numOrden) {
		this.numOrden = numOrden;
	}

}