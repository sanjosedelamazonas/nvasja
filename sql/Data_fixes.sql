UPDATE [SCP].[dbo].[scp_bancocabecera] SET [cod_destino] ='00000000' WHERE [cod_destino] IN ('V128', 'V032', '45604121', '291', 'V90')
UPDATE [SCP].[dbo].[scp_bancodetalle] SET cod_tipomov='' WHERE cod_tipomov IS NULL
UPDATE [SCP].[dbo].[scp_plancontable] SET [ind_tipomoneda] = 'E' WHERE txt_anoproceso IN ('2015','2016','2017','2018') AND flg_movimiento='N' AND cod_ctacontable='1060103'
UPDATE [SCP].[dbo].[scp_plancontable] SET [ind_tipomoneda] = 'N' WHERE txt_anoproceso IN ('2018') AND flg_movimiento='N' AND cod_ctacontable='1060101'