package org.sanjose.model;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.authentication.CurrentUser;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


/**
 * The persistent class for the scp_Contraparte database table.
 *
 */
@Entity
@Table(name="vsj_rendicionanticipio")
@NamedQuery(name="VsjRendicionanticipio.findAll", query="SELECT s FROM VsjRendicionanticipio s")
public class VsjRendicionanticipio implements Serializable {
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name="cod_comprobante")
	private String codComprobante;

	@Column(name="fec_anticipio")
	private Timestamp fecAnticipio;

	@Column(name="txt_glosa")
	private String txtGlosa;

	@Column(name="ind_tipomoneda")
	private Character indTipomoneda;

	@Column(name="num_anticipio")
	private BigDecimal numAnticipio;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	public VsjRendicionanticipio() {
	}

	public VsjRendicionanticipio prepareToSave() throws FieldGroup.CommitException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		if (this.getCodUregistro() == null) this.setCodUregistro(CurrentUser.get());
		if (this.getFecFregistro() == null) this.setFecFregistro(new Timestamp(System.currentTimeMillis()));
		this.setCodUactualiza(CurrentUser.get());
		this.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
		return this;
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

	public String getCodComprobante() {
		return codComprobante;
	}

	public void setCodComprobante(String codComprobante) {
		this.codComprobante = codComprobante;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getFecAnticipio() {
		return fecAnticipio;
	}

	public void setFecAnticipio(Timestamp fecAnticipio) {
		this.fecAnticipio = fecAnticipio;
	}

	public String getTxtGlosa() {
		return txtGlosa;
	}

	public void setTxtGlosa(String txtGlosa) {
		this.txtGlosa = txtGlosa;
	}

	public Character getIndTipomoneda() {
		return indTipomoneda;
	}

	public void setIndTipomoneda(Character indTipomoneda) {
		this.indTipomoneda = indTipomoneda;
	}

	public BigDecimal getNumAnticipio() {
		return numAnticipio;
	}

	public void setNumAnticipio(BigDecimal numAnticipio) {
		this.numAnticipio = numAnticipio;
	}
}