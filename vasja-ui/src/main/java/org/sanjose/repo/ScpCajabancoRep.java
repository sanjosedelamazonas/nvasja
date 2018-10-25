package org.sanjose.repo;

import org.sanjose.model.ScpCajabanco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScpCajabancoRep extends JpaRepository<ScpCajabanco, Long> {

    List<ScpCajabanco> findByCodDestinoOrCodDestinoitem(String s, String s2);

    ScpCajabanco findByCodCajabanco(Integer id);

    List<ScpCajabanco> findByCodTranscorrelativo(String s);

    List<ScpCajabanco> findByFecFechaBetween(Date from, Date to);
}
