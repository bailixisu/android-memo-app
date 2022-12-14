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
    AMapLocationClientOption mLocationOption;//????????????
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
        aMap.setLocationSource(this);// ??????????????????
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// ????????????????????????????????????
        aMap.setMyLocationEnabled(true);// ?????????true??????????????????????????????20020
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
                //?????????????????????????????????????????????
                Log.d("TAG", "onLocationChanged: ????????????");
//                aMapLocation.getLocationType();//????????????????????????????????????????????????????????????????????????????????????
                Double latitude = aMapLocation.getLatitude();//????????????
                Double longitude = aMapLocation.getLongitude();//????????????
//                aMapLocation.getAccuracy();//??????????????????
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date(aMapLocation.getTime());
                String time = df.format(date);//????????????
                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                //???????????????????????????
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                //?????????????????? ??????????????????????????????????????????
                mListener.onLocationChanged(aMapLocation);
                if(memo_id != 0){
//                    myLocationDatabaseAdapter.open();
                    myLocationDatabaseAdapter.myLocationInsert(new MyLocation(latitude,longitude,time,memo_id));
//                    myLocationDatabaseAdapter.close();
                }

//                aMapLocation.getAddress();//???????????????option?????????isNeedAddress???false??????????????????????????????????????????????????????????????????GPS??????????????????????????????
//                aMapLocation.getCountry();//????????????
//                aMapLocation.getProvince();//?????????
//                aMapLocation.getCity();//????????????
//                aMapLocation.getDistrict();//????????????
//                aMapLocation.getStreet();//????????????
//                aMapLocation.getStreetNum();//?????????????????????
//                aMapLocation.getCityCode();//????????????
//                aMapLocation.getAdCode();//????????????
//                LatLng latLng = new LatLng(latitude, longitude);
//                Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("????????????").snippet("DefaultMarker"));

            } else {
                //??????????????????ErrCode???????????????errInfo???????????????????????????????????????
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(getContext(), "????????????", Toast.LENGTH_LONG).show();
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
        //???????????????
        this.memo_id = memo_id;
        mLocationClient = new AMapLocationClient(getContext());
        //????????????????????????
        mLocationClient.setLocationListener(this);
        //?????????????????????
        mLocationOption = new AMapLocationClientOption();
        //?????????????????????Hight_Accuracy??????????????????Battery_Saving?????????????????????Device_Sensors??????????????????
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //????????????????????????????????????????????????????????????
        mLocationOption.setNeedAddress(true);
        //???????????????????????????,?????????false
        mLocationOption.setOnceLocation(false);
        //????????????????????????WIFI????????????????????????
//        mLocationOption.setWifiActiveScan(true);
        //??????????????????????????????,?????????false????????????????????????
        mLocationOption.setMockEnable(false);
        //??????????????????,????????????,?????????2000ms
        mLocationOption.setInterval(1000);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));//????????????????????????
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));//???????????????????????????
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//???????????????????????????
        aMap.setMyLocationStyle(myLocationStyle);
        //??????????????????????????????????????????
        mLocationClient.setLocationOption(mLocationOption);
        //????????????
        mLocationClient.startLocation();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i == 1000) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {

                String addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress()
                        + "??????";
                if(isMemo){
                    memoLocation.setText(addressName);
                    isMemo = false;
                }
            } else {
                Toast.makeText(getContext(), "?????????", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "?????????" + i, Toast.LENGTH_SHORT).show();
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