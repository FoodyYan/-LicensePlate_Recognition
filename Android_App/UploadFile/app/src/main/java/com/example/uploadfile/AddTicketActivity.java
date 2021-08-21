package com.example.uploadfile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddTicketActivity extends AppCompatActivity {
    String insertUrl;
    private Spinner spinnerWhy;
    private Spinner spinnerWhere;
    String result;
    String reason_add_ticket = "";
    String app;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    String a;
    String b;
    String c;
    String d;
    String e;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);

        /*setContentView(R.layout.addticket);
        Bundle bundle = getIntent().getExtras();
        TextView textView=(TextView) findViewById(R.id.textView2);
        textView.setText(bundle.getString("carPlate"));
        //spinnerWhy = findViewById(R.id.spinner1);
        spinnerWhere = findViewById(R.id.spinner2);*/
        setContentView(R.layout.addticket);

        TextView carplatetextview = findViewById(R.id.textView2);
        Intent intent = this.getIntent();
        app = intent.getStringExtra("carPlate");
        insertUrl = intent.getStringExtra("postUrl");
        carplatetextview.setText(app);
        //spinnerWhy = findViewById(R.id.spinner1);
        spinnerWhere = findViewById(R.id.spinner2);

    }

    public void submit(View view) {
        //String postUrl = "http://192.168.42.146:5000/insert";
        String postUrl = insertUrl + "insert";
        //String[] why = getResources().getStringArray(R.array.spinnerwhy);
        String[] where = getResources().getStringArray(R.array.spinnerwhere);
        //int why_id = spinnerWhy.getSelectedItemPosition();
        int where_id = spinnerWhere.getSelectedItemPosition();
        if (a != null) {
            if (reason_add_ticket == "") {
                reason_add_ticket = a;
            } else {
                reason_add_ticket = reason_add_ticket + "," + a;
            }
        }
        if (b != null) {
            if (reason_add_ticket == "") {
                reason_add_ticket = b;
            } else {
                reason_add_ticket = reason_add_ticket + "," + b;
            }
        }
        if (c != null) {
            if (reason_add_ticket == "") {
                reason_add_ticket = c;
            } else {
                reason_add_ticket = reason_add_ticket + "," + c;
            }
        }
        if (d != null) {
            if (reason_add_ticket == "") {
                reason_add_ticket = d;
            } else {
                reason_add_ticket = reason_add_ticket + "," + d;
            }
        }
        if (e != null) {
            if (reason_add_ticket == "") {
                reason_add_ticket = e;
            } else {
                reason_add_ticket = reason_add_ticket + "," + e;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        //先行定義時間格式
        Date dt = new Date();
        //取得現在時間
        String dts = sdf.format(dt);

        FormBody formBody = new FormBody.Builder()
                .add("Time", dts)
                .add("Carplate", app)
                .add("Reason", reason_add_ticket)
                .add("Place", where[where_id])
                .build();

        /*Request.Builder requestbuilder = new Request.Builder();
        requestbuilder.url(postUrl).post(requestBody);
        Request request = requestbuilder.build();*/
        //String reason = why[why_id];
        //String place = where[where_id];
        returnthevalue(postUrl, formBody);


    }
        /*String[] why = getResources().getStringArray(R.array.spinnerwhy);
        String[] where = getResources().getStringArray(R.array.spinnerwhere);

        int why_id = spinnerWhy.getSelectedItemPosition();
        int where_id = spinnerWhere.getSelectedItemPosition();

        Toast.makeText(this, why[why_id], Toast.LENGTH_LONG).show();
*/


    private void returnthevalue(String postUrl, FormBody formBody) {
        OkHttpClient client = new OkHttpClient();
        /*FormBody.Builder formBuilder = new FormBody.Builder();
        //formBuilder.add("carplate",carplate);
        formBuilder.add("why",why);
        formBuilder.add("where",where);*/
        Request request = new Request.Builder().
                url(postUrl).
                post(formBody).
                build();
        //Call call = client.newCall(request);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddTicketActivity.this, "錯誤", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            result = response.body().string();
                            if (result != null) {
                                Toast.makeText(AddTicketActivity.this, "新增成功", Toast.LENGTH_LONG).show();
                                AddTicketActivity.this.finish();
                            }
                            /*Intent intent = new Intent();
                            intent.setClass(AddTicketActivity.this, MainActivity.class);  //setClass(來源, 目的地)
                            startActivity(intent);*/
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void cancel(View view) {
        AddTicketActivity.this.finish();
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkBox1:
                if (checked) {
                    a = "未依規定停放";
                } else {
                    a = null;
                }
                break;
            case R.id.checkBox2:
                if (checked) {
                    b = "未貼通行證";
                } else {
                    b = null;
                }
                break;
            case R.id.checkBox3:
                if (checked) {
                    c = "佔用殘障車位";
                } else {
                    c = null;
                }
                break;
            case R.id.checkBox4:
                if (checked) {
                    d = "未戴安全帽";
                } else {
                    d = null;
                }
                break;
            case R.id.checkBox5:
                if (checked) {
                    e = "其他";
                } else {
                    e = null;
                }
                break;
        }
    }


}
