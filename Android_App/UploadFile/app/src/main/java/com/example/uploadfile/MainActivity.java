package com.example.uploadfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.os.Environment.DIRECTORY_DCIM;


public class MainActivity extends AppCompatActivity {

    String selectedImagePath;
    String carPlate;
    String dialogmessage;
    String[] result;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_IMAGE = 3;
    private ImageView showImage;
    private List<String> list1 = new ArrayList<String>();
    private List<String> list2 = new ArrayList<String>();


    String TAG = MainActivity.class.getSimpleName() + "My";
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    //EditText ipv4AddressView;

    //圖片路徑
    private Uri imageUri;

    //圖片名稱
    private String filename;
    private TextView lbl_imgpath;
    private EditText imgPath;
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    String postUrl = "";
    private final String PERMISSION_WRITE_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private final String PERMISSION_OPEN_CAMERA = "android.permission.CAMERA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow);
        Toolbar toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        showImage = (ImageView) findViewById(R.id.imgpic);
        imgPath = (EditText) findViewById(R.id.imgPath);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        viewPager = findViewById(R.id.view_pager);
        //ipv4AddressView=(EditText) findViewById(R.id.IPAddress);


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FirstFragment());
        fragments.add(new SecondFragment());

        FragmentAdapter adapter = new FragmentAdapter(fragments, getSupportFragmentManager());
        viewPager.setAdapter(adapter);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                // 跳轉指定頁面：Fragment
                switch (menuId) {
                    case R.id.action_homepage:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_record:
                        if (postUrl == "") {
                            viewPager.setCurrentItem(0);

                        } else {
                            viewPager.setCurrentItem(1);
                            catchData();
                        }
                        break;
                }
                return false;
            }
        });
        // ViewPager 滑動事件監聽
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                if (position == 1) {
                    if (postUrl == "") {

                        viewPager.setCurrentItem(0);

                    } else {
                        catchData();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"0624082@nkust.edu.tw"});  //developer 's email
                Email.putExtra(Intent.EXTRA_SUBJECT,
                        "Add your Subject"); // Email 's Subject
                Email.putExtra(Intent.EXTRA_TEXT, "Dear Developer ," + "");  //Email 's Greeting text
                startActivity(Intent.createChooser(Email, "Send Feedback:"));

                return true;

            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //getAcitvity是舊用法
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("製作團隊：顏嘉慧、許家盈、張詠晴、古宣鴻\n指導老師：黃承龍\n學校地址：824高雄市燕巢區卓越路2 號")
                        .setTitle("關於");
                //String 加上去

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
                    }
                });
              /*  builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "CANCEL", Toast.LENGTH_LONG).show();
                    }
                });*/

                // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;


            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void connectServer(View v) {
        EditText ipv4AddressView = findViewById(R.id.IPAddress);
        String ipv4Address = ipv4AddressView.getText().toString();
        EditText portNumberView = findViewById(R.id.portNumber);
        String portNumber = portNumberView.getText().toString();
        postUrl = "http://" + ipv4Address + ":" + portNumber + "/";
        // String postUrl= "http://163.18.22.32:8004/api_classify_image_upload";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        TextView responseText = findViewById(R.id.responseText);
        responseText.setText("Please wait ...");

        postRequest(postUrl, postBodyImage);


    }


    public void setDialogmessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(dialogmessage)
                .setTitle(R.string.dialog_title2)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, AddTicketActivity.class);  //setClass(來源, 目的地)
                        intent.putExtra("carPlate", carPlate);
                        intent.putExtra("postUrl", postUrl);
                        startActivity(intent);


                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();

    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("Failed to Connect to Server");
                    }
                });
            }


            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        try {
                            responseText.setText(response.body().string());
                            result = responseText.getText().toString().split(",");
                            carPlate = result[0];
                            if (carPlate != null) {
                                dialogmessage = "車牌:" + carPlate + "\n " + result[1];
                                setDialogmessage();

                            }
                            // Toast.makeText(MainActivity.this, carPlate, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                });
            }
        });
    }

    public void selectImage(View v) {
        if (!hasPermission()) {
            if (needCheckPermission()) {
                //如果須要檢查權限，由於這個步驟要等待使用者確認，
                //所以不能立即執行儲存的動作，
                //必須在 onRequestPermissionsResult 回應中才執行
                return;
            }
        } else {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, SELECT_IMAGE);
        }

    }

    public void openCamera(View v) {

        //啟動照相功能
        // Intent intent =
        if (!hasCameraPermission()) {
            if (needCheckCameraPermission()) {
                //如果須要檢查權限，由於這個步驟要等待使用者確認，
                //所以不能立即執行儲存的動作，
                //必須在 onRequestPermissionsResult 回應中才執行
                return;
            }
        }
        Intent intent = new Intent(); //呼叫照相機
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //指定圖片輸出地方。
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //startActivity(intent);
        //拍完照startActivityForResult() 結果返回onActivityResult() 函數
        startActivityForResult(intent, CROP_PHOTO);
    }


    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        //super.onActivityResult(reqCode, resCode, data);t

        super.onActivityResult(reqCode, resCode, data);
        if (resCode != RESULT_OK) {
            Toast.makeText(MainActivity.this, "ActivityResult resultCode error", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.v("TestFile",
                        "SD card is not avaiable/writeable right now.");
                return;
            }

        }
        switch (reqCode) {
            case CROP_PHOTO:
                try {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                    FileOutputStream b = null;
                    //File file = new File("/sdcard/myImage/");
                    File path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
                    // file.mkdirs();// 创建文件夹
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date(System.currentTimeMillis());
                    filename = format.format(date);
                    String fileName = path + filename + ".jpg";
                    EditText imgPath = findViewById(R.id.imgPath);
                    imgPath.setText(fileName);
                    Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();

                    try {
                        b = new FileOutputStream(fileName);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                        selectedImagePath = fileName;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            b.flush();
                            b.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    ((ImageView) findViewById(R.id.imgpic)).setImageBitmap(bitmap);// 将图片显示在ImageView里
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                break;
            case SELECT_IMAGE:
                if (resCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    selectedImagePath = getPath(getApplicationContext(), uri);
                    EditText imgPath = findViewById(R.id.imgPath);
                    //
                    imgPath.setText(selectedImagePath);
                    Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
                    ((ImageView) findViewById(R.id.imgpic)).setImageBitmap(bm);// 将图片显示在ImageView里
                    Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();
                } else {

                    super.onActivityResult(reqCode, resCode, data);
                }

                break;
            default:
                break;
        }


/*

        if(resCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedImagePath = getPath(getApplicationContext(), uri);
            EditText imgPath = findViewById(R.id.imgPath);
            imgPath.setText(selectedImagePath);
            Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();
        }else{

            super.onActivityResult(reqCode, resCode, data); }*/
    }

    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageState() + "/" + split[1];
                    //return Environment.getExternalStorageDirectory() + "/" + split[1];
                    //return  Environment.getExternalStorageState()+"/"+split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);

            }

            // MediaProvider

            else if (isMediaDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);

                final String[] split = docId.split(":");

                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {

                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                } else if ("video".equals(type)) {

                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (ActivityCompat.checkSelfPermission(this, PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }

        return true;
    }

    private boolean hasCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (ActivityCompat.checkSelfPermission(this, PERMISSION_OPEN_CAMERA) == PackageManager.PERMISSION_GRANTED);
        }

        return true;
    }

    private boolean needCheckPermission() {
        //MarshMallow(API-23)之後要在 Runtime 詢問權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {PERMISSION_WRITE_STORAGE};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
            return true;
        }

        return false;
    }

    private boolean needCheckCameraPermission() {
        //MarshMallow(API-23)之後要在 Runtime 詢問權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {PERMISSION_OPEN_CAMERA};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
            return true;
        }

        return false;
    }
