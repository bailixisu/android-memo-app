package com.cdh.bebetter.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cdh.bebetter.R;
import com.cdh.bebetter.activity.MemoEditActivity;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.adapter.MemoAdapter;
import com.cdh.bebetter.dao.Memo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemoSortFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoSortFragment extends Fragment {

    private ListView memoList;
    private View view;
    ImageView notCompleteImage;
    TextView notCompleteTitle  ;
    List<Memo> memos = new ArrayList<>();
    MemoAdapter memoAdapter;
    MemoAdapter emptyMemoAdapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SORT_TITLE = "title";
    private static final String MEMOS = "memos";

    // TODO: Rename and change types of parameters
    private String title;

    public MemoSortFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemoSortFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemoSortFragment newInstance(String param1, String param2) {
        MemoSortFragment fragment = new MemoSortFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        memoAdapter = new MemoAdapter(memos,getContext());
        emptyMemoAdapter = new MemoAdapter(new ArrayList<Memo>(),getContext());
        memoList.setAdapter(memoAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_memo_sort, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(SORT_TITLE);
            Log.d("TAG", "onCreateView: "+title);
            memos = (List<Memo>) bundle.getSerializable(MEMOS);
//            Log.d("TAG", "onCreateView: "+memos.size());
            Log.d("TAG", "onCreateView: "+memos.size());
        }
        findByIdAndInit();
        return view;
    }

    public void findByIdAndInit() {
        notCompleteTitle = view.findViewById(R.id.notCompleteTitle);
        notCompleteTitle.setText(title);

        notCompleteImage = view.findViewById(R.id.notCompleteImage);
        notCompleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag() == null){
                    view.setTag(false);
                }else {
                    view.setTag(!((boolean)view.getTag()));
                }
                if (!(boolean)view.getTag()){
                    memoList.setAdapter(emptyMemoAdapter);
                }else {
                    memoList.setAdapter(memoAdapter);
                    listViewAnimation();
                }
                imageRotate((ImageView) view);
            }
        });
        memoList = view.findViewById(R.id.memoList);
        listViewAnimation();
    }
    public void imageRotate(ImageView imageView){
        if (!(boolean)imageView.getTag()){
            imageView.animate().rotation(180).setDuration(500).start();}
        else {
            imageView.animate().rotation(0).setDuration(500).start();
        }
    }

    public void listViewAnimation(){
        Animation animation = (Animation) AnimationUtils.loadAnimation(getContext(), R.anim.dan_ru_dan_chu);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        memoList.setLayoutAnimation(layoutAnimationController);
    }
}