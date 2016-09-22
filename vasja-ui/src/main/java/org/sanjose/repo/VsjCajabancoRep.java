package org.sanjose.repo;

import java.util.List;

import org.sanjose.model.VsjCajabanco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VsjCajabancoRep extends JpaRepository<VsjCajabanco, Long> {

    List<VsjCajabanco> findByCodDestinoOrCodDestinoitem(String s, String s2);

    VsjCajabanco findByCodCajabanco(Integer id);

    List<VsjCajabanco> findByCodTranscorrelativo(String s);
}
