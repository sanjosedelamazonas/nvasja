package org.sanjose.repo;

import org.sanjose.model.Scp_Contraparte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Scp_ContraparteRep extends JpaRepository<Scp_Contraparte, Long> {

    //List<Scp_Contraparte> findById_CodProyecto(String s);

}
