--SELECT * FROM CHANGETABLE(CHANGES scp_comprobantecabecera, 0) AS ChTbl
SELECT * FROM CHANGETABLE(CHANGES scp_comprobantedetalle, 0) AS ChTbl

--SELECT NewTableVersion =  CHANGE_TRACKING_CURRENT_VERSION()