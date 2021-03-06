GO
DROP INDEX IF EXISTS scp_comprobantedetalle_txt_anoproceso_cod_mes_cod_origen_cod_comprobante_cod_ctacontable_index ON SCP.dbo.scp_comprobantedetalle ;
GO
CREATE INDEX scp_comprobantedetalle_txt_anoproceso_cod_mes_cod_origen_cod_comprobante_cod_ctacontable_index ON SCP.dbo.scp_comprobantedetalle (txt_anoproceso, cod_mes, cod_origen, cod_comprobante, cod_ctacontable)
GO
DROP INDEX IF EXISTS scp_comprobantedetalle_ano_filial_mes_origen_comprobante_ctacontable_proyecto_tipomoneda_idx ON SCP.dbo.scp_comprobantedetalle ;
GO
CREATE INDEX scp_comprobantedetalle_ano_filial_mes_origen_comprobante_ctacontable_proyecto_tipomoneda_idx ON SCP.dbo.scp_comprobantedetalle (txt_anoproceso DESC, cod_filial, cod_mes, cod_origen, cod_comprobante, cod_ctacontable, cod_proyecto, cod_tipomoneda)
GO
DROP INDEX IF EXISTS scp_comprobantedetalle_cod_ctacontable_fec_comprobante_index ON SCP.dbo.scp_comprobantedetalle ;
GO
CREATE INDEX scp_comprobantedetalle_cod_ctacontable_fec_comprobante_index ON SCP.dbo.scp_comprobantedetalle (cod_ctacontable, fec_comprobante);
GO
DROP INDEX IF EXISTS scp_comprobantedetalle_txt_anoproceso_ctacontable_proyecto_origen_mes_tipomoneda_index ON SCP.dbo.scp_comprobantedetalle ;
GO
CREATE INDEX scp_comprobantedetalle_txt_anoproceso_ctacontable_proyecto_origen_mes_tipomoneda_index ON SCP.dbo.scp_comprobantedetalle (txt_anoproceso, cod_ctacontable, cod_proyecto, cod_origen, cod_mes, cod_tipomoneda);
GO


ALTER TABLE SCP.dbo.scp_comprobantecabecera ALTER COLUMN txt_glosa varchar(90)