package org.sanjose.vasja;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_presupuestoproyecto database table.
 * 
 */
@Entity
@Table(name="scp_presupuestoproyecto")
@NamedQuery(name="ScpPresupuestoproyecto.findAll", query="SELECT s FROM ScpPresupuestoproyecto s")
public class ScpPresupuestoproyecto implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ScpPresupuestoproyectoPK id;

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

	@Column(name="num_ppto00")
	private double numPpto00;

	@Column(name="num_ppto01")
	private double numPpto01;

	@Column(name="num_ppto02")
	private double numPpto02;

	@Column(name="num_ppto03")
	private double numPpto03;

	@Column(name="num_ppto04")
	private double numPpto04;

	@Column(name="num_ppto05")
	private double numPpto05;

	@Column(name="num_ppto06")
	private double numPpto06;

	@Column(name="num_ppto07")
	private double numPpto07;

	@Column(name="num_ppto08")
	private double numPpto08;

	@Column(name="num_ppto09")
	private double numPpto09;

	@Column(name="num_ppto10")
	private double numPpto10;

	@Column(name="num_ppto11")
	private double numPpto11;

	@Column(name="num_ppto12")
	private double numPpto12;

	@Column(name="txt_nroperiodo")
	private String txtNroperiodo;

	public ScpPresupuestoproyecto() {
	}

	public ScpPresupuestoproyectoPK getId() {
		return this.id;
	}

	public void setId(ScpPresupuestoproyectoPK id) {
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

	public double getNumPpto00() {
		return this.numPpto00;
	}

	public void setNumPpto00(double numPpto00) {
		this.numPpto00 = numPpto00;
	}

	public double getNumPpto01() {
		return this.numPpto01;
	}

	public void setNumPpto01(double numPpto01) {
		this.numPpto01 = numPpto01;
	}

	public double getNumPpto02() {
		return this.numPpto02;
	}

	public void setNumPpto02(double numPpto02) {
		this.numPpto02 = numPpto02;
	}

	public double getNumPpto03() {
		return this.numPpto03;
	}

	public void setNumPpto03(double numPpto03) {
		this.numPpto03 = numPpto03;
	}

	public double getNumPpto04() {
		return this.numPpto04;
	}

	public void setNumPpto04(double numPpto04) {
		this.numPpto04 = numPpto04;
	}

	public double getNumPpto05() {
		return this.numPpto05;
	}

	public void setNumPpto05(double numPpto05) {
		this.numPpto05 = numPpto05;
	}

	public double getNumPpto06() {
		return this.numPpto06;
	}

	public void setNumPpto06(double numPpto06) {
		this.numPpto06 = numPpto06;
	}

	public double getNumPpto07() {
		return this.numPpto07;
	}

	public void setNumPpto07(double numPpto07) {
		this.numPpto07 = numPpto07;
	}

	public double getNumPpto08() {
		return this.numPpto08;
	}

	public void setNumPpto08(double numPpto08) {
		this.numPpto08 = numPpto08;
	}

	public double getNumPpto09() {
		return this.numPpto09;
	}

	public void setNumPpto09(double numPpto09) {
		this.numPpto09 = numPpto09;
	}

	public double getNumPpto10() {
		return this.numPpto10;
	}

	public void setNumPpto10(double numPpto10) {
		this.numPpto10 = numPpto10;
	}

	public double getNumPpto11() {
		return this.numPpto11;
	}

	public void setNumPpto11(double numPpto11) {
		this.numPpto11 = numPpto11;
	}

	public double getNumPpto12() {
		return this.numPpto12;
	}

	public void setNumPpto12(double numPpto12) {
		this.numPpto12 = numPpto12;
	}

	public String getTxtNroperiodo() {
		return this.txtNroperiodo;
	}

	public void setTxtNroperiodo(String txtNroperiodo) {
		this.txtNroperiodo = txtNroperiodo;
	}

}