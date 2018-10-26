package org.sanjose.repo;

import org.sanjose.model.ScpProyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScpProyectoRep extends JpaRepository<ScpProyecto, Long> {

	List<ScpProyecto> findByFecFinalGreaterThanOrFecFinalLessThan(Date fecFinal, Date fecFinalCent);

	List<ScpProyecto> findByFecFinalGreaterThanAndFecInicioLessThan(Date fecInicio, Date fecFinal);

	ScpProyecto findByCodProyecto(String codProyecto);
	
	//List<ScpPlanespecial> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);
}
