import sqlite3
from flask import jsonify
 
def dict_factory(cursor, row):
    d = {}
    for idx, col in enumerate(cursor.description):
        d[col[0]] = row[idx]
    return d

def json():
    connection = sqlite3.connect('C:/Users/Cathy/Desktop/FlaskServer/identify.db')
    connection.row_factory = dict_factory
 
    cursor = connection.cursor()
 
    cursor.execute("select * from record order by date DESC")
 
# fetch all or one we'll go for all.
 
    results = cursor.fetchall()
    connection.close()
    return jsonify(results)