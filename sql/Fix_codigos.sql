-- How many rows in caja banco will be affected  - 175
select * from scp_cajabanco where txt_anoproceso='2019' and fec_fregistro<'2019-07-10 00:00:00' and flg_enviado=0
-- How many rows in caja banco will be affected  - 34
select * from scp_bancocabecera where txt_anoproceso='2019' and fec_fregistro<'2019-07-10 00:00:00' and flg_enviado=0


--SELECT cod_bancocabecera, * FROM scp_bancocabecera;
alter table dbo.scp_cajabanco
    add tmp_cajabanco int
go
UPDATE scp_cajabanco SET tmp_cajabanco=cod_cajabanco;

--UPDATE scp_cajabanco SET tmp_cajabanco=cod_cajabanco;

--SELECT cod_cajabanco, * FROM scp_cajabanco where fec_fecha<'2019-10-07'
select * from scp_comprobantecabecera where fec_fregistro>'2019-07-10'

alter table dbo.scp_cajabanco drop constraint PK_scp_cajabanco
go

alter table dbo.scp_cajabanco
    add constraint scp_cajabanco_pk
        unique (tmp_cajabanco)
go

alter table dbo.scp_cajabanco
    drop cod_cajabanco int
go


DECLARE @count_cajabancos INT
--SET @count_cajabancos =
SELECT @count_cajabancos = count(*) FROM scp_cajabanco WHERE fec_fecha<'2019-07-10'
print @count_cajabancos

alter table dbo.scp_cajabanco
    add cod_cajabanco int
go

UPDATE scp_cajabanco SET cod_cajabanco=37355-tmp_cajabanco+1 WHERE fec_fecha<'2019-07-10'

alter table dbo.scp_cajabanco drop constraint scp_cajabanco_pk
go

alter table dbo.scp_cajabanco
    alter column cod_cajabanco int NOT NULL
go

alter table dbo.scp_cajabanco alter column cod_cajabanco IDENTITY(1,1) int not null
go

create unique index scp_cajabanco_cod_cajabanco_uindex
    on dbo.scp_cajabanco (cod_cajabanco)
go

alter table dbo.scp_cajabanco
    add constraint scp_cajabanco_pk
        primary key nonclustered (cod_cajabanco)
go

SELECT CAST('000035' AS INT);

-- Przykład dla rekordu 36
select * from scp_cajabanco where tmp_cajabanco = 36
-- test changing ID value
update scp_cajabanco set cod_cajabanco=37355-36+1 where tmp_cajabanco = 36
--- see how many would have to be updated
select * from scp_cajabanco where txt_anoproceso='2019' and fec_fregistro<'2019-07-10 00:00:00' and flg_enviado=0

--
--select * from scp_comprobantecabecera where fec_fregistro>'2019-07-10 00:00:00' and cod_origen='02'
-- Find in scp_cajabanco enviados a contab after deployment
CREATE PROCEDURE [dbo].[usp_scp_vsj_delete_enviada_de_contab]
    @old_cod int,
    @new_cod int,
    @origen varchar(2),
    @fecha_min timestamp
AS

create table tmp_enviados_to_delete (cod_cajabanco int, txt_corelativo varchar(8), tmp_cajabanco int);
INSERT INTO tmp_enviados_to_delete  (cod_cajabanco, txt_corelativo, tmp_cajabanco)
    (select cod_cajabanco, txt_correlativo, tmp_cajabanco from scp_cajabanco where cod_comprobanteenlace IN
        (select cod_comprobante from scp_comprobantecabecera where fec_fregistro>'2019-07-10 00:00:00' and cod_origen='01')
AND tmp_cajabanco IN (SELECT CAST(cod_comprobante AS INT) from scp_comprobantecabecera where tmp_cajabanco<=37355 AND fec_fregistro>'2019-07-10 00:00:00' and cod_origen='01'))


delete from tmp_enviados_to_delete;
select tmp_cajabanco from tmp_enviados_to_delete;


select cod_comprobante, * from scp_comprobantecabecera where fec_fregistro>'2019-07-10 00:00:00' and cod_origen='01' and cod_comprobante
    IN (REPLICATE('0',6-LEN(RTRIM( (select tmp_cajabanco from tmp_enviados_to_delete))))) + RTRIM(Isnull((
        select tmp_cajabanco from tmp_enviados_to_delete),0))

select cod_comprobante from scp_comprobantecabecera where fec_fregistro>'2019-07-10 00:00:00' and cod_origen='01'

select * from scp_comprobantedetalle where cod_comprobante='000036' and cod_mes='10' and cod_origen='01' and fec_fregistro>'2019-07-10'

--select * from scp_cajabanco where txt_correlativo='000036'

select cod_comprobanteenlace, * from scp_cajabanco where tmp_cajabanco=36

Exec usp_scp_vsj_comprobante_change_cod '000036',


CREATE PROCEDURE [dbo].[usp_scp_vsj_comprobante_change_cod]
    @old_cod int,
    @new_cod int,
    @origen varchar(2),
    @fecha_min timestamp
AS
    SET NOCOUNT ON;
DECLARE @old_cod_str VARCHAR(6)
DECLARE @new_cod_str VARCHAR(6)

Begin transaction


Select @old_cod_str=(REPLICATE('0',6-LEN(RTRIM(@old_cod)))) + RTRIM(Isnull((@old_cod),0))
Select @new_cod_str=(REPLICATE('0',6-LEN(RTRIM(@new_cod)))) + RTRIM(Isnull((@new_cod),0))

update dbo.scp_comprobantedetalle set cod_comprobante = @new_cod_str where cod_comprobante=@old_cod_str and cod_origen=@origen and fec_fregistro>=@fecha_min
update dbo.scp_comprobantecabecera set cod_comprobante = @new_cod_str where cod_comprobante=@old_cod_str and cod_origen=@origen and fec_fregistro>=@fecha_min

commit transaction
