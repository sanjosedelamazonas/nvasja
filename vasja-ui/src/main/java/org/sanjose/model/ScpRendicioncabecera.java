package org.sanjose.model;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.hibernate.validator.constraints.NotBlank;
import org.sanjose.authentication.Role;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Entity
@Table(name = "scp_rendicioncabecera")
@NamedQuery(name = "ScpRendicioncabecera.findAll", query = "SELECT v FROM ScpRendicioncabecera v")
public class ScpRendicioncabecera extends VsjItem {

    private static final long serialVersionUID = 123234242655744L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_rendicioncabecera")
    private Integer codRendicioncabecera;

    @Column(name = "cod_filial")
    private String codFilial;
    @Column(name = "cod_origen")
    private String codOrigen;
    @Column(name = "cod_comprobante")
    private String codComprobante;

    @Column(name = "fec_comprobante")
    private java.sql.Timestamp fecComprobante;
    @Column(name = "cod_tipooperacion")
    private String codTipooperacion;
    @Column(name = "cod_mediopago")
    private String codMediopago;
    @NotBlank
    @Size(min = 2, max = 70)
    @Column(name = "txt_glosa")
    private String txtGlosa;

    @Column(name = "cod_destino")
    private String codDestino;

    @Column(name = "cod_banco")
    private String codBanco;
    @Column(name = "flg_enviado")
    private Character flgEnviado;
    @Column(name = "cod_origenenlace")
    private String codOrigenenlace;
    @Column(name = "cod_comprobanteenlace")
    private String codComprobanteenlace;
    @Column(name = "num_totalanticipo")
    private BigDecimal numTotalanticipo;
    @Column(name = "num_gastototal")
    private BigDecimal numGastototal;
    @Column(name = "num_saldopendiente")
    private BigDecimal numSaldopendiente;

    @Column(name = "flg_im")
    private Character flgIm;

    //bi-directional many-to-one association to ScpRendiciondetealles
    @OneToMany(mappedBy = "scpRendicioncabecera")
    private List<ScpRendiciondetalle> scpRendiciondetalles;

    public ScpRendicioncabecera() {
        setFecComprobante(new Timestamp(System.currentTimeMillis()));
        setFecFregistro(new Timestamp(System.currentTimeMillis()));
        setTxtGlosa("TEST");
        setCodDestino("V003");
        setFlgEnviado('0');
        setFlgIm('1');
        setCodOrigen("08");
        setCodFilial("01");
        setCodTipooperacion("");
        setCodMediopago("");
        setCodComprobanteenlace("");
        setCodOrigenenlace("");
        setCodTipomoneda('0');
        setCodBanco("");
        setNumGastototal(new BigDecimal(0));
        setNumSaldopendiente(new BigDecimal(0));
        setNumTotalanticipo(new BigDecimal(0));
    }


    public VsjItem prepareToSave() throws FieldGroup.CommitException {
        super.prepareToSave();
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        this.setCodMes(sdf.format(this.getFecComprobante()));
        sdf = new SimpleDateFormat("yyyy");
        this.setTxtAnoproceso(sdf.format(this.getFecComprobante()));
        return this;
    }

//  @ManyToOne(targetEntity = ScpDestino.class)
//  @JoinColumn(name = "cod_destino", insertable = false, updatable = false)
//  private ScpDestino scpDestino;

    //@ManyToOne(targetEntity = MsgUsuario.class)
    //@JoinColumn(name = "cod_uactualiza", insertable = false, updatable = false)
    //private MsgUsuario msgUsuario;

    public boolean isReadOnly() {
    return (isEnviado() && !Role.isPrivileged());
}

    public boolean isEnviado() {
        return flgEnviado!=null && flgEnviado.equals('1');
    }

//    public MsgUsuario getMsgUsuario() {
//        return msgUsuario;
//    }
//
//    public void setMsgUsuario(MsgUsuario msgUsuario) {
//        this.msgUsuario = msgUsuario;
//    }

