SET ANSI_PADDING ON

CREATE INDEX scp_bancodetalle_cod_bancocabecera_index ON SCP.dbo.scp_bancodetalle (cod_bancocabecera)
CREATE INDEX scp_bancodetalle_txt_anoproceso_cod_mes_ind_tipocuenta_txt_correlativo_index ON SCP.dbo.scp_bancodetalle (txt_anoproceso, cod_mes, ind_tipocuenta, txt_correlativo)

GO
