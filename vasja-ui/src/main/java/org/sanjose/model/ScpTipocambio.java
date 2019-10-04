package org.sanjose.model;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.authentication.CurrentUser;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


/**
 * The persistent class for the scp_tipocambio database table.
 * 
 */
@Entity
@Table(name="scp_tipocambio")
@NamedQuery(name="ScpTipocambio.findAll", query="SELECT s FROM ScpTipocambio s")
public class ScpTipocambio implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ScpTipocambioPK id;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="num_tccdolar")
	private BigDecimal numTccdolar;

	@Column(name="num_tcceuro")
	private BigDecimal numTcceuro;

	@Column(name="num_tcvdolar")
	private BigDecimal numTcvdolar;

	@Column(name="num_tcveuro")
	private BigDecimal numTcveuro;

	public ScpTipocambio() {
	}
	public void prepareToSave() {
		if (this.getCodUregistro() == null) this.setCodUregistro(CurrentUser.get());
		if (this.getFecFregistro() == null) this.setFecFregistro(new Timestamp(System.currentTimeMillis()));
		this.setCodUactualiza(CurrentUser.get());
		this.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
	}

	public ScpTipocambioPK getId() {
		return this.id;
	}

	public void setId(ScpTipocambioPK id) {
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

	public BigDecimal getNumTccdolar() {
		return this.numTccdolar;
	}

	public void setNumTccdolar(BigDecimal numTccdolar) {
		this.numTccdolar = numTccdolar;
	}

	public BigDecimal getNumTcceuro() {
		return this.numTcceuro;
	}

	public void setNumTcceuro(BigDecimal numTcceuro) {
		this.numTcceuro = numTcceuro;
	}

	public BigDecimal getNumTcvdolar() {
		return this.numTcvdolar;
	}

	public void setNumTcvdolar(BigDecimal numTcvdolar) {
		this.numTcvdolar = numTcvdolar;
	}

	public BigDecimal getNumTcveuro() {
		return this.numTcveuro;
	}

	public void setNumTcveuro(BigDecimal numTcveuro) {
		this.numTcveuro = numTcveuro;
	}

}