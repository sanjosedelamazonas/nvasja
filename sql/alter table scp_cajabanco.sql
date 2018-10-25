Alter TABLE [dbo].[scp_cajabanco] add
	[cod_cajabanco] [int] IDENTITY(1,1) NOT NULL ,
	[cod_transcorrelativo] [varchar](255) NULL DEFAULT '',
	[cod_tipomov] [int] NULL DEFAULT 0
;
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
ALTER TABLE dbo.scp_cajabanco ADD CONSTRAINT
	PK_scp_cajabanco PRIMARY KEY CLUSTERED
	(
	cod_cajabanco
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_cajabanco SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
;
update [dbo].[scp_cajabanco] set
	[cod_transcorrelativo] ='',
	[cod_tipomov] =0
	where 1=1;