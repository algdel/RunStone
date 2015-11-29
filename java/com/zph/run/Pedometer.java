package com.zph.run;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import at.markushi.ui.CircleButton;


public class Pedometer extends Activity {
	private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
    
    private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;

    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;
    private boolean mQuitting = false;


    private CircleButton bcontrol;
    private CircleButton bset;
    private CircleButton breset;
    private CircleButton bquit;



    private boolean mIsRunning;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mStepValue = 0;
        mPaceValue = 0;
        
        this.setContentView(R.layout.indoor);
        
        mUtils = Utils.getInstance();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();


        //mSettings获取的是应用Basic设置
        //在此处设置了保存数据的想xml文件  因为每次想要进入settiing界面都要先
        // 经过这个Activity  而开启Activity必须执行onResume()方法  创建或者获取已经创建的  com.zph.run._preferences.xml文件
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        //将保存文件名与preferencesActivity关联  之后可以通过mPedometerSettings对象写入读取相关信息
        mPedometerSettings = new PedometerSettings(mSettings);
        

        mIsRunning = mPedometerSettings.isServiceRunning();


        //如果状态为未运行  同时距离上次运行超过分钟 则视为一次新的开始
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            bindStepService();
        }
        
        mPedometerSettings.clearServiceRunning();

        //步数变化
        mStepValueView     = (TextView) findViewById(R.id.step_value);
        //步数/小时
        mPaceValueView     = (TextView) findViewById(R.id.pace_value);
        //千米/小时
        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
        //速度变化
        mSpeedValueView    = (TextView) findViewById(R.id.speed_value);
        //能量消耗
        mCaloriesValueView = (TextView) findViewById(R.id.calories_value);


        bquit=(CircleButton)findViewById(R.id.quit);
        bcontrol=(CircleButton)findViewById(R.id.control);
        bset=(CircleButton)findViewById(R.id.set);
        breset=(CircleButton)findViewById(R.id.reset);

        //设置显示单位
        ((TextView) findViewById(R.id.distance_units)).setText(R.string.kilometers);
        ((TextView) findViewById(R.id.speed_units)).setText(R.string.kilometers_per_hour);
    }

    
    @Override
    protected void onPause() {
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
    
    protected void onRestart() {
        super.onRestart();
    }

    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
        }
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            mIsRunning = true;
            startService(new Intent(Pedometer.this,
                    StepService.class));
        }
    }
    
    private void bindStepService() {
        bindService(new Intent(Pedometer.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        if (mService != null) {
            stopService(new Intent(Pedometer.this,
                  StepService.class));
        }
        mIsRunning = false;
    }
    
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            mStepValueView.setText("0");
            mPaceValueView.setText("0");
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }


    public void onClick(View view){
        switch(view.getId()){
            case R.id.control:
                if(mIsRunning){
                    unbindStepService();
                    stopStepService();
                    //将图片设置为开始
                    bcontrol.setImageResource(R.drawable.kaishi);
                }
                else{
                    startStepService();
                    bindStepService();
                    //将图片设置为暂停
                    bcontrol.setImageResource(R.drawable.zanting);
                }
                break;
            case R.id.reset:
                resetValues(true);
                break;
            case R.id.set:
                Intent it=new Intent();
                it.setClass(Pedometer.this,Settings.class);
                startActivity(it);
                break;
            case R.id.quit:
                resetValues(false);
                unbindStepService();
                stopStepService();
                mQuitting = true;
                finish();
                break;
        }
    }

 
    //在StepDetector中判断走了一步之后同时更新  监听器队列  调用onStep方法，
    // 在onStep方法中简介调用了sendMessage方法
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) { 
                        mPaceValueView.setText("0");
                    }
                    else {
                        mPaceValueView.setText("" + (int)mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) { 
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) { 
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
}