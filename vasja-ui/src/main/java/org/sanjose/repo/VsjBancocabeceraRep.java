package org.sanjose.repo;

import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjCajabanco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VsjBancocabeceraRep extends JpaRepository<VsjBancocabecera, Long> {

    List<VsjBancocabecera> findByCodDestinoOrCodDestinoitem(String s, String s2);

    VsjBancocabecera findByCodBancocabecera(Integer id);

}
