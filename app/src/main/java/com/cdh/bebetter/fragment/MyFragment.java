package com.cdh.bebetter.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.request.RequestOptions;
//import com.cdh.bebetter.Manifest;
import com.cdh.bebetter.R;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.adapter.MemoSortAdapter;
import com.cdh.bebetter.adapter.MemoSortDatabaseAdapter;
import com.cdh.bebetter.adapter.MyLocationDatabaseAdapter;
import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.dao.MyLocation;
import com.cdh.bebetter.dao.SortMemo;
import com.cdh.bebetter.utils.BitmapUtils;
import com.cdh.bebetter.utils.CameraUtils;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.drawee.view.SimpleDraweeView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OkHttpClient client = new OkHttpClient();
    PopupWindow popupWindow;
    //存储拍完照后的图片
    private File outputImagePath;
    //启动相机标识
    public static final int TAKE_PHOTO = 1;
    //启动相册标识
    public static final int SELECT_PHOTO = 2;
    DatabaseAdapter databaseAdapter;
    MemoSortDatabaseAdapter memoSortDatabaseAdapter;
    MyLocationDatabaseAdapter myLocationDatabaseAdapter;
    String avatarSrc;

    //图片控件
    private ImageView ivHead;
    //Base64
    private String base64Pic;
    //拍照和相册获取图片的Bitmap
    private Bitmap orc_bitmap;

    //Glide请求图片选项配置
