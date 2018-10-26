/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.scp_bancocabecera
	DROP CONSTRAINT PK_scp_bancocabecera
GO
ALTER TABLE dbo.scp_bancocabecera SET (LOCK_ESCALATION = TABLE)
GO
COMMIT

/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.scp_bancocabecera ADD
	cod_bancocabecera [int] IDENTITY(1,1) NOT NULL ,
	ind_cobrado bit NULL DEFAULT 0,
	flg_Anula char(1) NULL DEFAULT '0',
	cod_mescobrado char(2) NULL
GO
ALTER TABLE dbo.scp_bancocabecera ADD CONSTRAINT
	PK_scp_bancocabecera PRIMARY KEY CLUSTERED
	(
	cod_bancocabecera
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_bancocabecera SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
update [dbo].[scp_bancocabecera] set
	[ind_cobrado ] = 0,
	[flg_Anula] =0,
	[cod_mescobrado]=''
	where 1=1

-- BANCO DETALLE

/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.scp_bancodetalle ADD
	cod_tipomov int NULL,
	cod_bancocabecera int NOT NULL DEFAULT 0
GO
ALTER TABLE dbo.scp_bancodetalle
	DROP CONSTRAINT PK_scp_bancodetalle
GO

UPDATE bd SET bd.cod_bancocabecera = cb.cod_bancocabecera FROM  scp_bancodetalle AS bd INNER JOIN scp_bancocabecera AS cb ON
cb.txt_anoproceso = bd.txt_anoproceso AND cb.flg_saldo=bd.flg_saldo AND
cb.ind_tipocuenta=bd.ind_tipocuenta AND cb.txt_correlativo=bd.txt_correlativo

ALTER TABLE dbo.scp_bancodetalle ADD CONSTRAINT
	PK_scp_bancodetalle PRIMARY KEY CLUSTERED
	(
	num_item,
	cod_bancocabecera
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_bancodetalle SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
;
update [dbo].[scp_bancodetalle] set
	[cod_tipomov] =0
	where 1=1;