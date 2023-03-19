package org.sanjose.repo;

import org.sanjose.model.MsgRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MsgRolRep extends JpaRepository<MsgRol, Long> {

	MsgRol findByCodRol(String rol);

	MsgRol findByTxtSerie(String s);

	List<MsgRol> findByCodRolLikeOrderByCodRolDesc(String s);
}
