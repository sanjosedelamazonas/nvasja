package org.sanjose.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MsgUsuarioRep extends JpaRepository<MsgUsuario, Long> {

	MsgUsuario findByTxtUsuarioAndTxtPassword(String s1, String s2);

	MsgUsuario findByTxtUsuario(String username);

}
