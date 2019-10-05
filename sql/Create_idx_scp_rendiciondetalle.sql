DROP INDEX IF EXISTS scp_rendiciondetalle_cod_destino__index ON SCP.dbo.scp_rendiciondetalle ;
GO
CREATE INDEX scp_rendiciondetalle_cod_destino__index ON SCP.dbo.scp_rendiciondetalle (cod_destino);
GO

