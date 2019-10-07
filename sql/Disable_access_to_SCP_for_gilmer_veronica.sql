update msg_acceso SET flg_acceso=0 where cod_aplicacion='SCP' and cod_rol='ROL006' and cod_opcion IN ('2.4.4','2.4.2', '2.4.3')
select * from msg_acceso where cod_aplicacion='SCP' and cod_rol='ROL006' and cod_opcion IN ('2.4.4','2.4.2', '2.4.3')

update msg_acceso SET flg_acceso=0 where cod_aplicacion='SCP' and cod_rol='ROL002' and cod_opcion IN ('2.4.1')
select * from msg_acceso where cod_aplicacion='SCP' and cod_rol='ROL002' and cod_opcion IN ('2.4.1')