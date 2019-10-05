CREATE INDEX scp_bancocabecera_cod_destino_index ON SCP.dbo.scp_bancocabecera (cod_destino)
CREATE INDEX scp_bancocabecera_fec_fecha_index ON SCP.dbo.scp_bancocabecera (fec_fecha DESC)
CREATE INDEX scp_bancocabecera_fec_fecha_cod_ctacontable_index ON SCP.dbo.scp_bancocabecera (fec_fecha DESC, cod_ctacontable)
CREATE INDEX scp_bancocabecera_ano_flg_enviado_cod_tipomoneda_cod_mes_txt_correlativo_ind_tipocuenta_index ON SCP.dbo.scp_bancocabecera (txt_anoproceso, flg_enviado, cod_tipomoneda, cod_mes, txt_correlativo, ind_tipocuenta)

GO
