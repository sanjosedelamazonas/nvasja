CREATE OR ALTER PROCEDURE [dbo].[usp_scp_vsj_fix_num_item_bancodetalle]
AS
BEGIN
    DECLARE @COD_CAB INT
    DECLARE @NUM_ITEM INT
    DECLARE @LAST_NUM_ITEM INT
    DECLARE myCursor CURSOR FOR
        SELECT num_item, cod_bancocabecera from scp_bancodetalle group by cod_bancocabecera, num_item having count(num_item)>1
    OPEN myCursor
    FETCH NEXT FROM myCursor INTO @NUM_ITEM, @COD_CAB
    WHILE (@@FETCH_STATUS = 0)
        BEGIN
            SELECT TOP 1 @LAST_NUM_ITEM=num_item FROM scp_bancodetalle WHERE cod_bancocabecera=@COD_CAB ORDER BY num_item DESC
            SET @LAST_NUM_ITEM = @LAST_NUM_ITEM +1
            UPDATE scp_bancodetalle SET num_item=@LAST_NUM_ITEM WHERE cod_bancocabecera=@COD_CAB AND num_item=@NUM_ITEM AND ind_tipocuenta='3';
            PRINT(STR(@COD_CAB)+'  '+ STR(@NUM_ITEM) + ' ' + STR(@LAST_NUM_ITEM))
            FETCH NEXT FROM myCursor INTO @NUM_ITEM, @COD_CAB
        END
    CLOSE myCursor
    DEALLOCATE myCursor
    --SELECT * FROM scp_bancodetalle WHERE NUM_ITEM=@NUM_ITEM AND cod_bancocabecera=@COD_CAB
END