/*
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_homepage:


                    return true;
                case R.id.action_record:

                    return true;
            }
            return false;
        }
    };*/


    private void catchData() {
        String catchData = postUrl + "json";
        ProgressDialog dialog = ProgressDialog.show(this, "讀取中"
                , "請稍候", true);

        new Thread(() -> {
            try {
                URL url = new URL(catchData);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String line = in.readLine();
                StringBuffer json = new StringBuffer();
                while (line != null) {
                    json.append(line);
                    line = in.readLine();
                }
                JSONArray jsonArray = new JSONArray(String.valueOf(json));
                if (arrayList != null && arrayList.size() > 0) {
                    arrayList.clear();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String date = jsonObject.getString("date");
                    String car_no = jsonObject.getString("car_no");
                    String bad_record = jsonObject.getString("bad_record");
                    String bad_place = jsonObject.getString("bad_place");
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("date", "違規日期：" + date);
                    hashMap.put("car_no", "車牌號碼：" + car_no);
                    hashMap.put("bad_record", "違規記錄：" + bad_record);
                    hashMap.put("bad_place", "違規地點：" + bad_place);
                    arrayList.add(hashMap);

                }

                Log.d(TAG, "" + "catchData: " + arrayList);
                runOnUiThread(() -> {
                    dialog.dismiss();
                    RecyclerView recyclerView;
                    MyListAdapter myAdapter;
                    recyclerView = findViewById(R.id.rec_recycleView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                    myAdapter = new MyListAdapter();
                    recyclerView.setAdapter(myAdapter);

                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHodler> {

        @NonNull
        @Override
        public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_item, parent, false);

            return new ViewHodler(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHodler holder, int position) {
            holder.tvcarno.setText(arrayList.get(position).get("car_no"));
            holder.tvdate.setText(arrayList.get(position).get("date"));
            holder.tvbadrecord.setText(arrayList.get(position).get("bad_record"));
            holder.tvbadplace.setText(arrayList.get(position).get("bad_place"));
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHodler extends RecyclerView.ViewHolder {
            private TextView tvcarno, tvdate, tvbadrecord, tvbadplace;

            public ViewHodler(@NonNull View itemView) {
                super(itemView);
                tvcarno = itemView.findViewById(R.id.textView_car_no);
                tvdate = itemView.findViewById(R.id.textView_date);
                tvbadrecord = itemView.findViewById(R.id.textView_bad_record);
                tvbadplace = itemView.findViewById(R.id.textView_bad_place);
            }
        }
    }


}



