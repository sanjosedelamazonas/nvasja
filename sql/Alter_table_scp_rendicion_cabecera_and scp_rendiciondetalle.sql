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
ALTER TABLE [dbo].[scp_rendicioncabecera] DROP CONSTRAINT [PK_scp_rendicioncabecera] WITH ( ONLINE = OFF )
GO
ALTER TABLE dbo.scp_rendicioncabecera SET (LOCK_ESCALATION = TABLE)
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
ALTER TABLE dbo.scp_rendicioncabecera ADD
	cod_rendicioncabecera [int] IDENTITY(1,1) NOT NULL
GO
ALTER TABLE dbo.scp_rendicioncabecera ADD CONSTRAINT
	PK_scp_rendicioncabecera PRIMARY KEY CLUSTERED
	(
	cod_rendicioncabecera
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_rendicioncabecera SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
-- rendicion DETALLE

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
ALTER TABLE dbo.scp_rendiciondetalle ADD
	cod_tipomov int NULL,
	cod_rendicioncabecera int NOT NULL DEFAULT 0
GO

ALTER TABLE [dbo].[scp_rendiciondetalle] DROP CONSTRAINT [PK_scp_rendiciondetalle] WITH ( ONLINE = OFF )
GO

UPDATE bd SET bd.cod_rendicioncabecera = cb.cod_rendicioncabecera FROM scp_rendiciondetalle AS bd INNER JOIN scp_rendicioncabecera AS cb ON
cb.txt_anoproceso = bd.txt_anoproceso AND cb.cod_filial=bd.cod_filial AND
cb.cod_mes = bd.cod_mes AND cb.cod_origen =bd.cod_origen AND cb.cod_comprobante = bd.cod_comprobante

ALTER TABLE dbo.scp_rendiciondetalle ADD CONSTRAINT
	PK_scp_rendiciondetalle PRIMARY KEY CLUSTERED
	(
	num_nroitem,
	cod_rendicioncabecera
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_rendiciondetalle SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
;
update [dbo].[scp_rendiciondetalle] set
	[cod_tipomov] =0
	where 1=1;