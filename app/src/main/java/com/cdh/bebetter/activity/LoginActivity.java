package com.cdh.bebetter.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.dao.Memo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    EditText account;
    EditText password;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(@Nullable @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        initViews();

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHAREPREFERCES_FILENAME,MODE_PRIVATE);
        account.setText(sharedPreferences.getString("account",""));
        password.setText(sharedPreferences.getString("password",""));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    private void initViews(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHAREPREFERCES_FILENAME,MODE_PRIVATE);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(getLoginJson());
            }
        });
        TextView register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private JSONObject getLoginJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account",account.getText().toString());
            jsonObject.put("password",password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void login(JSONObject jsonObject){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Log.d("TAG", "login: "+jsonObject.toString());
        Request request = new Request.Builder()
                .url(Constant.BASE_URL+"/login")
                .post(body)
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "onFailure: "+e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    loginResponse(result);
                    response.body().close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loginResponse(String result){
        if (result.equals("false")){
            Looper.prepare();
            AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("登录失败")//设置标题
                    .setMessage("请检查账号或密码是否正确，在重新登录")//设置要显示的内容
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();//销毁对话框
                        }
                    }).create();//create（）方法创建对话框
            dialog.show();//显示对话框
            Looper.loop();
        }else {
            SharedPreferences.Editor editor = getSharedPreferences(Constant.SHAREPREFERCES_FILENAME,MODE_PRIVATE).edit();
            editor.putString("account",account.getText().toString());
            editor.putString("password",password.getText().toString());
            editor.putString("username",result);
            editor.putBoolean("isLogin",true);
            editor.apply();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }
}
