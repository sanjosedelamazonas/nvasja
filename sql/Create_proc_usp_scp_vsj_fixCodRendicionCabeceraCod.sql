CREATE PROCEDURE [dbo].[usp_scp_vsj_FixCodRendicionCabeceraCod]
    @cod_rend_cab int
    AS
BEGIN
UPDATE [dbo].[scp_rendiciondetalle]
SET cod_rendicioncabecera=cab.cod_rendicioncabecera
FROM
    [dbo].[scp_rendicioncabecera] cab,
    [dbo].[scp_rendiciondetalle]rend
WHERE rend.cod_mes=cab.cod_mes AND
    rend.cod_origen=cab.cod_origen AND
    rend.cod_comprobante=cab.cod_comprobante AND
    rend.txt_anoproceso=cab.txt_anoproceso AND
    cab.cod_rendicioncabecera=@cod_rend_cab;
END