package com.cdh.bebetter.fragment;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.cdh.bebetter.R;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.adapter.MyLocationDatabaseAdapter;
import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.dao.MyLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FootFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FootFragment extends Fragment implements LocationSource, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MyLocationDatabaseAdapter myLocationDatabaseAdapter;
    MapView mapView;
    AMap aMap;
    LocationSource.OnLocationChangedListener mListener;
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;//定位蓝点
    MyLocationStyle myLocationStyle;
    private boolean isFirstLoc = true;
    List<MyLocation> myLocationList;
    TextView memoText;
    TextView memoTime;
    TextView memoLocation;
    Long memo_id;
    GeocodeSearch geocoderSearch;
    RegeocodeAddress regeocodeAddress;
    Boolean isMemo = false;
    DatabaseAdapter databaseAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FootFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FootFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FootFragment newInstance(String param1, String param2) {
        FootFragment fragment = new FootFragment();
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
        Log.d("TAG", "onCreate: FootFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("TAG", "onCreateView: FootFragment");
        View view = inflater.inflate(R.layout.fragment_foot, container, false);
        MapsInitializer.updatePrivacyAgree(getContext(), true);
        MapsInitializer.updatePrivacyShow(getContext(), true, true);
        initFindById(view);
        mapView.onCreate(savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        isFirstLoc = true;
        Log.d("TAG", "onStart: FootFragment");
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点20020
        try {
            location(0L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData();
    }

    public void initData(){
        myLocationDatabaseAdapter = new MyLocationDatabaseAdapter(getContext());
        myLocationDatabaseAdapter.open();
        databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.open();
        myLocationList = myLocationDatabaseAdapter.myLocationFindAllRecords();
//        myLocationDatabaseAdapter.myLocationDeleteAllRecords();
        for (int i = 0; i < myLocationList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(myLocationList.get(i).getLatitude(),myLocationList.get(i).getLongitude()));
            markerOptions.title(myLocationList.get(i).getMemoId().toString());
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
//            Log.d("TAG", "initIcon: "+BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.icon)).getBitmap()));
            aMap.addMarker(markerOptions).setInfoWindowEnable(false);
            if (i == myLocationList.size()-1){
                setMarkerInfo(myLocationList.get(i).getLatitude(),myLocationList.get(i).getLongitude(),myLocationList.get(i).getMemoId());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocationList.get(i).getLatitude(),myLocationList.get(i).getLongitude()), 18));
            }
        }
    }

    void initFindById(View view){
        memoTime = view.findViewById(R.id.memoTime);
        memoText = view.findViewById(R.id.memoText);
        memoLocation = view.findViewById(R.id.memoLocation);
        mapView = view.findViewById(R.id.map);
        aMap = mapView.getMap();
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() != null){
                    setMarkerInfo(marker.getPosition().latitude,marker.getPosition().longitude,Long.parseLong(marker.getTitle()));
                }
                return true;
            }
        });

    }

    public void getAddressByLatAndLng(Double latitude, Double longitude) {
        isMemo = true;
        try {
            geocoderSearch = new GeocodeSearch(getContext());
        } catch (AMapException e) {
            e.printStackTrace();
        }
        geocoderSearch.setOnGeocodeSearchListener(this);
        geocoderSearch.getFromLocationAsyn(new RegeocodeQuery(new LatLonPoint(latitude, longitude), 200, GeocodeSearch.AMAP));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        databaseAdapter.close();
        myLocationDatabaseAdapter.close();
    }



    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("TAG", "onLocationChanged: .................");
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                Log.d("TAG", "onLocationChanged: 定位成功");
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                Double latitude = aMapLocation.getLatitude();//获取纬度
                Double longitude = aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date(aMapLocation.getTime());
                String time = df.format(date);//定位时间
                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                //将地图移动到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(aMapLocation);
                if(memo_id != 0){
//                    myLocationDatabaseAdapter.open();
                    myLocationDatabaseAdapter.myLocationInsert(new MyLocation(latitude,longitude,time,memo_id));
//                    myLocationDatabaseAdapter.close();
                }

//                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                aMapLocation.getCountry();//国家信息
//                aMapLocation.getProvince();//省信息
//                aMapLocation.getCity();//城市信息
//                aMapLocation.getDistrict();//城区信息
//                aMapLocation.getStreet();//街道信息
//                aMapLocation.getStreetNum();//街道门牌号信息
//                aMapLocation.getCityCode();//城市编码
//                aMapLocation.getAdCode();//地区编码
//                LatLng latLng = new LatLng(latitude, longitude);
//                Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("当前位置").snippet("DefaultMarker"));

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(getContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
        mLocationClient.stopLocation();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private void insertLocation(MyLocation myLocation){
        myLocationDatabaseAdapter.myLocationInsert(myLocation);
    }

    public void location(Long memo_id) throws Exception {
        //初始化定位
        this.memo_id = memo_id;
        mLocationClient = new AMapLocationClient(getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
//        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));//设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));//设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i == 1000) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {

                String addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                if(isMemo){
                    memoLocation.setText(addressName);
                    isMemo = false;
                }
            } else {
                Toast.makeText(getContext(), "无结果", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "错误码" + i, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    public void setMarkerInfo(Double latitude, Double longitude,Long memo_id){
        getAddressByLatAndLng(latitude, longitude);
        Memo memo = databaseAdapter.memoFindById(memo_id);
        memoText.setText(memo.getContent());
        memoTime.setText(memo.getCompleteTime());
    }


}