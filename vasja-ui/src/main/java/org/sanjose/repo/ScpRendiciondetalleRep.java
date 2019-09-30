package org.sanjose.repo;

import org.sanjose.model.ScpRendiciondetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScpRendiciondetalleRep extends JpaRepository<ScpRendiciondetalle, Long> {

    //List<ScpRendiciondetalle> findByCodDestinoOrCodDestinoitem(String s, String s2);

//    List<ScpRendiciondetalle> findById_CodBancocabecera(Integer id);

    //List<ScpRendiciondetalle> findById_CodBancocabeceraAndId_NumItemGreaterThan(Integer id, Integer numItem);
}
