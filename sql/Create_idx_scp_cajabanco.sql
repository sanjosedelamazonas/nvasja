SET ANSI_PADDING ON

CREATE INDEX scp_cajabanco_cod_destino_cod_destinoitem_index ON SCP.dbo.scp_cajabanco (cod_destino, cod_destinoitem)
CREATE INDEX scp_cajabanco_cod_transcorrelativo_index ON SCP.dbo.scp_cajabanco (cod_transcorrelativo)
CREATE INDEX scp_cajabanco_fec_fecha_index ON SCP.dbo.scp_cajabanco (fec_fecha DESC)
CREATE INDEX scp_cajabanco_fec_fecha_cod_proyecto_cod_tipomoneda_flg_enviado_index ON SCP.dbo.scp_cajabanco (fec_fecha, cod_proyecto, cod_tipomoneda, flg_enviado)

GO
