package org.sanjose.repo;

import org.sanjose.model.VsjCajabanco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface VsjCajabancoRep extends JpaRepository<VsjCajabanco, Long> {

    List<VsjCajabanco> findByCodDestinoOrCodDestinoitem(String s, String s2);

    VsjCajabanco findByCodCajabanco(Integer id);

    List<VsjCajabanco> findByCodTranscorrelativo(String s);

    List<VsjCajabanco> findByFecFechaBetween(Date from, Date to);
}
