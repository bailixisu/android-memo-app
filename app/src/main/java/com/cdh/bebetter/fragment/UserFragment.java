package com.cdh.bebetter.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.dao.Memo;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DatabaseAdapter databaseAdapter;
    LineChart chart;
    List<Memo> memos;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initViews(view);
        return view;
    }


    void initViews(View view) {
        databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.open();
        memos = databaseAdapter.memoFindAllRecords();
        PieChart pieChart = view.findViewById(R.id.pieChart);
        showPieChart(pieChart,getPieChartData());
        chart = view.findViewById(R.id.chart);
        initLineChart();
        chart.setData(setLineData());
        chart.invalidate();
    }

    private List<PieEntry> getPieChartData() {
        Map<String,Float> map = new HashMap<String,Float>();
        float onePie = 1f/memos.size();
        for (Memo memo : memos) {
            if (map.containsKey(memo.getSort())) {
                map.put(memo.getSort(),map.get(memo.getSort())+onePie);
            } else {
                map.put(memo.getSort(),onePie);
            }
        }
        List<PieEntry> mPie = new ArrayList<>();
        for (Map.Entry<String,Float> entry : map.entrySet()) {
            mPie.add(new PieEntry(entry.getValue(),entry.getKey()));
        }
//        mPie.add(new PieEntry(0,"测试"));
        return mPie;
    }

    private void showPieChart(PieChart pieChart, List<PieEntry> pieList) {
        PieDataSet dataSet = new PieDataSet(pieList,"图例");

        // 设置颜色list，让不同的块显示不同颜色，下面是我觉得不错的颜色集合，比较亮
        ArrayList<Integer> colors = new ArrayList<Integer>();
        int[] MATERIAL_COLORS = {
                Color.rgb(200, 172, 255)
        };
        for (int c : MATERIAL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieChart.setUsePercentValues(true);
        // 设置描述，我设置了不显示，因为不好看，你也可以试试让它显示，真的不好看
        Description description = new Description();
        description.setEnabled(true);
        pieChart.setDescription(description);
        //设置半透明圆环的半径, 0为透明
        pieChart.setTransparentCircleRadius(0f);

        //设置初始旋转角度
        pieChart.setRotationAngle(-15);

        //数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1OffsetPercentage(80f);

        //设置连接线的颜色
        dataSet.setValueLineColor(Color.LTGRAY);
        // 连接线在饼状图外面
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // 设置饼块之间的间隔
        dataSet.setSliceSpace(1f);
        dataSet.setHighlightEnabled(true);
        // 不显示图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

        // 和四周相隔一段距离,显示数据
        pieChart.setExtraOffsets(26, 5, 26, 5);

        // 设置pieChart图表是否可以手动旋转
        pieChart.setRotationEnabled(true);
        // 设置piecahrt图表点击Item高亮是否可用
        pieChart.setHighlightPerTapEnabled(true);
        // 设置pieChart图表展示动画效果，动画运行1.4秒结束
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        //设置pieChart是否只显示饼图上百分比不显示文字
        pieChart.setDrawEntryLabels(true);
        //是否绘制PieChart内部中心文本
        pieChart.setDrawCenterText(false);
        // 绘制内容value，设置字体颜色大小
        pieData.setDrawValues(true);
        //设置pieChart显示百分比
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.DKGRAY);

        pieChart.setData(pieData);
        // 更新 piechart 视图
        pieChart.postInvalidate();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseAdapter.close();
    }

    private void initLineChart(){
        //设置线状图不显示描述
        chart.setDescription(null);

        //Y 自下往上动态绘制  这里添加初始的动画效果
        chart.animateY(1000);

        //获取柱状图的X轴
        XAxis xAxis = chart.getXAxis();
        //下面两个是获取Y轴  包括左右
        YAxis axisLeft = chart.getAxisLeft();
        YAxis axisRight = chart.getAxisRight();
        //设置XY轴
        setAXis(xAxis,axisLeft,axisRight);

    }


    /*
     * 设置折线图的XY轴
     * */
    public void setAXis(XAxis axis,YAxis axisLeft,YAxis axisRight){
        //设置X轴在图底部显示
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置X轴的宽度
        axis.setAxisLineWidth(1);
        axis.setAxisLineColor(Color.BLACK);
        //起始0坐标开始
        axis.setAxisMinimum(0);
        //设置X轴显示轴线
        axis.setDrawAxisLine(true);
        //x的表格线不显示
        axis.setDrawGridLines(false);
        //设置X轴显示
        axis.setEnabled(true);
        //x轴显示字符串
        axis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                //从当前时间开始往前推7天的字符串
                Date date = new Date(System.currentTimeMillis() - (long) ((6f - value) * 24 * 60 * 60 * 1000));
                SimpleDateFormat format = new SimpleDateFormat("MM-dd");
                return format.format(date);
            }
        });
        //
        axis.setLabelCount(7,true);

        //y轴0刻度
        axisLeft.setAxisMinimum(0);
        //不画网格线
        axisLeft.setDrawGridLines(false);
        axisLeft.setAxisLineColor(Color.BLACK);
        //显示Y轴轴线
        axisLeft.setDrawAxisLine(true);
        axisLeft.setAxisLineWidth(1);
        axisLeft.setEnabled(true);
        axisLeft.setDrawLabels(true);


        //不显示右Y轴
        axisRight.setEnabled(false);

    }
    public LineData setLineData(){
        //X轴就是0-6
        List<Entry> mListEnryMin = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            //添加x,y坐标的值
            Date date = new Date(System.currentTimeMillis() - (long) ((6 - i) * 24 * 60 * 60 * 1000));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = format.format(date);
            int memoCount = 0;
            for (int count = 0; count < memos.size(); count++) {
                Log.d("TAG", "setLineData: "+memos.size());
                Log.d("TAG", "setLineData: "+dateStr+"  "+memos.get(count).getCompleteTime());
                //比较前i天的日期和完成日期
                if (memos.get(count).getCompleteTime() == null || memos.get(count).getCompleteTime().equals("")
                        ||memos.get(count).getStatus() == Constant.COMPLETE) {
                    continue;
                }
                if(dateStr.equals(memos.get(count).getCompleteTime().split(" ")[0])){
                    memoCount++;
                }
            }
            mListEnryMin.add(new Entry(i,memoCount));
        }
        LineDataSet barDataSet = new LineDataSet(mListEnryMin,"日期");
        barDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //设置线条颜色为红色
        barDataSet.setColors(Color.parseColor("#ff0000"));
        //设置折线图转择点的值的大小
        barDataSet.setValueTextSize(12);

        LineData lineData = new LineData(barDataSet);

        return lineData;
    }




}