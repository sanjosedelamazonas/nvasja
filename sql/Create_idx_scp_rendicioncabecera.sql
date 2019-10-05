DROP INDEX IF EXISTS scp_rendicioncabecera_cod_destino_index ON SCP.dbo.scp_rendicioncabecera ;
GO
CREATE INDEX scp_rendicioncabecera_cod_destino_index ON SCP.dbo.scp_rendicioncabecera (cod_destino)
GO
DROP INDEX IF EXISTS scp_rendicioncabecera_fec_comprobante_index ON SCP.dbo.scp_rendicioncabecera;
GO
CREATE INDEX scp_rendicioncabecera_fec_comprobante_index ON SCP.dbo.scp_rendicioncabecera (fec_comprobante DESC);
GO
