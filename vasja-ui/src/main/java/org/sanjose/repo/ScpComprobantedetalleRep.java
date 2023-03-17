package org.sanjose.repo;

import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpComprobantedetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScpComprobantedetalleRep extends JpaRepository<ScpComprobantedetalle, Long> {

//    List<ScpComprobantedetalle> findById_TxtAnoprocesoAndId_CodMesAndId_CodOrigenAndId_CodComprobanteAndCodCtacontable(
//            String s, String s2, String s3, String s4, String s5);

    List<ScpComprobantedetalle> findById_TxtAnoprocesoAndId_CodMesAndId_CodOrigenAndId_CodComprobante(
            String s, String s2, String s3, String s4);

    List<ScpComprobantedetalle> findById_TxtAnoprocesoAndId_CodMesAndId_CodOrigenAndId_CodComprobanteAndCodCtacontable(
            String s, String s2, String s3, String s4, String ctacontable);

    List<ScpComprobantedetalle> findByFecComprobanteBetween(Date from, Date to);

    List<ScpComprobantedetalle> findByFecComprobanteBetweenAndCodTerceroIsIn(Date from, Date to, List<String> s);

    List<ScpComprobantedetalle> findByFecComprobanteBetweenAndCodTerceroIsInAndCodCtacontableStartingWithOrderByFecComprobanteAscId_CodComprobanteAsc(Date from, Date to, List<String> s, String s2);
}
