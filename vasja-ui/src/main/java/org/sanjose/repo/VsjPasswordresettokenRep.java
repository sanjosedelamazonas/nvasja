package org.sanjose.repo;

import org.sanjose.model.VsjPasswordresettoken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VsjPasswordresettokenRep extends JpaRepository<VsjPasswordresettoken, Long> {

    VsjPasswordresettoken findByToken(String token);

}
