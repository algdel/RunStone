package com.zph.run;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {

    //会默认保存在com.zph,run_preferences.xml  即packagename_preferences.xml里面
    //继承自PreferenceActivity的类  如果没有特别声明  一个项目即使有很多个这样的类  都保存在一个xml里面
    //这个packagename_preference.xml属于整个应用
        /*android.content.SharedPreferences是一个接口，用来获取和修改持久化存储的数据。有三种获取系统中保存的持久化数据的方式：
        1）. public SharedPreferences getPreferences (int mode)
        通过Activity对象获取，获取的是本Activity私有的Preference，保存在系统中的xml形式的文件的名称为这个Activity的名字，因此一个Activity只能有一个，属于这个Activity。
        2）. public SharedPreferences getSharedPreferences (String name, int mode)
        因为Activity继承了ContextWrapper，因此也是通过Activity对象获取，但是属于整个应用程序，可以有多个，以第一参数的name为文件名保存在系统中。
        3）. public static SharedPreferences getDefaultSharedPreferences (Context context)
        PreferenceManager的静态函数，保存PreferenceActivity中的设置，属于整个应用程序，但是只有一个，Android会根据包名和PreferenceActivity的布局文件来起一个名字保存。
    * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
    }
}
