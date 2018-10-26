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

ALTER TABLE dbo.scp_cajabanco SET (LOCK_ESCALATION = TABLE)
GO
ALTER TABLE dbo.scp_cajabanco
	DROP CONSTRAINT PK_scp_cajabanco
GO
ALTER TABLE dbo.scp_cajabanco SET (LOCK_ESCALATION = TABLE)
GO

Alter TABLE [dbo].[scp_cajabanco] add
	[cod_cajabanco] [int] IDENTITY(1,1) NOT NULL ,
	[cod_transcorrelativo] [varchar](255) NULL DEFAULT '',
	[cod_tipomov] [int] NULL DEFAULT 0
;
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
;
GO
;
create function [dbo].[usp_vsj_cajabanco_gen_correlativo](@id int)
returns char(8)
as
begin
return right('00000000' + convert(varchar(10), @id), 8)
end
GO
;
create trigger [dbo].[vsj_scp_cajabanco_insert] on [dbo].[scp_cajabanco]
after insert as
update
    scp_cajabanco
set
    scp_cajabanco.txt_correlativo = dbo.usp_vsj_cajabanco_gen_correlativo(scp_cajabanco.cod_cajabanco)
from
    scp_cajabanco
inner join
    inserted on scp_cajabanco.cod_cajabanco = inserted.cod_cajabanco
GO
