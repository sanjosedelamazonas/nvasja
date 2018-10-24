package org.sanjose.repo;

import org.sanjose.model.VsjConfiguracioncaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VsjConfiguracioncajaRep extends JpaRepository<VsjConfiguracioncaja, Long> {

    List<VsjConfiguracioncaja> findByCodProyectoAndIndTipomoneda(String codProyecto, Character tipoMoneda);

    List<VsjConfiguracioncaja> findByCodDestinoAndIndTipomoneda(String codDestino, Character tipoMoneda);

    List<VsjConfiguracioncaja> findByIndTipomonedaAndCodDestinoAndCodProyectoAndCodCategoriaproyecto(Character tipoMoneda, String codDestino, String codProyecto, String codCatProyecto);

    List<VsjConfiguracioncaja> findByCodCategoriaproyectoAndIndTipomoneda(String codCatProyecto, Character tipoMoneda);
}
