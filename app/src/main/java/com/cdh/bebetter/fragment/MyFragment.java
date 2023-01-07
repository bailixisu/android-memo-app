package com.cdh.bebetter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cdh.bebetter.R;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.adapter.MemoSortDatabaseAdapter;
import com.cdh.bebetter.adapter.MyLocationDatabaseAdapter;
import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.dao.MyLocation;
import com.cdh.bebetter.dao.SortMemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    DatabaseAdapter databaseAdapter;
    MemoSortDatabaseAdapter memoSortDatabaseAdapter;
    MyLocationDatabaseAdapter myLocationDatabaseAdapter;
    public MyFragment() {
        // Required empty public constructor
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
                        JSONArray jsonArray = new JSONArray(result);;
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