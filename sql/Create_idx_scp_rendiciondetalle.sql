DROP INDEX IF EXISTS scp_rendiciondetalle_cod_destino__index ON SCP.dbo.scp_rendiciondetalle ;
GO
CREATE INDEX scp_rendiciondetalle_cod_destino__index ON SCP.dbo.scp_rendiciondetalle (cod_destino);
GO


ALTER TABLE SCP.dbo.scp_rendiciondetalle DROP CONSTRAINT PK_scp_rendiciondetalle
ALTER TABLE SCP.dbo.scp_rendiciondetalle ADD CONSTRAINT scp_rendiciondetalle_pk UNIQUE (txt_anoproceso, cod_filial, cod_rendicioncabecera, cod_mes, cod_origen, cod_comprobante, num_nroitem)
