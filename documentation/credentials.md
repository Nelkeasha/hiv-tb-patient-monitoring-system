#Admin
email: admin@hivtb.rw
password: Admin@2026
#CHW
"email": "jean@hivtb.rw",
"password": "Jean@2026"

"Missed dose marking is fully automatic. 
A Spring Boot scheduled job runs at window_close_time for each active confirmation_logs record. 
If confirmed_at is still null when the window closes, the system automatically sets is_missed to true, is_within_window to false, updates the medication_records adherence percentage, triggers the AI risk score recalculation, and refreshes the CHW priority list. No manual CHW action is needed or expected."

Host: dpg-d8dbsq4m0tmc73dpb0vg-a
Port: 5432
Database: hivtb_db
Username: hivtb_db_user
Password: 6gEdOnNADgsu0IxwneyaLe7OHNasfXJR