    public Integer getCodRendicioncabecera() {
        return codRendicioncabecera;
    }

    public void setCodRendicioncabecera(Integer codRendicioncabecera) {
        this.codRendicioncabecera = codRendicioncabecera;
    }

    public String getCodFilial() {
        return codFilial;
    }

    public void setCodFilial(String codFilial) {
        this.codFilial = codFilial;
    }

    public String getCodOrigen() {
        return codOrigen;
    }

    public void setCodOrigen(String codOrigen) {
        this.codOrigen = codOrigen;
    }

    public String getCodComprobante() {
        return codComprobante;
    }

    public void setCodComprobante(String codComprobante) {
        this.codComprobante = codComprobante;
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

    public Character getFlgEnviado() {
        return flgEnviado;
    }

    public void setFlgEnviado(Character flgEnviado) {
        this.flgEnviado = flgEnviado;
    }

    public String getCodOrigenenlace() {
        return codOrigenenlace;
    }

    public void setCodOrigenenlace(String codOrigenenlace) {
        this.codOrigenenlace = codOrigenenlace;
    }

    public String getCodComprobanteenlace() {
        return codComprobanteenlace;
    }

    public void setCodComprobanteenlace(String codComprobanteenlace) {
        this.codComprobanteenlace = codComprobanteenlace;
    }

    public BigDecimal getNumTotalanticipo() {
        return numTotalanticipo;
    }

    public void setNumTotalanticipo(BigDecimal numTotalanticipo) {
        this.numTotalanticipo = numTotalanticipo;
    }

    public BigDecimal getNumGastototal() {
        return numGastototal;
    }

    public void setNumGastototal(BigDecimal numGastototal) {
        this.numGastototal = numGastototal;
    }

    public BigDecimal getNumSaldopendiente() {
        return numSaldopendiente;
    }

    public void setNumSaldopendiente(BigDecimal numSaldopendiente) {
        this.numSaldopendiente = numSaldopendiente;
    }

    public Character getFlgIm() {
        return flgIm;
    }

    public void setFlgIm(Character flgIm) {
        this.flgIm = flgIm;
    }

    public List<ScpRendiciondetalle> getScpRendiciondetalles() {
        return scpRendiciondetalles;
    }

    public void setScpRendiciondetalles(List<ScpRendiciondetalle> scpRendiciondetalles) {
        this.scpRendiciondetalles = scpRendiciondetalles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScpRendicioncabecera that = (ScpRendicioncabecera) o;

        return getCodRendicioncabecera() != null ? getCodRendicioncabecera().equals(that.getCodRendicioncabecera()) : that.getCodRendicioncabecera() == null;

    }

    @Override
    public int hashCode() {
        return getCodRendicioncabecera() != null ? getCodRendicioncabecera().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ScpRendicioncabecera{" +
                "codRendicioncabecera=" + codRendicioncabecera +
                ", codFilial='" + codFilial + '\'' +
                ", codOrigen='" + codOrigen + '\'' +
                ", codComprobante='" + codComprobante + '\'' +
                ", fecComprobante=" + fecComprobante +
                ", codTipooperacion='" + codTipooperacion + '\'' +
                ", codMediopago='" + codMediopago + '\'' +
                ", txtGlosa='" + txtGlosa + '\'' +
                ", codDestino='" + codDestino + '\'' +
                ", codBanco='" + codBanco + '\'' +
                ", flgEnviado=" + flgEnviado +
                ", codOrigenenlace='" + codOrigenenlace + '\'' +
                ", codComprobanteenlace='" + codComprobanteenlace + '\'' +
                ", numTotalanticipo=" + numTotalanticipo +
                ", numGastototal=" + numGastototal +
                ", numSaldopendiente=" + numSaldopendiente +
                ", flgIm=" + flgIm +
                ", " + super.toString() +
                '}';
    }
}
