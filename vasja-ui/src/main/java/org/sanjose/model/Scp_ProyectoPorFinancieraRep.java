package org.sanjose.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Scp_ProyectoPorFinancieraRep extends JpaRepository<Scp_ProyectoPorFinanciera, Long> {

    List<Scp_ProyectoPorFinanciera> findById_CodProyecto(String s);

}
