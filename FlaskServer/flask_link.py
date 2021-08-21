from keras.models import load_model
import cv2
from PIL import Image
import numpy as np
import shutil, os
from time import sleep 
from flask import Flask,request
import flask
import requests
import werkzeug
import os
import sys
import dosql
from dosql import obtain
import insert 
from insert import add
import jsontest
sys.path.append(r'C:/Users/Cathy/Desktop/FlaskServer')

import recogPlate

labels = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z']  #標籤值
#擷取車牌
dirname = 'recogdata'
model = load_model('carplate_model.hdf5')  #讀取模型
imgname = 'androidFlask.jpg'
detector = cv2.CascadeClassifier('haar_carplate.xml')

 
app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False
#解決json亂碼
@app.route('/', methods = ['GET', 'POST'])
def handle_request():
    imagefile = flask.request.files['image']
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    print("\nReceived image File name : " + imagefile.filename)
    imagefile.save(filename)
    img = cv2.imread(imgname)
    signs = detector.detectMultiScale(img, scaleFactor=1.1, minNeighbors=4, minSize=(20, 20))
    out = recogPlate.recognize(signs)
    put = dosql.obtain(out)
    output = out + ',' + put
    return output

@app.route("/insert", methods=["GET", "POST"])
def insert_method():
    Time = request.form['Time']
    Carplate = request.form['Carplate']
    Reason = request.form['Reason']
    Place = request.form['Place']

    add(Time,Carplate,Reason,Place)
    return "success"

@app.route("/json", methods=["GET", "POST"])
def dojson():
    return jsontest.json()

app.run(host="0.0.0.0", port=5000)