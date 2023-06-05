# Sleep Tracker Database Class

import sqlite3

class sleep_db_class:

    def __init__(self, db_name, table_name):
        self.database_name = db_name
        self.table_name = table_name

    def open_db_connection(self):
        self.db_connection = sqlite3.connect(self.database_name)
        self.db_cursor = self.db_connection.cursor()
    
    def close_db_connection(self):
        self.db_connection.commit()
        self.db_connection.close()        
    
    # Sleep - Create Table 
    def create_sleep_table(self): # database schima
        self.open_db_connection()
        self.db_cursor.execute(f''' SELECT count(name) FROM sqlite_master WHERE type='table' AND name='{str(self.table_name)}' ''')
        #if the count is 1, then table exists
        if self.db_cursor.fetchone()[0]==1 : {
            print(f'{str(self.table_name)} table exists')
        }
        else:
            # self.db_cursor.execute(f'DROP TABLE IF EXISTS {str(self.table_name)}')
            self.db_cursor.execute(f'CREATE TABLE {str(self.table_name)} (datetime NUMBERIC, sleep_sex TEXT, sleep_exercise TEXT, sleep_bath_one TEXT, sleep_bath_two TEXT, sleep_home TEXT, sleep_slept_day TEXT, sleep_screen TEXT, sleep_meds TEXT, sleep_drink TEXT, sleep_drive TEXT, sleep_work TEXT, sleep_alarm TEXT, sleep_eat TEXT, unknown_x TEXT)')
        self.close_db_connection()

    # Wakeup - Create Table
    def create_wakeup_table(self):
        self.open_db_connection()
        self.db_cursor.execute(f''' SELECT count(name) FROM sqlite_master WHERE type='table' AND name='{str(self.table_name)}' ''')
        #if the count is 1, then table exists
        if self.db_cursor.fetchone()[0]==1 : {
            print(f'{str(self.table_name)} table exists')
        }
        else:
            # self.db_cursor.execute(f'DROP TABLE IF EXISTS {str(self.table_name)}')
            self.db_cursor.execute(f'CREATE TABLE {str(self.table_name)} (datetime NUMBERIC, wakeup_dream TEXT, wakeup_alarm TEXT, sleep_quality TEXT)')
        self.close_db_connection()

    # Sleep Duration - Create Table
    def create_sleep_duration_table(self):
        self.open_db_connection()
        self.db_cursor.execute(f''' SELECT count(name) FROM sqlite_master WHERE type='table' AND name='{str(self.table_name)}' ''')
        #if the count is 1, then table exists
        if self.db_cursor.fetchone()[0]==1 : {
            print(f'{str(self.table_name)} table exists')
        }
        else:
            self.db_cursor.execute(f'CREATE TABLE {str(self.table_name)} (datetime NUMBERIC, duration REAL)')
        self.close_db_connection()

    # Sleep - Insert Record    
    def insert_record_sleep_table(self, datetime, sleep_sex, sleep_exercise, sleep_bath_one, sleep_bath_two, sleep_home, sleep_slept_day, sleep_screen, sleep_meds, sleep_drink, sleep_drive, sleep_work, sleep_alarm, sleep_eat, unknown_x):
        self.open_db_connection()
        self.db_cursor.execute(f"INSERT INTO {str(self.table_name)} VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", (datetime, sleep_sex, sleep_exercise, sleep_bath_one, sleep_bath_two, sleep_home, sleep_slept_day, sleep_screen, sleep_meds, sleep_drink, sleep_drive, sleep_work, sleep_alarm, sleep_eat, unknown_x))
        self.close_db_connection()
    
    # Wakeup - Insert Record
    def insert_record_wakeup_table(self, datetime, wakeup_dream, wakeup_alarm, sleep_quality):
        self.open_db_connection()
        self.db_cursor.execute(f"INSERT INTO {str(self.table_name)} VALUES (?,?,?,?)", (datetime, wakeup_dream, wakeup_alarm, sleep_quality))
        self.close_db_connection()

    # Sleep Duration - Insert Record
    def insert_record_sleep_duration_table(self, datetime, duration):
        self.open_db_connection()
        self.db_cursor.execute(f"INSERT INTO {str(self.table_name)} VALUES (?,?)", (datetime, duration))
        self.close_db_connection()
        
    def show_all_records(self):
        self.open_db_connection()
        self.db_cursor.execute(f"SELECT rowid, * FROM {str(self.table_name)} ORDER BY rowid") 
        self.items = self.db_cursor.fetchall()
        self.close_db_connection()
        return self.items

    def lookup_last_seven_days(self):
        self.open_db_connection()
        self.db_cursor.execute(f"SELECT datetime FROM {str(self.table_name)} ORDER BY datetime DESC LIMIT 7")
        self.items = self.db_cursor.fetchall()
        self.close_db_connection()
        return self.items
    
    def lookup_by_limit(self, limit):
        self.open_db_connection()
        self.db_cursor.execute(f"SELECT datetime FROM {str(self.table_name)} ORDER BY rowid DESC LIMIT (?)", (limit,))
        self.items = self.db_cursor.fetchall()
        self.close_db_connection()
        return self.items

    def lookup_by_limit_sleep_duration(self, limit):
        self.open_db_connection()
        self.db_cursor.execute(f"SELECT datetime, duration FROM {str(self.table_name)} ORDER BY rowid DESC LIMIT (?)", (limit,))
        self.items = self.db_cursor.fetchall()
        self.close_db_connection()
        return self.items        

    def lookup_quality(self):
        self.open_db_connection()
        self.db_cursor.execute(f"SELECT sleep_quality FROM {str(self.table_name)} ORDER BY rowid DESC LIMIT 1")
        self.items = self.db_cursor.fetchall()
        self.close_db_connection()
        if not self.items:
            self.items = "Empty"
            return self.items
        else:
            return self.items

