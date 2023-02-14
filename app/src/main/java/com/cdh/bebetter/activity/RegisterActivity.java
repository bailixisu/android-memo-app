package com.cdh.bebetter.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;

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

public class RegisterActivity extends AppCompatActivity {

    EditText account;
    EditText password;
    EditText username;
    private OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(@Nullable @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.regsiter_acitivity);
        initViews();

    }

    private void initViews(){
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        TextView register = findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountStr = account.getText().toString();
                String passwordStr = password.getText().toString();
                String usernameStr = username.getText().toString();
                if(accountStr.equals("") || passwordStr.equals("") || usernameStr.equals("")){
                    Toast.makeText(RegisterActivity.this,"输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                register(getLoginJson());

            }
        });
        ImageView back = findViewById(R.id.back_icon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private JSONObject getLoginJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account",account.getText().toString());
            jsonObject.put("password",password.getText().toString());
            jsonObject.put("username",username.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void register(JSONObject jsonObject){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Log.d("TAG", "register: "+jsonObject.toString());
        Request request = new Request.Builder()
                .url(Constant.BASE_URL+"/register")
                .post(body)
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "onFailure: "+e);
                    Looper.prepare();
                    AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("注册失败")//设置标题
                            .setMessage("请检查网络是否正常，在重新注册")//设置要显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();//销毁对话框
                                }
                            }).create();//create（）方法创建对话框
                    dialog.show();//显示对话框
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    if (result.equals("false")){
                        Looper.prepare();
                        AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册失败")//设置标题
                                .setMessage("该账号已被注册")//设置要显示的内容
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();//销毁对话框
                                    }
                                }).create();//create（）方法创建对话框
                        dialog.show();//显示对话框
                        Looper.loop();
                        return;
                    }
                    SharedPreferences.Editor editor = getSharedPreferences(Constant.SHAREPREFERCES_FILENAME,MODE_PRIVATE).edit();
                    editor.putString("account",account.getText().toString());
                    editor.putString("password",password.getText().toString());
                    editor.putString("username",username.getText().toString());
                    editor.apply();
                    Looper.prepare();
                    AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("注册成功")//设置标题
                            .setMessage("")//设置要显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();//销毁对话框
                                }
                            }).create();//create（）方法创建对话框
                    dialog.show();//显示对话框
                    Looper.loop();
                    response.body().close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
