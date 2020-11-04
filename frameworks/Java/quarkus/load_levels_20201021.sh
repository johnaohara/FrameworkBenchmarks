echo "Defining load levels..."

declare -A DB_LOAD

DB_LOAD[hibernate-1-0ms]="10000,15000,17000,20000,22000,32000,34000"
DB_LOAD[hibernate-1-1ms]="1000,3000,3200,3400,3600"
DB_LOAD[hibernate-2-0ms]="10000,20000,30000,32000,34000,36000,38000,40000"
DB_LOAD[hibernate-2-1ms]="1000,3000,3400,3600,3800"

DB_LOAD[hibernate-reactive-1-0ms]="10000,17000,19000,20000,28000,30000,32000,34000"
DB_LOAD[hibernate-reactive-1-1ms]="1000,3200,3400,3600,3800"
DB_LOAD[hibernate-reactive-2-0ms]="10000,20000,30000,32000,34000,36000,38000,40000"
DB_LOAD[hibernate-reactive-2-1ms]="1000,3000,3400,3600,3800"

DB_LOAD[hibernate-reactive-routes-blocking-1-0ms]=""
DB_LOAD[hibernate-reactive-routes-blocking-1-1ms]=""
DB_LOAD[hibernate-reactive-routes-blocking-2-0ms]=""
DB_LOAD[hibernate-reactive-routes-blocking-2-1ms]=""

DB_LOAD[pgclient-1-0ms]=""
DB_LOAD[pgclient-1-1ms]=""
DB_LOAD[pgclient-2-0ms]=""
DB_LOAD[pgclient-2-1ms]=""

declare -A UPDATE_LOAD

UPDATE_LOAD[hibernate-1-0ms]="50,80,100,500,700,720,740"
UPDATE_LOAD[hibernate-2-0ms]="50,80,100,500,700,720,740"
UPDATE_LOAD[hibernate-1-1ms]="50,60,80,100,500,600,620,640"
UPDATE_LOAD[hibernate-2-1ms]="50,60,80,100,500,600,620,640"

UPDATE_LOAD[hibernate-reactive-1-0ms]="50,80,100,500,700,720,740"
UPDATE_LOAD[hibernate-reactive-2-0ms]="50,80,100,500,700,720,760"
UPDATE_LOAD[hibernate-reactive-1-1ms]="50,80,90,100,500,700,720"
UPDATE_LOAD[hibernate-reactive-2-1ms]="50,80,90,100,500,600,620,680"

UPDATE_LOAD[pgclient-1-0ms]=""
UPDATE_LOAD[pgclient-2-0ms]=""
UPDATE_LOAD[pgclient-1-1ms]=""
UPDATE_LOAD[pgclient-2-1ms]=""

UPDATE_LOAD[hibernate-reactive-routes-blocking-1-0ms]=""
UPDATE_LOAD[hibernate-reactive-routes-blocking-2-0ms]=""
UPDATE_LOAD[hibernate-reactive-routes-blocking-1-1ms]=""
UPDATE_LOAD[hibernate-reactive-routes-blocking-2-1ms]=""

echo "Defining load levels...Done!"