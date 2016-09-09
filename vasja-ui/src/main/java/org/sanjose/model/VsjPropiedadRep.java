package org.sanjose.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VsjPropiedadRep extends JpaRepository<VsjPropiedad, Long> {

	VsjPropiedad findByNombre(String s);
	
}
