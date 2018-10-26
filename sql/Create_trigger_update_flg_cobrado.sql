alter trigger [dbo].[vsj_scp_comprobantedetalle_banco_flg_cobrado] on [dbo].[scp_comprobantedetalle]
after update as

DECLARE TrigTempUpdate_Cursor CURSOR FOR
select
      case when i.flg_chequecobrado='1' then 1 else 0 end,
      i.cod_mescobr,
      i.cod_origen,
      i.cod_comprobante,
      i.cod_mes,
      i.txt_anoproceso
from inserted i inner join deleted d on i.cod_origen=d.cod_origen and i.cod_comprobante=d.cod_comprobante and i.cod_mes=d.cod_mes
and i.txt_anoproceso=d.txt_anoproceso and i.cod_filial=d.cod_filial and i.num_nroitem=d.num_nroitem
where i.cod_origen='02' and i.flg_chequecobrado='1' and i.flg_chequecobrado<>d.flg_chequecobrado

DECLARE @ind_cobrado bit,
        @cod_mescobr char(2),
        @cod_origen varchar(6),
        @cod_comprobante varchar(6),
        @cod_mes varchar(2),
        @txt_anoproceso varchar(4),
        @cod_bancocabecera int

OPEN TrigTempUpdate_Cursor;

FETCH NEXT FROM TrigTempUpdate_Cursor INTO @ind_cobrado, @cod_mescobr, @cod_origen, @cod_comprobante, @cod_mes, @txt_anoproceso

WHILE @@FETCH_STATUS = 0

BEGIN

    update scp_bancocabecera set ind_cobrado=@ind_cobrado, cod_mescobrado=@cod_mescobr
    where cod_origenenlace=@cod_origen and cod_comprobanteenlace=@cod_comprobante and cod_mes=@cod_mes and txt_anoproceso=@txt_anoproceso

    --  PRINT 'Processing ID: ' + CONVERT(varchar(10), @cod_bancocabecera) + ' ind_cobr: ' + convert(varchar(10),@ind_cobrado) + ' mes: ' + @cod_mescobr

    FETCH NEXT FROM TrigTempUpdate_Cursor INTO  @ind_cobrado, @cod_mescobr, @cod_origen, @cod_comprobante, @cod_mes, @txt_anoproceso

END;

CLOSE TrigTempUpdate_Cursor;

DEALLOCATE TrigTempUpdate_Cursor;
