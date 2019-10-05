DROP INDEX IF EXISTS scp_cajabanco_cod_destino_cod_destinoitem_index ON SCP.dbo.scp_cajabanco ;
GO
CREATE INDEX scp_cajabanco_cod_destino_cod_destinoitem_index ON SCP.dbo.scp_cajabanco (cod_destino, cod_destinoitem);
GO
DROP INDEX IF EXISTS scp_cajabanco_cod_transcorrelativo_index ON SCP.dbo.scp_cajabanco ;
GO
CREATE INDEX scp_cajabanco_cod_transcorrelativo_index ON SCP.dbo.scp_cajabanco (cod_transcorrelativo)
GO
DROP INDEX IF EXISTS scp_cajabanco_fec_fecha_index ON SCP.dbo.scp_cajabanco ;
GO
CREATE INDEX scp_cajabanco_fec_fecha_index ON SCP.dbo.scp_cajabanco (fec_fecha DESC);
GO
DROP INDEX IF EXISTS scp_cajabanco_fec_fecha_cod_proyecto_cod_tipomoneda_flg_enviado_index ON SCP.dbo.scp_cajabanco;
GO
CREATE INDEX scp_cajabanco_fec_fecha_cod_proyecto_cod_tipomoneda_flg_enviado_index ON SCP.dbo.scp_cajabanco (fec_fecha, cod_proyecto, cod_tipomoneda, flg_enviado);
GO
