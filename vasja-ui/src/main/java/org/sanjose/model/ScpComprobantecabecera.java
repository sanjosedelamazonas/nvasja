package org.sanjose.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "scp_comprobantecabecera")
@NamedQuery(name = "ScpComprobantecabecera.findAll", query = "SELECT s FROM ScpComprobantecabecera s")
public class ScpComprobantecabecera implements Serializable {

    private static final long serialVersionUID = 124664635241267763L;

    @EmbeddedId
    private ScpComprobantecabeceraPK id;

    @Column(name = "fec_comprobante")
    private Timestamp fecComprobante;

    @Column(name="cod_tipooperacion")
    private String codTipooperacion;

    @Column(name="cod_mediopago")
    private String codMediopago;

    @NotNull
    @Column(name="cod_tipomoneda")
    private Character codTipomoneda;

    @NotBlank
    @Size(min=2, max=70)
    @Column(name="txt_glosa")
    private String txtGlosa;

    @NotNull
    @Column(name="cod_destino")
    private String codDestino;

    @Column(name="cod_banco")
    private String codBanco;

    @NotNull
    @Column(name="cod_ctacontable")
    private String codCtacontable;

    @Column(name = "flg_im")
    private Character flgIm;

    @Column(name="cod_uactualiza")
    private String codUactualiza;

    @Column(name="cod_uregistro")
    private String codUregistro;

    @Column(name="fec_factualiza")
    private Timestamp fecFactualiza;

    @Column(name="fec_fregistro")
    private Timestamp fecFregistro;

    public ScpComprobantecabeceraPK getId() {
        return id;
    }

    public void setId(ScpComprobantecabeceraPK id) {
        this.id = id;
    }

    public Timestamp getFecComprobante() {
        return fecComprobante;
    }

    public void setFecComprobante(Timestamp fecComprobante) {
        this.fecComprobante = fecComprobante;
    }

    public String getCodTipooperacion() {
        return codTipooperacion;
    }

    public void setCodTipooperacion(String codTipooperacion) {
        this.codTipooperacion = codTipooperacion;
    }

    public String getCodMediopago() {
        return codMediopago;
    }

    public void setCodMediopago(String codMediopago) {
        this.codMediopago = codMediopago;
    }

    public Character getCodTipomoneda() {
        return codTipomoneda;
    }

    public void setCodTipomoneda(Character codTipomoneda) {
        this.codTipomoneda = codTipomoneda;
    }

    public String getTxtGlosa() {
        return txtGlosa;
    }

    public void setTxtGlosa(String txtGlosa) {
        this.txtGlosa = txtGlosa;
    }

    public String getCodDestino() {
        return codDestino;
    }

    public void setCodDestino(String codDestino) {
        this.codDestino = codDestino;
    }

    public String getCodBanco() {
        return codBanco;
    }

    public void setCodBanco(String codBanco) {
        this.codBanco = codBanco;
    }

    public String getCodCtacontable() {
        return codCtacontable;
    }

    public void setCodCtacontable(String codCtacontable) {
        this.codCtacontable = codCtacontable;
    }

    public Character getFlgIm() {
        return flgIm;
    }

    public void setFlgIm(Character flgIm) {
        this.flgIm = flgIm;
    }

    public String getCodUactualiza() {
        return codUactualiza;
    }

    public void setCodUactualiza(String codUactualiza) {
        this.codUactualiza = codUactualiza;
    }

    public String getCodUregistro() {
        return codUregistro;
    }

    public void setCodUregistro(String codUregistro) {
        this.codUregistro = codUregistro;
    }

    public Timestamp getFecFactualiza() {
        return fecFactualiza;
    }

    public void setFecFactualiza(Timestamp fecFactualiza) {
        this.fecFactualiza = fecFactualiza;
    }

    public Timestamp getFecFregistro() {
        return fecFregistro;
    }

    public void setFecFregistro(Timestamp fecFregistro) {
        this.fecFregistro = fecFregistro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScpComprobantecabecera that = (ScpComprobantecabecera) o;
        return Objects.equals(id, that.id) && Objects.equals(fecComprobante, that.fecComprobante) && Objects.equals(codTipooperacion, that.codTipooperacion) && Objects.equals(codMediopago, that.codMediopago) && Objects.equals(codTipomoneda, that.codTipomoneda) && Objects.equals(txtGlosa, that.txtGlosa) && Objects.equals(codDestino, that.codDestino) && Objects.equals(codBanco, that.codBanco) && Objects.equals(codCtacontable, that.codCtacontable) && Objects.equals(flgIm, that.flgIm) && Objects.equals(codUactualiza, that.codUactualiza) && Objects.equals(codUregistro, that.codUregistro) && Objects.equals(fecFactualiza, that.fecFactualiza) && Objects.equals(fecFregistro, that.fecFregistro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fecComprobante, codTipooperacion, codMediopago, codTipomoneda, txtGlosa, codDestino, codBanco, codCtacontable, flgIm, codUactualiza, codUregistro, fecFactualiza, fecFregistro);
    }
}
