package org.sanjose.model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="vsj_passwordresettoken")
public class VsjPasswordresettoken {

    private static final int EXPIRATION = 60 * 60;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    private String token;

    @OneToOne(targetEntity = MsgUsuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "cod_usuario")
    private MsgUsuario user;

    private Date expiryDate;

    public VsjPasswordresettoken(MsgUsuario usuario) {
        this.user = usuario;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());
        c1.add(Calendar.SECOND, EXPIRATION);
        expiryDate=c1.getTime();
        token = UUID.randomUUID().toString();
    }

    public VsjPasswordresettoken() {
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public MsgUsuario getUser() {
        return user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}
