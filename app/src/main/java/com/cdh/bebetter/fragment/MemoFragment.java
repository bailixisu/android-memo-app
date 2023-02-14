package com.cdh.bebetter.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.activity.MainActivity;
import com.cdh.bebetter.activity.MemoEditActivity;
import com.cdh.bebetter.activity.SortEditActivity;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.adapter.MainSortListItemAdapter;
import com.cdh.bebetter.adapter.MemoAdapter;
import com.cdh.bebetter.adapter.MemoRecycleAdapter;
import com.cdh.bebetter.adapter.MemoSortDatabaseAdapter;
import com.cdh.bebetter.adapter.MyLocationDatabaseAdapter;
import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.dao.MyLocation;
import com.cdh.bebetter.dao.SortMemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoFragment extends Fragment implements MainSortListItemAdapter.OnSortItemClickListener
        , MemoRecycleAdapter.OnSortItemClickListener {
    private RecyclerView memoList;
    private RecyclerView completedMemoList;
    private RecyclerView outOfDateMemoList;
    private LinearLayout memoListLayout;
    private LinearLayout completedMemoListLayout;
    private LinearLayout outOfDateMemoListLayout;
    private LinearLayout memoListEmptyLayout;
    private View view;
    private FloatingActionButton floatingActionButton;
    CoordinatorLayout memoFragmentContainerRoot;
    ImageView notCompleteImage;
    ImageView completeImage;
    ImageView outOfDateImage;
    TextView notCompleteTitle;
    TextView completeTitle;
    TextView outOfDateTitle;
    TextView title;
    TextView memoCount;
    MemoRecycleAdapter memoAdapter;
    MemoRecycleAdapter completedMemoAdapter;
    MemoRecycleAdapter outOfDateMemoAdapter;
    DatabaseAdapter databaseAdapter;
    MemoSortDatabaseAdapter memoSortDatabaseAdapter;
    MyLocationDatabaseAdapter myLocationDatabaseAdapter;
    ImageView mainImage;
    LinearLayout mainTitleLayout;
    List<SortMemo> sortMemoList;
    List<Memo> memos;
    String filterTag;
    int backgroundColor;
    PopupWindow popupWindow;
    PopupWindow addSortPopupWindow;
    MemoFragment.OnMemoFragmentListener mListener;
    LocationListener locationListener;
    LocationManager locationManager;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MemoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemoFragment newInstance(String param1, String param2) {
        MemoFragment fragment = new MemoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterTag = Constant.ALL_MEMO_STRING;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        if(savedInstanceState != null){
////            filterTag = savedInstanceState.getString("filterTag");
////            backgroundColor = savedInstanceState.getInt("backgroundColor");
//            Log.d("TAG", "onCreate: "+savedInstanceState.getString("filterTag"));
//            Log.d("TAG", "onCreate: "+savedInstanceState.getInt("backgroundColor"));
//        }
        view = inflater.inflate(R.layout.fragment_memo, container, false);
        findByIdAndInit();
        mListener = (MemoFragment.OnMemoFragmentListener) getActivity();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        locationManager.removeUpdates(locationListener);
        databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.open();
        memoSortDatabaseAdapter = new MemoSortDatabaseAdapter(getContext());
        memoSortDatabaseAdapter.open();
        myLocationDatabaseAdapter = new MyLocationDatabaseAdapter(getContext());
        myLocationDatabaseAdapter.open();
        setMemoListByFilter(Constant.ALL_MEMO_STRING, Constant.COLOR_BACKGROUND_COLOR_0);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("filterTag", filterTag);
        outState.putInt("backgroundColor", backgroundColor);
    }


    @Override
    public void onStop() {
        super.onStop();
        databaseAdapter.close();
        memoSortDatabaseAdapter.close();
        myLocationDatabaseAdapter.close();
    }

    public void findByIdAndInit() {
        memoFragmentContainerRoot = view.findViewById(R.id.memoFragmentContainerRoot);
        mainTitleLayout = view.findViewById(R.id.mainTitleLayout);
        memoCount = view.findViewById(R.id.memoCount);
        title = view.findViewById(R.id.mainTitle);
        mainImage = view.findViewById(R.id.mainNotCompleteImage);
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() == null) {
                    view.setTag(false);
                } else {
                    view.setTag(!((boolean) view.getTag()));
                }
                imageRotate((ImageView) view);
                initMainSortPopWindow(view);
            }
        });
        floatingActionButton = view.findViewById(R.id.addMemo);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //实现一个闹钟
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
//                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
//                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
//                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),null);
//                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(5000);
                //比较时间
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                try {
//                    Date date1 = simpleDateFormat.parse("2020-12-12 12:12");
//                    Date date = simpleDateFormat.parse("2020-12-12 12:12");
//                    Log.d("TAG", "onClick: "+date1.compareTo(date));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }


//                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//                Log.d("TAG", "onClick: "+uri);
//                MediaPlayer mediaPlayer = new MediaPlayer();
//                try {
//                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    mediaPlayer.setDataSource(getContext(),uri);
//                    mediaPlayer.prepare();
//                    mediaPlayer.setLooping(true);
//                    mediaPlayer.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                mediaPlayer.start();

                Intent intent = new Intent(getContext(), MemoEditActivity.class);
                intent.putExtra("sort", filterTag);
                intent.putExtra("backgroundColor", backgroundColor);
                startActivity(intent);
//
//                HttpURLConnection httpURLConnection = null;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            URL url = new URL("http://192.168.31.81:8000/sort_memo");
//                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                            httpURLConnection.setRequestMethod("GET");
//                            httpURLConnection.setConnectTimeout(8000);
//                            httpURLConnection.setReadTimeout(8000);
//                            InputStream inputStream = httpURLConnection.getInputStream();
//                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                            StringBuilder stringBuilder = new StringBuilder();
//                            String line;
//                            while ((line = bufferedReader.readLine()) != null){
//                                stringBuilder.append(line);
//                            }
//                            Log.d("TAG", "run: "+stringBuilder.toString());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
            }
        });

        notCompleteImage = view.findViewById(R.id.notCompleteImage);
        notCompleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() == null) {
                    view.setTag(false);
                } else {
                    view.setTag(!((boolean) view.getTag()));
                }
                if (!(boolean) view.getTag()) {
                    memoAdapter.setIsClear(false);
                } else {
                    memoAdapter.setIsClear(true);
                    listViewAnimation();
                }
                imageRotateForList((ImageView) view);
            }
        });
        memoList = view.findViewById(R.id.memoList);
        memoListLayout = view.findViewById(R.id.notCompleteLayout);

        //complete list
        completedMemoList = view.findViewById(R.id.completeMemoList);
        completeImage = view.findViewById(R.id.completeImage);
        completeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() == null) {
                    view.setTag(false);
                } else {
                    view.setTag(!((boolean) view.getTag()));
                }
                if (!(boolean) view.getTag()) {
                    completedMemoAdapter.setIsClear(false);
                } else {
                    completedMemoAdapter.setIsClear(true);
                    listViewAnimation();
                }
                imageRotateForList((ImageView) view);
            }
        });
        completeTitle = view.findViewById(R.id.completeTitle);
        completedMemoListLayout = view.findViewById(R.id.completeLayout);


        //out of date list
        outOfDateMemoList = view.findViewById(R.id.outDateMemoList);
        outOfDateImage = view.findViewById(R.id.outDateImage);
        outOfDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() == null) {
                    view.setTag(false);
                } else {
                    view.setTag(!((boolean) view.getTag()));
                }
                if (!(boolean) view.getTag()) {
                    outOfDateMemoAdapter.setIsClear(false);
                } else {
                    outOfDateMemoAdapter.setIsClear(true);
                    listViewAnimation();
                }
                imageRotateForList((ImageView) view);
            }
        });
        outOfDateTitle = view.findViewById(R.id.outDateTitle);
        outOfDateMemoListLayout = view.findViewById(R.id.outDateLayout);

        memoListEmptyLayout = view.findViewById(R.id.noMemoLayout);
        listViewAnimation();
    }

    public void imageRotate(ImageView imageView) {
        if (!(boolean) imageView.getTag()) {
            imageView.setTag(!((boolean) imageView.getTag()));
            imageView.animate().rotation(180).setDuration(200).start();
        } else {
            imageView.animate().rotation(0).setDuration(200).start();
        }
    }

    public void imageRotateForList(ImageView imageView) {
        if (!(boolean) imageView.getTag()) {
            imageView.animate().rotation(180).setDuration(200).start();
        } else {
            imageView.animate().rotation(0).setDuration(200).start();
        }
    }

    public void listViewAnimation() {
        Animation animation = (Animation) AnimationUtils.loadAnimation(getContext(), R.anim.dan_ru_dan_chu);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        memoList.setLayoutAnimation(layoutAnimationController);
        completedMemoList.setLayoutAnimation(layoutAnimationController);
        outOfDateMemoList.setLayoutAnimation(layoutAnimationController);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }

    //初始化弹窗
    void initMainSortPopWindow(View v) {
        View popWindowView = getActivity().getLayoutInflater().inflate(R.layout.main_sort_popwindow, null);
//        View popWindowView = LayoutInflater.from(getContext()).inflate(R.layout.main_sort_popwindow,null);
        popupWindow = new PopupWindow(popWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imageRotate(mainImage);
                setBackgroundAlpha(1.0f);
            }
        });

        CardView allMemoCard = popWindowView.findViewById(R.id.allMemoCard);
        TextView allMemoCount = popWindowView.findViewById(R.id.allMemoCount);
        allMemoCount.setText(String.valueOf(getSortCount(Constant.ALL_MEMO_STRING)));
        allMemoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMemoListByFilter(Constant.ALL_MEMO_STRING, Constant.COLOR_BACKGROUND_COLOR_0);
                popupWindow.dismiss();
            }
        });
        CardView notSortMemoCard = popWindowView.findViewById(R.id.notSortMemoCard);

        notSortMemoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMemoListByFilter(Constant.NOT_SORT_MEMO_STRING, Constant.COLOR_BACKGROUND_COLOR_0);
                popupWindow.dismiss();
            }
        });
        TextView notSortMemoCount = popWindowView.findViewById(R.id.notSortMemoCount);
        notSortMemoCount.setText(String.valueOf(getSortCount(Constant.NOT_SORT_MEMO_STRING)));

        RecyclerView mainSortList = popWindowView.findViewById(R.id.mainSortList);
        MainSortListItemAdapter mainSortListItemAdapter = new MainSortListItemAdapter(getContext(), sortMemoList, memos, this);
        mainSortList.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mainSortList.setAdapter(mainSortListItemAdapter);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(backgroundColor));
        popupWindow.showAtLocation(popWindowView, Gravity.BOTTOM, 0, 60);
        CardView mainPopWindowRootCard = popWindowView.findViewById(R.id.mainPopWindowRootCard);
        mainPopWindowRootCard.setCardBackgroundColor(backgroundColor);

        TextView memoSortEditButton = popWindowView.findViewById(R.id.memoSortEditButton);
        memoSortEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SortEditActivity.class);
                startActivity(intent);
            }
        });

        TextView addMemoSort = popWindowView.findViewById(R.id.addMemoSort);
        addMemoSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                initAddSortPopWindow();
            }
        });
