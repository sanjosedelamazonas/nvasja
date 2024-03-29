package org.sanjose.model;

import org.hibernate.validator.constraints.NotBlank;
import org.sanjose.authentication.CurrentUser;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;


/**
 * The persistent class for the scp_destino database table.
 * 
 */
@Entity
@Table(name="scp_destino")
@NamedQuery(name="ScpDestino.findAll", query="SELECT s FROM ScpDestino s")
public class ScpDestino implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	//@GeneratedValue(strategy=GenerationType.IDENTITY)
	@NotBlank
	@NotNull
	@Column(name="cod_destino")
	private String codDestino;

	@Column(name="cod_cargo")
	private String codCargo;

	@Column(name="cod_filial")
	private String codFilial;

	@Column(name="cod_uactualiza")
	private String codUactualiza;

	@Column(name="cod_uregistro")
	private String codUregistro;

	@Column(name="fec_factualiza")
	private Timestamp fecFactualiza;

	@Column(name="fec_fregistro")
	private Timestamp fecFregistro;

	@Column(name="flg_im")
	private Character flgIm;

	@Column(name="ind_sexo")
	private Character indSexo;

	@Column(name="ind_tipodctoidentidad")
	private String indTipodctoidentidad;

	@NotNull
	@Column(name="ind_tipodestino")
	private Character indTipodestino;

	@NotNull
	@Column(name="ind_tipopersona")
	private Character indTipopersona;

	@Column(name="txt_apellidomaterno")
	private String txtApellidomaterno;

	@Column(name="txt_apellidopaterno")
	private String txtApellidopaterno;

	@Column(name="txt_direccion")
	private String txtDireccion;

	@NotBlank
	@Column(name="txt_nombre")
	private String txtNombre;

	@Column(name="txt_nombredestino")
	private String txtNombredestino;

	@Column(name="txt_numerodctoidentidad")
	private String txtNumerodctoidentidad;

	@Column(name="txt_ruc")
	private String txtRuc;

	@Column(name="txt_telefono1")
	private String txtTelefono1;

	@Column(name="txt_telefono2")
	private String txtTelefono2;

	@Column(name="txt_email")
	private String txtEmail;

	@Column(name="txt_usuario")
	private String txtUsuario;

	@Column(name="flg_activo")
	private boolean activo;

	@Column(name="flg_enviar_reporte")
	private boolean enviarreporte;


	public ScpDestino() {
	}

	public void prepToSave() {
        if (getCodUregistro() == null) setCodUregistro(CurrentUser.get());
        if (getFecFregistro() == null) setFecFregistro(new Timestamp(System.currentTimeMillis()));
        setCodUactualiza(CurrentUser.get());
        setFecFactualiza(new Timestamp(System.currentTimeMillis()));

        if (getTxtNombredestino() == null) setTxtNombredestino("");
		if (getTxtNombredestino().length() > 100) {
			setTxtNombredestino(getTxtNombredestino().substring(0, 100));
		}
		if (getTxtNombre() == null) setTxtNombre(getTxtNombredestino());
		if (getCodCargo() == null) setCodCargo("");
        if (getCodFilial()== null) setCodFilial("");
        if (getTxtApellidomaterno() == null) setTxtApellidomaterno("");
        if (getTxtDireccion() == null) setTxtDireccion("");
		if (getTxtDireccion().length() > 150) {
			setTxtDireccion(getTxtDireccion().substring(0, 150));
		}
        if (getTxtApellidopaterno() == null) setTxtApellidopaterno("");
        if (getTxtNumerodctoidentidad() == null) setTxtNumerodctoidentidad("");
        if (getTxtRuc() == null) setTxtRuc("");
        if (getTxtTelefono1() == null) setTxtTelefono1("");
        if (getTxtTelefono2() == null) setTxtTelefono2("");
        if (getTxtEmail() == null) setTxtEmail("");
        if (getTxtUsuario() == null) setTxtUsuario("");
        if (getIndSexo() == null) setIndSexo(' ');
        if (getIndTipodctoidentidad() == null) setIndTipodctoidentidad("");
        setFlgIm('1');
    }
	

	public String getCodDestino() {
		return this.codDestino;
	}

	public void setCodDestino(String codDestino) {
		this.codDestino = codDestino;
	}

	public String getCodCargo() {
		return this.codCargo;
	}

	public void setCodCargo(String codCargo) {
		this.codCargo = codCargo;
	}

	public String getCodFilial() {
		return this.codFilial;
	}

	public void setCodFilial(String codFilial) {
		this.codFilial = codFilial;
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

	public Character getFlgIm() {
		return this.flgIm;
	}

	public void setFlgIm(Character flgIm) {
		this.flgIm = flgIm;
	}

	public Character getIndSexo() {
		return this.indSexo;
	}

	public void setIndSexo(Character indSexo) {
		this.indSexo = indSexo;
	}

	public String getIndTipodctoidentidad() {
		return this.indTipodctoidentidad;
	}

	public void setIndTipodctoidentidad(String indTipodctoidentidad) {
		this.indTipodctoidentidad = indTipodctoidentidad;
	}

	public Character getIndTipodestino() {
		return this.indTipodestino;
	}

	public void setIndTipodestino(Character indTipodestino) {
		this.indTipodestino = indTipodestino;
	}

	public Character getIndTipopersona() {
		return this.indTipopersona;
	}

	public void setIndTipopersona(Character indTipopersona) {
		this.indTipopersona = indTipopersona;
	}

	public String getTxtApellidomaterno() {
		return this.txtApellidomaterno;
	}

	public void setTxtApellidomaterno(String txtApellidomaterno) {
		this.txtApellidomaterno = txtApellidomaterno;
	}

	public String getTxtApellidopaterno() {
		return this.txtApellidopaterno;
	}

	public void setTxtApellidopaterno(String txtApellidopaterno) {
		this.txtApellidopaterno = txtApellidopaterno;
	}

	public String getTxtDireccion() {
		return this.txtDireccion;
	}

	public void setTxtDireccion(String txtDireccion) {
		this.txtDireccion = txtDireccion;
	}

	public String getTxtNombre() {
		return this.txtNombre;
	}

	public void setTxtNombre(String txtNombre) {
		this.txtNombre = txtNombre;
	}

	public String getTxtNombredestino() {
		return this.txtNombredestino;
	}

	public void setTxtNombredestino(String txtNombredestino) {
		this.txtNombredestino = txtNombredestino;
	}

	public String getTxtNumerodctoidentidad() {
		return this.txtNumerodctoidentidad;
	}

	public void setTxtNumerodctoidentidad(String txtNumerodctoidentidad) {
		this.txtNumerodctoidentidad = txtNumerodctoidentidad;
	}

	public String getTxtRuc() {
		return this.txtRuc;
	}

	public void setTxtRuc(String txtRuc) {
		this.txtRuc = txtRuc;
	}

	public String getTxtTelefono1() {
		return this.txtTelefono1;
	}

	public void setTxtTelefono1(String txtTelefono1) {
		this.txtTelefono1 = txtTelefono1;
	}

	public String getTxtTelefono2() {
		return this.txtTelefono2;
	}

	public void setTxtTelefono2(String txtTelefono2) {
		this.txtTelefono2 = txtTelefono2;
	}

	public String getTxtEmail() {
		return txtEmail;
	}

	public void setTxtEmail(String txtEmail) {
		this.txtEmail = txtEmail;
	}

	public String getTxtUsuario() {
		return txtUsuario;
	}

	public void setTxtUsuario(String txtUsuario) {
		this.txtUsuario = txtUsuario;
	}

	public boolean isActivo() {
		return activo;
	}

    public boolean getActivo() {
        return activo;
    }

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public boolean isEnviarreporte() {
		return enviarreporte;
	}


    public boolean getEnviarreporte() {
        return enviarreporte;
    }

    public void setEnviarreporte(boolean enviarreporte) {
		this.enviarreporte = enviarreporte;
	}

	@Override
	public String toString() {
		return "ScpDestino{" +
				"codDestino='" + codDestino + '\'' +
				", codCargo='" + codCargo + '\'' +
				", codFilial='" + codFilial + '\'' +
				", codUactualiza='" + codUactualiza + '\'' +
				", codUregistro='" + codUregistro + '\'' +
				", fecFactualiza=" + fecFactualiza +
				", fecFregistro=" + fecFregistro +
				", flgIm=" + flgIm +
				", indSexo=" + indSexo +
				", indTipodctoidentidad='" + indTipodctoidentidad + '\'' +
				", indTipodestino=" + indTipodestino +
				", indTipopersona=" + indTipopersona +
				", txtApellidomaterno='" + txtApellidomaterno + '\'' +
				", txtApellidopaterno='" + txtApellidopaterno + '\'' +
				", txtDireccion='" + txtDireccion + '\'' +
				", txtNombre='" + txtNombre + '\'' +
				", txtNombredestino='" + txtNombredestino + '\'' +
				", txtNumerodctoidentidad='" + txtNumerodctoidentidad + '\'' +
				", txtRuc='" + txtRuc + '\'' +
				", txtTelefono1='" + txtTelefono1 + '\'' +
				", txtTelefono2='" + txtTelefono2 + '\'' +
				'}';
	}
}