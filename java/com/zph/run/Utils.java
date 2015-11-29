package com.zph.run;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.View;

import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by Administrator on 2015/11/10.
 */
public class Utils  {
    public static int scantime=1000;
    //根据参数来年各个经纬度点  获取两点之间的 距离 单位M
    public static final double getDistance(LatLng start,LatLng end){
        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;


        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;

        return d*1000;
    }

    private static final String TAG = "Utils";


    private Service mService;


    private static Utils instance = null;

    private Utils() {
    }


    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    public void setService(Service service) {
        mService = service;
    }


    public void ding() {
    }

    /********** Time **********/

    public static long currentTimeInMillis() {
        Time time = new Time();
        time.setToNow();
        return time.toMillis(false);
    }

    public static LocationClientOption init(){
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);


        option.setScanSpan(scantime);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.disableCache(true);// 禁止启用缓存定位
        option.setPriority(LocationClientOption.GpsFirst );
        return option;
    }

    //截屏功能  调用时为 getScreenHot((View) getWindow().getDecorView(), "/sdcard/test1.png");
    //只能实现普通模式的截屏  在有定位图层 地图图层情况下无法获取他们具体图像
    public static final void getScreenHot(View v, String filePath)
    {
        try
        {
            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);
            v.draw(canvas);

            try
            {
                FileOutputStream fos = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            catch (FileNotFoundException e)
            {
                throw new InvalidParameterException();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static final String generateFilename(){
        String filename="";
        long cur=System.currentTimeMillis();
        filename=""+cur/145566794+""+cur/465488856+".png";
        return filename;
    }
    public static final boolean gPSIsOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps =locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    //获取屏幕像素  deviceWidthHeigth[0]为宽度
    //deviceWidthHeigth[1]为高度
    public static int[] deviceWidthHeight = new int[2];
    public static int[] getDeviceInfo(Context context) {
        if ((deviceWidthHeight[0] == 0) && (deviceWidthHeight[1] == 0)) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay()
                    .getMetrics(metrics);
            deviceWidthHeight[0] = metrics.widthPixels;
            deviceWidthHeight[1] = metrics.heightPixels;
        }
        return deviceWidthHeight;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //将Drawable转换成为Bitmap
    public static  Bitmap drawableToBitamp(Drawable drawable)
    {	Bitmap bitmap = null;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }



    public void onGetCurrentMap(Bitmap bitmap,int statusBarHeight,String filename) {
        File file = new File("sdcard/"+filename);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            // 获取状态栏高度
            //Rect frame = new Rect();
            //BaiduActivity.this.getWindow().getDecorView()
              //      .getWindowVisibleDisplayFrame(frame);
            //int statusBarHeight = frame.top;
            bitmap = Bitmap
                    .createBitmap(
                            bitmap,
                            0,statusBarHeight,deviceWidthHeight[0],
                            deviceWidthHeight[1]-statusBarHeight);
            //pos_IV.setImageBitmap(BitmapUtils.toRoundCorner(bitmap, 15));
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


     public static boolean isInteger(String number){
         int length=number.length();
         for(int i=0;i<length;i++){
             if(number.charAt(i)-'0'<0||number.charAt(i)-'0'>9){
               return false;
             }
         }
         if(length==0)return false;
         return true;
     }


    //将double类型数据保留一位小数
    public static String double2dot(double num){
        String res=String.valueOf(num);
        int x=0;
        for(x=0;x<res.length();x++){
            if(res.charAt(x)=='.')break;
        }
        res=res.substring(0,x+2);
        return res;
    }
}