//        setBackgroundAlpha(0.8f);
    }

    private SpannableString getSpannableString(String content) {
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
        return spannableString;
    }


    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha;
        getActivity().getWindow().setAttributes(lp);
    }

    private List<Memo> getMemosByFilter(String filterTag) {
        if (filterTag.equals("全部待办")) {
            return memos;
        }
        List<Memo> newMemos = new ArrayList<>();
        for (int i = 0; i < memos.size(); i++) {
            if (filterTag.equals(memos.get(i).getSort())) {
                newMemos.add(memos.get(i));
            }
        }
        return newMemos;
    }

    private List<Memo> getMemosByStatus(Integer status, List<Memo> memos) {
        List<Memo> newMemos = new ArrayList<>();
        for (int i = 0; i < memos.size(); i++) {
            if (memos.get(i).getStatus() == status) {
                newMemos.add(memos.get(i));
            }
        }
        return newMemos;
    }

    @Override
    public void onItemClick(String sortText, int color) {
        setMemoListByFilter(sortText, color);
        popupWindow.dismiss();
    }

    @SuppressLint("SetTextI18n")
    private void setMemoListByFilter(String sortText, int color) {
        filterTag = sortText;
        backgroundColor = color;
        title.setText(sortText);
        memos = databaseAdapter.memoFindAllRecords();
        int count = getSortCount(sortText);
        sortMemoList = memoSortDatabaseAdapter.memoFindAllRecords();
        List<Memo> filterMemos = getMemosByFilter(sortText);
        List<Memo> notCompleteMemos = getMemosByStatus(Constant.NOT_COMPLETE, filterMemos);
        memoAdapter = new MemoRecycleAdapter(notCompleteMemos, getContext(), this);
        memoList.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        memoList.setAdapter(memoAdapter);
        if (notCompleteMemos.size() == 0) {
            memoListLayout.setVisibility(View.GONE);
        } else {
            memoListLayout.setVisibility(View.VISIBLE);
        }

        List<Memo> completeMemos = getMemosByStatus(Constant.COMPLETE, filterMemos);
        completedMemoAdapter = new MemoRecycleAdapter(completeMemos, getContext(), this);
        completedMemoList.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        completedMemoList.setAdapter(completedMemoAdapter);
        if (completeMemos.size() == 0) {
            completedMemoListLayout.setVisibility(View.GONE);
        } else {
            completedMemoListLayout.setVisibility(View.VISIBLE);
        }


        List<Memo> outOfDateMemos = getMemosByStatus(Constant.OUT_DATE, filterMemos);
        outOfDateMemoAdapter = new MemoRecycleAdapter(outOfDateMemos, getContext(), this);
        outOfDateMemoList.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        outOfDateMemoList.setAdapter(outOfDateMemoAdapter);
        if (outOfDateMemos.size() == 0) {
            outOfDateMemoListLayout.setVisibility(View.GONE);
        } else {
            outOfDateMemoListLayout.setVisibility(View.VISIBLE);
        }

        if (filterMemos.size() == 0) {
            memoListEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            memoListEmptyLayout.setVisibility(View.GONE);
        }

        memoFragmentContainerRoot.setBackgroundColor(color);
        if (mListener != null) {
            mListener.onBackGroundChange(color);
        }
        if (count == 0) {
            mainTitleLayout.removeView(memoCount);
            return;
        }
        if (mainTitleLayout.getChildCount() == 1) {
            mainTitleLayout.addView(memoCount);
        }
        memoCount.setText(String.valueOf(count) + "条待办");
    }

    private Integer getSortCount(String sort) {
        int count = 0;
        if (sort.equals(Constant.ALL_MEMO_STRING)) {
            return memos.size();
        }
        for (int i = 0; i < memos.size(); i++) {
            if (sort.equals(memos.get(i).getSort())) {
                count++;
            }
        }
        return count;
    }

    private void initAddSortPopWindow() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_memo_sort_popwindow, null);
        SortMemo sortMemo = new SortMemo();
        sortMemo.setSortIconColor(Constant.COLOR_1);
        sortMemo.setSortBackgroundColor(Constant.COLOR_BACKGROUND_COLOR_1);
        addSortPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        addSortPopupWindow.setOutsideTouchable(true);
        addSortPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        setBackgroundAlpha(0.8f);
        addSortPopupWindow.setAnimationStyle(R.style.pw_bottom_anim_style);
        addSortPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//        addSortPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        addSortPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        addSortPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 826);
        addSortPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        EditText addSortEdit = view.findViewById(R.id.sortName);
        ImageView addMemoSortIcon = view.findViewById(R.id.addMemoSortIcon);
        addMemoSortIcon.setColorFilter(Constant.COLOR_1);
        addSortEdit.setFocusable(true);
        addSortEdit.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        TextView addMemoSortCancel = view.findViewById(R.id.addMemoSortCancel);
        addMemoSortCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                addSortPopupWindow.dismiss();
            }
        });
        TextView addMemoSortSave = view.findViewById(R.id.addMemoSortSave);
        addMemoSortSave.setAlpha(0.6f);
        addMemoSortSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getAlpha() == 1.0f) {
                    try {
                        if (!memoSortDatabaseAdapter.isRecordExist(sortMemo.getSortText())) {
                            memoSortDatabaseAdapter.memoSortInsert(sortMemo);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            addSortPopupWindow.dismiss();
                            setMemoListByFilter(sortMemo.getSortText(), sortMemo.getSortBackgroundColor());
                        } else {
                            Toast.makeText(getContext(), "分类已存在", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//        addSortEdit.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                addSortEdit.requestFocus();
                imm.showSoftInput(addSortEdit, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
        RadioGroup memoSortEditPopWindowColorRadioGroup = view.findViewById(R.id.memoSortEditPopWindowColorRadioGroup);
        memoSortEditPopWindowColorRadioGroup.check(R.id.memoSortEditPopWindowRadioButton1);
        memoSortEditPopWindowColorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int index = 0;
                switch (i) {
                    case R.id.memoSortEditPopWindowRadioButton1:
                        index = 0;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton2:
                        index = 1;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton3:
                        index = 2;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton4:
                        index = 3;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton5:
                        index = 4;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton6:
                        index = 5;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton7:
                        index = 6;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton8:
                        index = 7;
                        break;
                }
                sortMemo.setSortBackgroundColor(Constant.COLORS_BACKGROUND[index]);
                sortMemo.setSortIconColor(Constant.COLORS[index]);
                addMemoSortIcon.setColorFilter(Constant.COLORS[index]);
            }
        });
        addSortEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addMemoSortSave.setAlpha(editable.toString().length() > 0 ? 1.0f : 0.6f);
                sortMemo.setSortText(editable.toString());
            }
        });
    }

    @Override
    public void onRadioButtonClick() {
        setMemoListByFilter(filterTag, backgroundColor);
    }

    @Override
    public void onItemChange(Memo memo) {
        if(memo.getStatus() == Constant.COMPLETE){
            Log.d("TAG", "onItemChange: "+memo.toString());
            Log.d("TAG", "onItemChange: "+memo.getId());
//            getFinishedMemoLocation(memo.getId());
            mListener.getMyLocation(memo.getId());
        }
        databaseAdapter.memoUpdate(memo);
        setMemoListByFilter(filterTag, backgroundColor);
    }

    //回调函数的接口
    public interface OnMemoFragmentListener {
        void onBackGroundChange(int color);
        void getMyLocation(Long memo_id);
    }

    //获取完成memo的位置
    public void getFinishedMemoLocation(Long id) {
        Log.d("TAG", "getFinishedMemoLocation: "+id);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("TAG", "getFinishedMemoLocation: 3333333333333333333311");
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = new Date(System.currentTimeMillis());
                    String time = simpleDateFormat.format(date);
                    Log.d("TAG", "getFinishedMemoLocation: "+id);
                    myLocationDatabaseAdapter.myLocationInsert(new MyLocation(latitude, longitude, time, id));
                    locationManager.removeUpdates(locationListener);
                }
            }
        };
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,10,locationListener);
//        locationManager.removeUpdates(locationListener);
    }
}