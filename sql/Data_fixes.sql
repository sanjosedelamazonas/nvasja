--UPDATE [dbo].[scp_bancocabecera] SET [cod_destino] ='00000000' WHERE [cod_destino] IN ('V128', 'V032', '45604121', '291', 'V90')
UPDATE [dbo].[scp_bancodetalle] SET cod_tipomov='' WHERE cod_tipomov IS NULL
UPDATE [dbo].[scp_plancontable] SET [ind_tipomoneda] = 'E' WHERE txt_anoproceso IN ('2014','2015','2016','2017','2018') AND flg_movimiento='N' AND cod_ctacontable='1060103'
UPDATE [dbo].[scp_plancontable] SET [ind_tipomoneda] = 'N' WHERE txt_anoproceso IN ('2014','2015','2016','2017','2018') AND flg_movimiento='N' AND cod_ctacontable='1060101'
update  [SCP].[dbo].[scp_proyecto]  set fec_final=CAST('2018-12-31' AS date) where cod_proyecto in ('005028','005029','005031','005035','005051','005054','005055','005056','005057','005058','005059','005060','005061','005064','005065','005066','005069','005070','005071','005072','005073','005074','005076','005077','023631','023211','023081','023020','023019','023012','023112','050012','006066','005002','005022');
update [SCP].[dbo].[scp_cajabanco]   set cod_destino='05344434'	where cod_destino='V057';
update [SCP].[dbo].[scp_cajabanco]    set cod_destino='10053714841' where cod_destino='V046';
update [SCP].[dbo].[scp_cajabanco] set cod_destino='V090' where cod_destino='V90';
update [SCP].[dbo].[scp_cajabanco]  set cod_destino='10053444348' where cod_destino='1005344434';
update [SCP].[dbo].[scp_cajabanco] set cod_destino='V090' FROM [SCP].[dbo].[scp_cajabanco]  where cod_destino in (SELECT sc.cod_destino FROM scp_cajabanco sc LEFT JOIN scp_destino sd on sc.cod_destino = sd.cod_destino WHERE sd.cod_destino IS NULL);


 update [SCP].[dbo].[scp_bancodetalle] set cod_destino='V090'  where cod_destino='45604121';
 update [SCP].[dbo].[scp_bancodetalle] set cod_destino='05344434'  where cod_destino='V057';
  update [SCP].[dbo].[scp_bancodetalle] set cod_destino='10053714841'  where cod_destino='V046';
   update [SCP].[dbo].[scp_bancodetalle] set cod_destino='V022'  where cod_destino='179';
   update [SCP].[dbo].[scp_bancodetalle] set cod_destino='20100017491'  where cod_destino='2010017491';
   update [SCP].[dbo].[scp_bancodetalle] set cod_destino='V345'  where cod_destino='125';
    update [SCP].[dbo].[scp_bancodetalle] set cod_destino='V268'  where cod_destino='V90';
  update [SCP].[dbo].[scp_bancodetalle] set cod_destino='05311887'  where cod_destino='291';
    update [SCP].[dbo].[scp_bancodetalle] set cod_destino='20100043140'  where cod_destino='40519799';
	  update [SCP].[dbo].[scp_bancodetalle] set cod_destino='20100043140'  where cod_destino='';

  update [SCP].[dbo].[scp_bancodetalle] set cod_destino='20100043140'  where cod_destino in ('167','20493573643','V128');



  --------------------------------------CONFIGURACION ----------------------
   update [SCP].[dbo].[scp_plancontable]
    set flg_estadocuenta='1' where txt_anoproceso='2019'
    and cod_ctacontable in ('1040101','1040102','1040104','1040105','1040106','1040201','1040202','1040203','1060101','1060102','1060103','1060108','1060109');
