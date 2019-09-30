package org.sanjose.model;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.authentication.CurrentUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * VASJA class
 * User: prubach
 * Date: 23.09.16
 */
@MappedSuperclass
public abstract class VsjItem {

    public VsjItem prepareToSave() throws FieldGroup.CommitException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        if (this.getCodUregistro() == null) this.setCodUregistro(CurrentUser.get());
        if (this.getFecFregistro() == null) this.setFecFregistro(new Timestamp(System.currentTimeMillis()));
        this.setCodUactualiza(CurrentUser.get());
        this.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
        return this;
    }


    @Column(name="cod_mes")
    private String codMes;

    @Column(name="txt_anoproceso")
    private String txtAnoproceso;

    @Column(name="cod_uactualiza")
    private String codUactualiza;

    @Column(name="cod_uregistro")
    private String codUregistro;

    @Column(name="fec_factualiza")
    private Timestamp fecFactualiza;

    @Column(name="fec_fregistro")
    private Timestamp fecFregistro;
    
    @NotNull
    @Column(name="cod_tipomoneda")
    private Character codTipomoneda;

    public String getCodMes() {
        return codMes;
    }

    public void setCodMes(String codMes) {
        this.codMes = codMes;
    }

    public String getTxtAnoproceso() {
        return txtAnoproceso;
    }

    public void setTxtAnoproceso(String txtAnoproceso) {
        this.txtAnoproceso = txtAnoproceso;
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

    public Character getCodTipomoneda() {
        return codTipomoneda;
    }

    public void setCodTipomoneda(Character codTipomoneda) {
        this.codTipomoneda = codTipomoneda;
    }

    @Override
    public String toString() {
        return "VsjItem{" +
                "codMes='" + codMes + '\'' +
                ", txtAnoproceso='" + txtAnoproceso + '\'' +
                ", codUactualiza='" + codUactualiza + '\'' +
                ", codUregistro='" + codUregistro + '\'' +
                ", fecFactualiza=" + fecFactualiza +
                ", fecFregistro=" + fecFregistro +
                ", codTipomoneda=" + codTipomoneda +
                '}';
    }
}