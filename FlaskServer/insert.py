import sqlite3

def add(Time,Carplate,Reason,Place):
    conn = sqlite3.connect('C:/Users/Cathy/Desktop/FlaskServer/identify.db')
    print ("Opened database successfully")
    conn.commit()


    cursor = conn.cursor()
    cursor.execute("Insert INTO record (date,car_no,bad_record,bad_place) VALUES(?,?,?,?)",(Time,Carplate,Reason,Place))
    conn.commit()

    conn.close()
    return 'insert success'