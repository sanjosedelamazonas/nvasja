package org.sanjose.repo;

import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VsjBancodetalleRep extends JpaRepository<VsjBancodetalle, Long> {

    List<VsjBancodetalle> findByCodDestinoOrCodDestinoitem(String s, String s2);

    List<VsjBancodetalle> findById_CodBancocabecera(Integer id);
}
