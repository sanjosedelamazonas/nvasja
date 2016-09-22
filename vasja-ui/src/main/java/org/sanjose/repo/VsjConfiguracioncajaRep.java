package org.sanjose.repo;

import org.sanjose.model.VsjConfiguracioncaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VsjConfiguracioncajaRep extends JpaRepository<VsjConfiguracioncaja, Long> {

    List<VsjConfiguracioncaja> findByCodProyectoAndIndTipomoneda(String codProyecto, String tipoMoneda);

    List<VsjConfiguracioncaja> findByCodDestinoAndIndTipomoneda(String codDestino, String tipoMoneda);

    List<VsjConfiguracioncaja> findByCodCategoriaproyectoAndIndTipomoneda(String codCatProyecto, String tipoMoneda);
}
