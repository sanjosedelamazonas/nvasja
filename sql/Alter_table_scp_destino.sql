ALTER TABLE SCP.dbo.scp_destino ADD txt_email varchar(100) NULL
ALTER TABLE SCP.dbo.scp_destino ADD flg_activo bit DEFAULT 'true'
ALTER TABLE SCP.dbo.scp_destino ADD flg_enviar_reporte bit DEFAULT 'true'
ALTER TABLE SCP.dbo.scp_destino ADD txt_usuario varchar(20) DEFAULT ''


UPDATE scp_destino SET scp_destino.txt_usuario='' WHERE scp_destino.txt_usuario IS NULL
UPDATE scp_destino SET scp_destino.txt_email='' WHERE scp_destino.txt_email IS NULL
UPDATE scp_destino SET scp_destino.flg_activo='true'
UPDATE scp_destino SET scp_destino.flg_enviar_reporte='false'
