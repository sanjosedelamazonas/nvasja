USE [SCP]
GO

/****** Object:  Trigger [dbo].[vsj_cajabanco_insert]    Script Date: 09/12/2016 10:03:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


--alter [dbo].[vsj_cajabanco] add usp_vsj_cajabanco_gen_correlativo varchar(10) 
create trigger [dbo].[vsj_cajabanco_insert] on [dbo].[vsj_cajabanco]
after insert as 
update 
    vsj_cajabanco 
set 
    vsj_cajabanco.txt_correlativo = dbo.usp_vsj_cajabanco_gen_correlativo(vsj_cajabanco.cod_cajabanco) 
from 
    vsj_cajabanco 
inner join 
    inserted on vsj_cajabanco.cod_cajabanco = inserted.cod_cajabanco
    

GO

