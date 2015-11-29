package com.zph.run;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import net.simonvt.numberpicker.NumberPicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import at.markushi.ui.CircleButton;

public class nextplan extends FragmentActivity implements OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    String[] dis={"1000","2000","3000","4000","5000","6000","7000","8000","9000","10000"};

    String goal="2000";

    NumberPicker np;

    int year,month,day,hour,minute;

    CircleButton date,time,set;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_test1);

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

        DateFormat dfx = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        Date now=new Date();
        String  string=dfx.format(now);
        String[] tem=string.split("-");

        year = Integer.valueOf(tem[0]);
        month = Integer.valueOf(tem[1])-1;
        day = Integer.valueOf(tem[2]);
        hour = Integer.valueOf(tem[3]);
        minute = Integer.valueOf(tem[4]);

        np = (NumberPicker) findViewById(R.id.np);
        np.setDisplayedValues(dis);
        np.setMinValue(0);
        np.setMaxValue(dis.length - 1);
        np.setOnValueChangedListener(new myOnValueChangeListener());


        date = (CircleButton) findViewById(R.id.dateButton);
        date.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //震动
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(2015, 2030);
                datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        time = (CircleButton) findViewById(R.id.timeButton);
        time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置震动
                timePickerDialog.setVibrate(true);
                timePickerDialog.setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });

        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }

            TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }

        set = (CircleButton) findViewById(R.id.submit);

        set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now1 = new Date();
                String timeflomat = year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + "00";
                Date mubiao = null;
                try {
                    mubiao = df.parse(timeflomat);
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
                if (now1.compareTo(mubiao) > 0) {
                    Toast.makeText(nextplan.this, "日期选择错误，重新选择" + year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
                } else {
                    //求出此时与目标时间的差值  为毫秒级
                    long tem = mubiao.getTime();
                    long cur = now1.getTime();
                    Toast.makeText(nextplan.this, "时间:" + year + "-" + (month + 1) + "-" + day + "," + hour + ":" + minute
                            + "\n里程:" + goal + "m", Toast.LENGTH_SHORT).show();
                    //alarmservice.addNotification(nextplan.this,info,"哈哈哈马学森","要开始运动了","您设定的任务要开始了，里程"+distance+"m,Run！");

                    //添加Service至系统进程  机试程序退出依然可以提醒
                    addinfo(tem - cur, "RunStone计划", "要开始运动了", "您设定的任务要开始了，里程" + goal + "m,Run！", "" + goal);

                    //保存至数据库
                    ContentValues value = new ContentValues();
                    value.put("time", df.format(mubiao).toString());
                    value.put("distance", goal);

                    DbHelper dbHelper = new DbHelper(nextplan.this, "plan", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    long status;
                    status = db.insert("tb_plan", null, value);
                    if (status != -1) {
                        Toast.makeText(nextplan.this, "计划保存至数据库", Toast.LENGTH_SHORT).show();
                        set.setEnabled(false);
                    } else {
                        Toast.makeText(nextplan.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void addinfo(long info,String tickerText,String contentTitle,String contentText,String distance){
        Intent intent = new Intent(nextplan.this, alarmservice.class);
        intent.putExtra("when", info);
        intent.putExtra("distance",distance);
        intent.putExtra("tickerText", tickerText);
        intent.putExtra("contentTitle", contentTitle);
        intent.putExtra("contentText", contentText);
        nextplan.this.startService(intent);
    }


    /*private boolean isVibrate() {
        return ((CheckBox) findViewById(R.id.checkBoxVibrate)).isChecked();
    }*/

    private boolean isCloseOnSingleTapDay() {
        //return ((CheckBox) findViewById(R.id.checkBoxCloseOnSingleTapDay)).isChecked();
        return true;
    }

    private boolean isCloseOnSingleTapMinute() {
        //eturn ((CheckBox) findViewById(R.id.checkBoxCloseOnSingleTapMinute)).isChecked();
        return false;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Toast.makeText(nextplan.this, "new date:" + year + "-" + (month+1) + "-" + day, Toast.LENGTH_LONG).show();
        this.year=year;
        this.month=month;
        this.day=day;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Toast.makeText(nextplan.this, "new time:" + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
        this.hour=hourOfDay;
        this.minute=minute;
    }

    class myOnValueChangeListener implements NumberPicker.OnValueChangeListener {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            goal=dis[newVal];
        }
    }
}
