SET ANSI_PADDING ON

CREATE INDEX scp_bancocabecera_cod_destino_index ON SCP.dbo.scp_bancocabecera (cod_destino)
CREATE INDEX scp_bancocabecera_fec_fecha_index ON SCP.dbo.scp_bancocabecera (fec_fecha DESC)
CREATE INDEX scp_bancocabecera_fec_fecha_cod_ctacontable_index ON SCP.dbo.scp_bancocabecera (fec_fecha DESC, cod_ctacontable)

GO
