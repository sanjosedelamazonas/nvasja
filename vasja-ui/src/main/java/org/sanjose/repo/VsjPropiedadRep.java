package org.sanjose.repo;

import org.sanjose.model.VsjPropiedad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VsjPropiedadRep extends JpaRepository<VsjPropiedad, Long> {

	VsjPropiedad findByNombre(String s);
	
}
