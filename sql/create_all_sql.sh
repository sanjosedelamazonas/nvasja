#!/bin/bash
cat Alter_*.sql > all.sql
cat Create_vsj_*.sql >> all.sql
#cat Create_function_*.sql >> all.sql
#cat Create_trigger_*.sql >> all.sql

cat Create_usp_*.sql >> all.sql
cat Create_proc_1_*.sql >> all.sql
cat Create_proc_usp*.sql >> all.sql
cat Create_proc_fun*.sql >> all.sql
cat Create_function_scp_*.sql >> all.sql
cat Insert_into*.sql >> all.sql

OLD="USE"
NEW="--USE"
f=all.sql
TFILE=tmp_all.sql
sed "s/$OLD/$NEW/g" "$f" > $TFILE && mv $TFILE "$f"
