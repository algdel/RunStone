package com.zph.run;

/**
 * Created by Administrator on 2015/11/10.
 */

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import cn.sharesdk.framework.ShareSDK;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 */
public class LocationApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        ShareSDK.initSDK(getApplicationContext());
        //Rect frame = new Rect();
    }
}
