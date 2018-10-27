/****** Script for SelectTopNRows command from SSMS  ******/
delete from [dbo].[vsj_configuractacajabanco]
insert into [dbo].[vsj_configuractacajabanco]
(      [cod_tipocuenta]
      ,[txt_tipocuenta]
      ,[cod_ctacontablecaja]
      ,[cod_ctacontablegasto]
      ,[cod_ctaespecial]
      ,[para_caja]
      ,[para_banco]
      ,[para_proyecto]
      ,[para_tercero]
      ,[activo])
SELECT [cod_tipocuenta]
      ,[txt_tipocuenta]
      ,[cod_ctacontablecaja]
      ,[cod_ctacontablegasto]
      ,[cod_ctaespecial]
, case [ind_tipocuenta] --para caja
			when 0 then 1
			when 1 then 1
			when 2 then 0
			when 3 then 0
end
, case [ind_tipocuenta] --[para_banco]
			when 0 then 0
			when 1 then 0
			when 2 then 1
			when 3 then 1
end
, case [ind_tipocuenta] --[para_proyecto]
			when 0 then 1
			when 1 then 0
			when 2 then 1
			when 3 then 0
end
, case [ind_tipocuenta] --[para_tercero]
			when 0 then 0
			when 1 then 1
			when 2 then 0
			when 3 then 1
end
,1
FROM [dbo].[scp_configuractacajabanco]
where txt_anoproceso='2018'
