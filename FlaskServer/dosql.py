import sqlite3

def obtain(car):
    conn = sqlite3.connect('C:/Users/Cathy/Desktop/FlaskServer/identify.db')
    print ("Opened database successfully")
    conn.commit()

    result=''
    cursor = conn.cursor()
    for row in cursor.execute("SELECT * FROM master"):
        if car == row[2]:
            result = '已申請車證'
    if result == '':
        result = '未申請車證'
    conn.commit()

    conn.close()
    return result
