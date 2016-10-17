UPDATE [SCP].[dbo].[vsj_bancocabecera] SET [cod_destino] ='00000000' WHERE [cod_destino] IN ('V128', 'V032', '45604121', '291', 'V90')
UPDATE [SCP].[dbo].[vsj_bancodetalle] SET cod_tipomov='' WHERE cod_tipomov IS NULL
