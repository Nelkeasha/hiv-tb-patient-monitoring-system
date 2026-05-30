#Admin
email: admin@hivtb.rw
password: Admin@2026
#CHW
"email": "jean@hivtb.rw",
"password": "r! "

"Missed dose marking is fully automatic. A Spring Boot scheduled job runs at window_close_time for each active confirmation_logs record. If confirmed_at is still null when the window closes, the system automatically sets is_missed to true, is_within_window to false, updates the medication_records adherence percentage, triggers the AI risk score recalculation, and refreshes the CHW priority list. No manual CHW action is needed or expected."