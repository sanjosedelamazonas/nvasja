SET ANSI_PADDING ON


CREATE INDEX scp_comprobantedetalle_txt_anoproceso_cod_mes_cod_origen_cod_comprobante_cod_ctacontable_index ON SCP.dbo.scp_comprobantedetalle (txt_anoproceso, cod_mes, cod_origen, cod_comprobante, cod_ctacontable)

GO
