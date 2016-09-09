package org.sanjose.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScpPlanproyectoRep extends JpaRepository<ScpPlanproyecto, Long> {

    List<ScpPlanproyecto> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);

    List<ScpPlanproyecto> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(String mov, String ano, String proyecto);
}
