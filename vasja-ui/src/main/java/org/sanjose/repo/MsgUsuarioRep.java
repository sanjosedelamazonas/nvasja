package org.sanjose.repo;

import org.sanjose.model.MsgUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgUsuarioRep extends JpaRepository<MsgUsuario, Long> {

	MsgUsuario findByTxtUsuarioAndTxtPassword(String s1, String s2);

	MsgUsuario findByTxtUsuario(String username);

}
