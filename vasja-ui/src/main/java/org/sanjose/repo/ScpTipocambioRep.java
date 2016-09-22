package org.sanjose.repo;

import org.sanjose.model.ScpTipocambio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScpTipocambioRep extends JpaRepository<ScpTipocambio, Long> {
	List<ScpTipocambio> findById_FecFechacambio(Date date);
}
