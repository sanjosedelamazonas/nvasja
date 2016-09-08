package org.sanjose.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VsjCajabancoRep extends JpaRepository<VsjCajabanco, Long> {

	//List<VsjCajabanco> findByFlgMovimiento(String s);
	
	//List<VsjCajabanco> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(String mov, String ano, String codcta);
	
	//List<VsjCajabanco> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);	
}
