package com.cdh.bebetter.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cdh.bebetter.R;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerDialog extends DialogFragment {
    private TextView title;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private String identity;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;


    public interface NoticeDialogListener {
        public void onDialogPositiveClick(TimePickerDialog dialog);
        public void onDialogNegativeClick(TimePickerDialog dialog);
    }

    NoticeDialogListener listener;

    public TimePickerDialog() {
    }

    @SuppressLint("ValidFragment")
    public TimePickerDialog(String id) {
        this.identity = id;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_time_picker,null);
        initFindById(view);
        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogPositiveClick(TimePickerDialog.this);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogNegativeClick(TimePickerDialog.this);
            }
        });
        return builder.create();
    }


    private void initFindById(View view){
        initTime();
        title = view.findViewById(R.id.timePickerDialogTitle);
        title.setText(getTimeString());

        datePicker = view.findViewById(R.id.timePickerDialogDatePicker);
        resizePicker(datePicker);
        reOrderDatePicker(view);
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1+1;
                day = i2;
                title.setText(getTimeString());
                setMonthDisplayedValues(datePicker);
            }
        });
        

        timePicker = view.findViewById(R.id.timePickerDialogTimePicker);
        timePicker.setIs24HourView(true);
        resizePicker(timePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = i;
                minute = i1;
                title.setText(getTimeString());
            }
        });
    }

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
    }
    public String getTimeString () {
        return year+"年"+month+"月"+day+"日"+hour+"时"+minute+"分";
    }

    public String getFormatString(String format){
        LocalDateTime localDateTime = LocalDateTime.of(year,month,day,hour,minute);
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }


    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getIdentity() {
        return identity;
    }

    private void resizePicker(FrameLayout tp){
        List<NumberPicker> npList = findNumberPicker(tp);
        for(NumberPicker np:npList){
            resizeNumberPicker(np);
        }
    }

    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup){
        List<NumberPicker> npList = new ArrayList();
        View child = null;
        if(null != viewGroup){
            for(int i = 0;i<viewGroup.getChildCount();i++){
                child = viewGroup.getChildAt(i);
                if(child instanceof NumberPicker){
                    npList.add((NumberPicker)child);
                }
                else if(child instanceof LinearLayout){
                    List<NumberPicker> result = findNumberPicker((ViewGroup)child);
                    if(result.size()>0){
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    private void resizeNumberPicker(NumberPicker np){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(15, 0, 15, 0);
        np.setLayoutParams(params);
    }
    
    private void reOrderDatePicker(View view) {
        Resources resources = Resources.getSystem();
        // 获取布局LinearLayout 
        LinearLayout mSpinners = (LinearLayout) view.findViewById(resources.getIdentifier("pickers", "id", "android"));

        if (mSpinners != null) {
            // 获取年月日numberpicker
            NumberPicker yearPicker = (NumberPicker) datePicker.findViewById(resources.getIdentifier("year", "id", "android"));
            NumberPicker monthPicker = (NumberPicker) datePicker.findViewById(resources.getIdentifier("month", "id", "android"));
            NumberPicker dayPicker = (NumberPicker) datePicker.findViewById(resources.getIdentifier("day", "id", "android"));

            /*重新排列datepicker年月日的顺序*/
            mSpinners.removeAllViews();
            mSpinners.addView(yearPicker);
            mSpinners.addView(monthPicker);
            mSpinners.addView(dayPicker);
            setMonthDisplayedValues(datePicker);
        }
    }

//    private void setMonthPickerFormat(NumberPicker monthPicker) {
//        monthPicker.setFormatter(new NumberPicker.Formatter() {
//            @Override
//            public String format(int i) {
//                String[] months = {"一月","二月","三月","四月","五月","六月",
//                        "七月","八月","九月","十月","十一月","十二月"};
//                return months[i];
//            }
//        });
//    }

    private void setMonthDisplayedValues(DatePicker datePicker) {
        Resources resources = Resources.getSystem();
        NumberPicker monthPicker = (NumberPicker) datePicker.findViewById(resources.getIdentifier("month", "id", "android"));
        String[] months = {"一月","二月","三月","四月","五月","六月",
                "七月","八月","九月","十月","十一月","十二月"};
        monthPicker.setDisplayedValues(months);
    }
}
