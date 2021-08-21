## 校園車牌辨識系統

iParking是基於學校所使用的傳統車證進行開發及改良，內建了「車牌辨識系統」功能，讓使用者可以使用手機快速地擷取並辨識車牌，並結合「新增違規記錄」功能，提供使用者辨識完後立刻進行開單的動作，減少整體流程所耗費的時間。



### Flask Server

位置：FlaskServer/flask_link.py

+　負責接收車牌，辨識車牌號碼後用車牌號碼為檔名儲存，去資料庫驗證有無登入車牌號碼，將結果回傳Client端。

### Android APP

位置：Android_App/UploadFile

+ App名稱：iParking

+ Android目標版本:9.0

+ 使用firebase管理使用者
+ 請求框架：OkHttp3