//    private RequestOptions requestOptions = RequestOptions.circleCropTransform()
//            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
//            .skipMemoryCache(true);//不做内存缓存

    public MyFragment() {
        // Required empty public constructor
    }
    /**
     * 拍照
     */
    private void takePhoto() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        outputImagePath = new File(getContext().getExternalCacheDir(),
                filename + ".jpg");
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(getContext(), outputImagePath);
        // 开启一个带有返回值的Activity，请求码为TAKE_PHOTO
        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
    }

    private void openAlbum() {
        startActivityForResult(CameraUtils.getSelectPhotoIntent(), SELECT_PHOTO);
    }

    private void showMsg(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG);
    }


    //初始化拍照弹框
    private void initPopWindow(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.photo_dialog_layout,null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,826);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        setBackgroundAlpha(0.8f);

        TextView tvTakePictures = view.findViewById(R.id.tv_take_pictures);
        TextView tvOpenAlbum = view.findViewById(R.id.tv_open_album);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);

        //拍照
        tvTakePictures.setOnClickListener(v -> {
            takePhoto();
            popupWindow.dismiss();
        });
        //打开相册
        tvOpenAlbum.setOnClickListener(v -> {
            openAlbum();
            popupWindow.dismiss();
        });
        //取消
        tvCancel.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha;
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        Fresco.initialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        initViews(view);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.open();
        memoSortDatabaseAdapter = new MemoSortDatabaseAdapter(getContext());
        memoSortDatabaseAdapter.open();
        myLocationDatabaseAdapter = new MyLocationDatabaseAdapter(getContext());
        myLocationDatabaseAdapter.open();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        avatarSrc = sharedPreferences.getString("avatar","");
        if (!avatarSrc.equals("")){
            displayImage(avatarSrc);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseAdapter.close();
        memoSortDatabaseAdapter.close();
        myLocationDatabaseAdapter.close();
    }

    void initViews(View view) {
        LinearLayout saveDataToCloud = view.findViewById(R.id.saveDataToCloud);
        saveDataToCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addToCloud();
                    }
                }).start();
                Toast.makeText(getContext(), "备份成功", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout loadFromCloud = view.findViewById(R.id.loadFromCloud);

        loadFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadDataFromCloud();
                    }
                }).start();
                Toast.makeText(getContext(), "恢复成功", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout exit = view.findViewById(R.id.exit);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivHead = view.findViewById(R.id.ivHead);
        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopWindow();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //拍照后返回
            case TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        avatarSrc = getTakePhotoPath(data);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    displayImage(avatarSrc);
                }
                break;
            //打开相册后返回
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4及以上系统使用这个方法处理图片
                        avatarSrc = CameraUtils.getImageOnKitKatPath(data, getContext());
                    } else {
                        avatarSrc = CameraUtils.getImageBeforeKitKatPath(data, getContext());
                    }
                    //显示图片
                    displayImage(avatarSrc);
                }
                break;
            default:
                break;
        }
        Log.d("TAG", "onActivityResult: "+avatarSrc);
        SharedPreferences.Editor editor = getContext().getSharedPreferences("user",Context.MODE_PRIVATE).edit();
        editor.putString("avatar",avatarSrc);
        editor.commit();
    }

    private void displayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            //压缩图片
            orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
            ivHead.setBackground(new BitmapDrawable(createCircleImage(orc_bitmap)));
            //转Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);

        } else {
            showMsg("图片获取失败");
        }
    }


    public String getTakePhotoPath(Intent data) throws FileNotFoundException {
        Bitmap photo = data.getParcelableExtra("data");
        FileOutputStream fileOutputStream = null;
        try {
            String saveDir = getActivity().getExternalFilesDir(null)+ "/be_better";
            File dir = new File(saveDir);
            if (!dir.exists()){
                if (!dir.mkdir()){
                    Log.d("TAG", "onStart: 失败了");
                }
            }
            SimpleDateFormat t = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String filename = "MT" + (t.format(new Date())) + ".jpg";
            /**新建文件*/
            File file = new File(saveDir, filename);
            /***打开文件输出流*/
            fileOutputStream = new FileOutputStream(file);
            /**
             * 对应Bitmap的compress(Bitmap.CompressFormat format, int quality, OutputStream stream)方法中第一个参数。
             * CompressFormat类是个枚举，有三个取值：JPEG、PNG和WEBP。其中，
             * PNG是无损格式（忽略质量设置），会导致方法中的第二个参数压缩质量失效，
             * JPEG不解释，
             * 而WEBP格式是Google新推出的，据官方资料称“在质量相同的情况下，WEBP格式图像的体积要比JPEG格式图像小40%。
             */
            photo.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            /***相片的完整路径*/
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static Bitmap createCircleImage(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        float raduis = 8000 * 0.5f;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setColor(Color.RED);
        //画布设置遮罩效果
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        //处理图像数据
        Bitmap bitmap = Bitmap.createBitmap(width, height, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        //bitmap的显示由画笔paint来决定
        canvas.drawCircle(0,0, raduis, paint);
        return bitmap;
    }

    void loadDataFromCloud(){
        Request request = new Request.Builder()
                .url("http://192.168.1.3:8080/memos")
                .get()
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(result);;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Memo memo = new Memo();
                            memo.setId(jsonObject.getLong("id"));
                            memo.setContent(jsonObject.getString("content"));
                            memo.setStartTime(jsonObject.getString("start_time"));
                            memo.setCompleteTime(jsonObject.getString("complete_time"));
                            memo.setNote(jsonObject.getString("note"));
                            memo.setSort(jsonObject.getString("sort"));
                            memo.setColor(jsonObject.getInt("color"));
                            memo.setStatus(jsonObject.getInt("status"));
                            memo.setLike(jsonObject.getInt("like"));
                            memo.setCirculate(jsonObject.getInt("circulate"));
                            memo.setDeadline(jsonObject.getString("deadline"));
                            databaseAdapter.memoInsert(memo);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    response.body().close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        request = new Request.Builder()
                .url("http://192.168.1.3:8080/sort_memos")
                .get()
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            SortMemo sortMemo = new SortMemo();
                            sortMemo.setSortText(jsonObject.getString("sortText"));
                            sortMemo.setSortIconColor(jsonObject.getInt("sort_icon_color"));
                            sortMemo.setSortBackgroundColor(jsonObject.getInt("sort_background_color"));
                            memoSortDatabaseAdapter.memoSortInsert(sortMemo);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    response.body().close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        request = new Request.Builder()
                .url("http://192.168.1.3:8080/locations")
                .get()
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(result);;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            MyLocation myLocation = new MyLocation();
                            myLocation.setId(jsonObject.getLong("id"));
                            myLocation.setLatitude(jsonObject.getDouble("latitude"));
                            myLocation.setLongitude(jsonObject.getDouble("longitude"));
                            myLocation.setTime(jsonObject.getString("time"));
                            myLocation.setMemoId(jsonObject.getLong("memoId"));
                            myLocationDatabaseAdapter.myLocationInsert(myLocation);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    response.body().close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void saveDataToCloud(JSONObject jsonObject) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url("http://192.168.1.3:8080/memos")
                .post(body)
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    response.body().close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveSortMemoToCloud(JSONObject jsonObject) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url("http://192.168.1.3:8080/sort_memo")
                .post(body)
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    response.body().close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void saveLocationToCloud(JSONObject jsonObject) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url("http://192.168.1.3:8080/location")
                .post(body)
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    response.body().close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    void addToCloud() {
        List<Memo> memos = databaseAdapter.memoFindAllRecords();
        Integer count = 0;
        for (Memo memo : memos) {
            JSONObject memoJson = new JSONObject();
            try {
                memoJson.put("id", memo.getId());
                memoJson.put("content",memo.getContent());
                memoJson.put("start_time",memo.getStartTime());
                memoJson.put("deadline",memo.getDeadline());
                memoJson.put("complete_time",memo.getCompleteTime());
                memoJson.put("note",memo.getNote());
                memoJson.put("sort",memo.getSort());
                memoJson.put("color",memo.getColor());
                memoJson.put("status",memo.getStatus());
                memoJson.put("like",memo.getLike());
                memoJson.put("circulate",memo.getCirculate());
                memoJson.put("identifier","18607010580");
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveDataToCloud(memoJson);
        }

        List<SortMemo> memoSorts = memoSortDatabaseAdapter.memoFindAllRecords();
        for (SortMemo memoSort : memoSorts) {
            JSONObject memoSortJson = new JSONObject();
            try {
                memoSortJson.put("sortText", memoSort.getSortText());
                memoSortJson.put("sort_icon_color",memoSort.getSortIconColor());
                memoSortJson.put("sort_background_color",memoSort.getSortBackgroundColor());
                memoSortJson.put("identifier","18607010580");
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveSortMemoToCloud(memoSortJson);
        }

        List<MyLocation> myLocations = myLocationDatabaseAdapter.myLocationFindAllRecords();
        for (MyLocation myLocation : myLocations) {
            JSONObject myLocationJson = new JSONObject();
            try {
                myLocationJson.put("id", myLocation.getId());
                myLocationJson.put("latitude",myLocation.getLatitude());
                myLocationJson.put("longitude",myLocation.getLongitude());
                myLocationJson.put("time",myLocation.getTime());
                myLocationJson.put("memoId",myLocation.getMemoId());
                myLocationJson.put("identifier","18607010580");
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveLocationToCloud(myLocationJson);
        }
    }

}