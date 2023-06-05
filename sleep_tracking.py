# Sleep Tracking App
# Author: omrsangx
import string
import re
import json
import random
#import datetime
from datetime import datetime
# import time
from flask import Flask, render_template, Response, request, json, escape
from sleep_tracking_db_class import sleep_db_class # Importing the class for the database object
import sqlite3

app = Flask(__name__)

database_name_sleep = 'sleep_database_pro_app.db'
database_table_name = 'sleep_table'
wakeup_table_name = 'wakeup_table'
sleep_duration_table_name = 'duration_table'


# Creating database object
db_obj_sleep = sleep_db_class(database_name_sleep,database_table_name)
db_obj_wakeup = sleep_db_class(database_name_sleep,wakeup_table_name)
db_obj_duration = sleep_db_class(database_name_sleep,sleep_duration_table_name)

# Creating database sleep and wake tables
db_obj_sleep.create_sleep_table()
db_obj_wakeup.create_wakeup_table()
db_obj_duration.create_sleep_duration_table()

def change_to_datetime_obj(value):
    datetime_object = datetime.fromisoformat(str(value))
    return str(datetime_object)

# Index route, function
@app.route("/")
def index():
    html_file = "index.html" 

    def get_month_day_function(value):
        change_back_to_date = datetime.fromisoformat(str(value))
        return str(change_back_to_date.month) + "/" + str(change_back_to_date.day)
    
    limit_value = 7
    sleep_duration_seven_days = db_obj_duration.lookup_by_limit_sleep_duration(limit_value)
    get_month_day_list = list()
    duration_hours_list = list()
    get_sleep_quality = db_obj_wakeup.lookup_quality()
    sleep_quality_string = re.sub('[(,\')]', '', str(get_sleep_quality[0]))
    print(type(sleep_quality_string))
    for row in reversed(sleep_duration_seven_days):
        get_month_day_list.append(get_month_day_function(row[0]))
        duration_hours_list.append(row[1])

    sleep_durations_data = json.dumps(duration_hours_list)
    sleep_duration_dates_labels = json.dumps(get_month_day_list)
    print("sleep duration:", sleep_durations_data)
    print("days of the week:", sleep_duration_dates_labels)
    return render_template(html_file, data=sleep_durations_data, labels=sleep_duration_dates_labels, sleep_quality_str=sleep_quality_string)  

@app.route("/wba-sleep")
def wba_sleep():
    html_file = "sleep.html" 
    return render_template(html_file)

@app.route("/wba-wakeup")
def wba_wakeup():
    html_file = "wakeup.html" 
    return render_template(html_file)

# Sleep route, function
@app.route("/sleepform", methods=["POST"])
def sleep_func():
    sleep_date_time = datetime.now()
    sleep_sex =  str(escape(request.form.get('sleep_sex')))
    sleep_exercise = str(escape(request.form.get('sleep_exercise')))
    sleep_bath_one = str(escape(request.form.get('sleep_bath_one')))
    sleep_bath_two = str(escape(request.form.get('sleep_bath_two')))
    sleep_home = str(escape(request.form.get('sleep_home')))
    sleep_slept_day = str(escape(request.form.get('sleep_slept_day')))
    sleep_screen = str(escape(request.form.get('sleep_screen')))
    sleep_meds = str(escape(request.form.get('sleep_meds')))
    sleep_drink = str(escape(request.form.get('sleep_drink')))
    sleep_drive = str(escape(request.form.get('sleep_drive')))
    sleep_work = str(escape(request.form.get('sleep_work')))
    sleep_alarm = str(escape(request.form.get('sleep_alarm')))
    sleep_eat = str(escape(request.form.get('sleep_eat')))
    unknown_x = str(escape(request.form.get('unknown_x')))
    db_obj_sleep.insert_record_sleep_table(sleep_date_time, sleep_sex, sleep_exercise, sleep_bath_one, sleep_bath_two, sleep_home, sleep_slept_day, sleep_screen, sleep_meds, sleep_drink, sleep_drive, sleep_work, sleep_alarm, sleep_eat, unknown_x)
    
    html_form_submitted = "landing_page.html"
    return render_template(html_form_submitted)

# Wakup route, function
@app.route("/wakeupform", methods=["POST"])
def wakeup_func():
    wakeup_date_time = datetime.now()

    zero_place_holder = float(0)
    limit_value = 1
    sleep_date = db_obj_sleep.lookup_by_limit(limit_value)
    get_date_db = list()
    for row in sleep_date:
        get_date_db.append(row[0])
    
    sleep_datetime_data = get_date_db[0]
    print(sleep_datetime_data)
    last_sleep_datetime = datetime.fromisoformat(str(sleep_datetime_data))
    now_datetime = datetime.now()
    sleep_duration = now_datetime - last_sleep_datetime
    total_hours_asleep = format(sleep_duration.total_seconds() / 3600, ".2f")


    if float(total_hours_asleep) < float(17):
        db_obj_duration.insert_record_sleep_duration_table(now_datetime, total_hours_asleep)
    else:
        print("log: zero place holder added to db")
        db_obj_duration.insert_record_sleep_duration_table(now_datetime, zero_place_holder)
    
    wakeup_dream = str(escape(request.form.get('wakeup_dream')))
    wakeup_alarm = str(escape(request.form.get('wakeup_alarm')))
    sleep_quality = str(escape(request.form.get('sleep_quality')))
    db_obj_wakeup.insert_record_wakeup_table(wakeup_date_time, wakeup_dream, wakeup_alarm, sleep_quality)
    html_form_submitted = "landing_page.html"
    return render_template(html_form_submitted) 

if __name__ == "__main__":
    app.run()

