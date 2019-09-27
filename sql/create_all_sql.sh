#!/bin/bash
cat Alter_*.sql > all.sql
echo "PRINT '1. Alter DONE'" >> all.sql
cat Create_vsj_*.sql >> all.sql
echo "PRINT '2. Create_vsj DONE'" >> all.sql
#cat Create_function_*.sql >> all.sql
#cat Create_trigger_*.sql >> all.sql
cat Create_usp_*.sql >> all.sql
echo "PRINT '3. Cr usp'" >> all.sql
cat Create_proc_1_*.sql >> all.sql
echo "PRINT '4. Cr proc1'" >> all.sql
cat Create_proc_usp*.sql >> all.sql
echo "PRINT '5. Cr proc_usp*; GO;'" >> all.sql
cat Create_proc_fun*.sql >> all.sql
cat Create_function_scp_*.sql >> all.sql
echo "PRINT '7. Cr function_scp'" >> all.sql
cat Insert_into*.sql >> all.sql
echo "PRINT '8. Cr vsj'" >> all.sql
echo "PRINT '9. FIXING DATA'" >> all.sql
cat Data_fixes.sql >> all.sql
echo "PRINT '10. DATA FIXED'" >> all.sql
OLD="USE"
NEW="--USE"
f=all.sql
TFILE=tmp_all.sql
sed "s/$OLD/$NEW/g" "$f" > $TFILE && mv $TFILE "$f"
