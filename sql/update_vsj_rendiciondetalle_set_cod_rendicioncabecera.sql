UPDATE scp_rendiciondetalle
SET cod_rendicioncabecera=cab.cod_rendicioncabecera
FROM
    scp_rendicioncabecera cab,
    scp_rendiciondetalle rend
WHERE rend.cod_mes=cab.cod_mes AND
      rend.cod_origen=cab.cod_origen AND
      rend.cod_comprobante=cab.cod_comprobante AND
      rend.txt_anoproceso=cab.txt_anoproceso;
