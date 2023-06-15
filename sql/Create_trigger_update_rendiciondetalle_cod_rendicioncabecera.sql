SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

drop trigger if exists [dbo].[vsj_scp_rendiciondetalle_cod_rendicioncabecera]
go

CREATE TRIGGER [dbo].[vsj_scp_rendiciondetalle_cod_rendicioncabecera] on [dbo].[scp_rendiciondetalle]
  AFTER INSERT
  AS
BEGIN
  DECLARE @cod_origen varchar(6),
  @cod_comprobante varchar(6),
  @cod_mes varchar(2),
  @txt_anoproceso varchar(4),
  @cod_rendicioncabecera int,
  @cod_cabecera_in_detalle int,
  @num_nroitem numeric(4)
  SET NOCOUNT ON;
  SELECT
         @cod_mes=inserted.cod_mes,
         @cod_origen=inserted.cod_origen,
         @cod_comprobante=inserted.cod_comprobante,
         @txt_anoproceso=inserted.txt_anoproceso,
         @num_nroitem=inserted.num_nroitem,
         @cod_cabecera_in_detalle=inserted.cod_rendicioncabecera
  FROM inserted;
  IF (@cod_cabecera_in_detalle IS NULL) OR (@cod_cabecera_in_detalle=0)
  BEGIN

    SELECT @cod_rendicioncabecera=cod_rendicioncabecera
    FROM
         scp_rendicioncabecera
    WHERE
        cod_mes=@cod_mes AND
        cod_origen=@cod_origen AND
        cod_comprobante=@cod_comprobante AND
        txt_anoproceso=@txt_anoproceso
    UPDATE scp_rendiciondetalle
    SET cod_rendicioncabecera=@cod_rendicioncabecera
    WHERE
        cod_mes=@cod_mes AND
        cod_origen=@cod_origen AND
        cod_comprobante=@cod_comprobante AND
        txt_anoproceso=@txt_anoproceso AND
        num_nroitem=@num_nroitem
  END
END
GO
