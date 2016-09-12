package org.sanjose.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Scp_ContraparteRep extends JpaRepository<Scp_Contraparte, Long> {

    //List<Scp_Contraparte> findById_CodProyecto(String s);

}
