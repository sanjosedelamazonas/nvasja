USE [SCP]
GO

/****** Object:  UserDefinedFunction [dbo].[usp_vsj_cajabanco_gen_correlativo]    Script Date: 09/12/2016 10:04:20 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


create function [dbo].[usp_vsj_cajabanco_gen_correlativo](@id int) 
returns char(8) 
as 
begin 
return right('00000000' + convert(varchar(10), @id), 8) 
end


GO

