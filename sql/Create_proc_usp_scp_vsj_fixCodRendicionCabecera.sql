CREATE PROCEDURE [dbo].[usp_scp_vsj_FixCodRendicionCabecera]
    @cod_destino varchar(11)
AS
  SET NOCOUNT ON;

BEGIN
    UPDATE [dbo].[scp_rendiciondetalle]
    SET cod_rendicioncabecera=cab.cod_rendicioncabecera
    FROM
        [dbo].[scp_rendicioncabecera] cab,
        [dbo].[scp_rendiciondetalle]rend
    WHERE rend.cod_mes=cab.cod_mes AND
        rend.cod_origen=cab.cod_origen AND
        rend.cod_comprobante=cab.cod_comprobante AND
        rend.txt_anoproceso=cab.txt_anoproceso AND rend.cod_destino LIKE @cod_destino
END