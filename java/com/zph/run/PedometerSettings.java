package com.zph.run;

import android.content.SharedPreferences;

public class PedometerSettings {

    SharedPreferences mSettings;
    
    public static int M_NONE = 1;
    public static int M_PACE = 2;
    public static int M_SPEED = 3;
    
    public PedometerSettings(SharedPreferences settings) {

        mSettings = settings;
    }

    
    public float getStepLength() {
        try {
            return Float.valueOf(mSettings.getString("step_length", "70").trim());
        }
        catch (NumberFormatException e) {
            // TODO: reset value, & notify user somehow
            return 0f;
        }
    }
    
    public float getBodyWeight() {
        try {
            return Float.valueOf(mSettings.getString("body_weight","60").trim());
        }
        catch (NumberFormatException e) {
            // TODO: reset value, & notify user somehow
            return 0f;
        }
    }

    public boolean isRunning() {
        return mSettings.getString("exercise_type", "running").equals("running");
    }


    public boolean wakeAggressively() {
        return mSettings.getString("operation_level", "run_in_background").equals("wake_up");
    }
    public boolean keepScreenOn() {
        return mSettings.getString("operation_level", "run_in_background").equals("keep_screen_on");
    }

    public void saveServiceRunningWithTimestamp(boolean running) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", running);
        editor.putLong("last_seen", Utils.currentTimeInMillis());
        editor.commit();
    }
    
    public void saveServiceRunningWithNullTimestamp(boolean running) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", running);
        editor.putLong("last_seen", 0);
        editor.commit();
    }

    public void clearServiceRunning() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", false);
        editor.putLong("last_seen", 0);
        editor.commit();
    }

    public boolean isServiceRunning() {
        return mSettings.getBoolean("service_running", false);
    }


    public boolean isMetric() {
        return true;
    }



    public boolean isNewStart() {

        // 如果距离上次执行  onPause超过十分钟  则视为重新开始
        return mSettings.getLong("last_seen", 0) < (Utils.currentTimeInMillis() - 1000*60*10);
    }
}