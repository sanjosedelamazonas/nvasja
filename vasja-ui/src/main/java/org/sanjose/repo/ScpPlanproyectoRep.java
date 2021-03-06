package org.sanjose.repo;

import org.sanjose.model.ScpPlanproyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.DocFlavor;
import java.util.List;

public interface ScpPlanproyectoRep extends JpaRepository<ScpPlanproyecto, Long> {

    List<ScpPlanproyecto> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);

    List<ScpPlanproyecto> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(String mov, String ano, String proyecto);
}
