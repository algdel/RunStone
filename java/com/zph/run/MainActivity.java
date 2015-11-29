package com.zph.run;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import at.markushi.ui.CircleButton;

public class MainActivity extends Activity {
    //描述相关内容 包括速度距离
    private TextView content;

    //开始定位的按钮 只需要一次
    private CircleButton btget;

    //实时反馈当先信息
    private TextView tv;

    private MapView map;

    //Map主界面
    private BaiduMap bmap;
    private CircleButton btend;
    //当前时间
    private long curtime;
    //跑步速度最大值
    private double maxSpeed = 0;
    //跑步平均速度
    private double speed;
    boolean isfirst = true;
    //测试 文本图层
    private OverlayOptions textoption;
    //维护距离变量
    private double distance;
    //定位主变量
    LocationClient mlocationclient;

    //维护经过的点
    ArrayList<LatLng> list = new ArrayList<LatLng>();

    //维护跑步循环时间，监听次数等信息
    private long looptime = (long) Utils.scantime / 1000;

    //开始跑步时间
    String starttime;

    //小说点位数保存
    DecimalFormat df = new DecimalFormat("#.0");

    //在主页面中填入的目标
    private int goal=1000;

    //完成度进度条(原声 未使用)
    private ProgressBar bar;

    //总进度条  彩色
    NumberProgressBar npb;

    //阶段进度条
    HoloCircularProgressBar bar1;

    private float f_bar1=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setContentView(R.layout.activity_main);

        Bundle bd=this.getIntent().getExtras();
        String tem=bd.getString("goal");

        //增加容错性 确定不会在此处抛出异常  先进行判断
        if(Utils.isInteger(tem)){
            goal=Integer.valueOf(tem);
        }else{
            goal=1000;
        }
        mlocationclient = new LocationClient(this);
        myLocationlistener listener = new myLocationlistener();
        mlocationclient.registerLocationListener(listener);
        init();

        bmap = map.getMap();


        //是否启用卫星图
        //bmap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        //开启定位图层
        bmap.setMyLocationEnabled(true);


        btget.setOnClickListener(new myOnclicklistener());
        btend.setOnClickListener(new myOnclicklistener());

        //设置缩放级别
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(19);
        bmap.animateMapStatus(u);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
    }

    @Override
    protected void onPause() {
        super.onPause();
        mlocationclient.stop();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
    }

    private void initclient() {
        //设置LocationClientOption
        mlocationclient.setLocOption(Utils.init());
    }

    private void init(){
        content = (TextView) findViewById(R.id.content);
        //bar=(ProgressBar)findViewById(R.id.bar);
        //bar.setVisibility(View.VISIBLE);
        map = (MapView) findViewById(R.id.mapview);
        npb=(NumberProgressBar)findViewById(R.id.npb);
        bar1=(HoloCircularProgressBar)findViewById(R.id.bar1);
        btend = (CircleButton) findViewById(R.id.btend);
        btget = (CircleButton) findViewById(R.id.btget);
        //((LocationApplication)getApplication()).mLocationResult=tv;
    }

    class myOnclicklistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btget:
                    initclient();
                    mlocationclient.start();
                    if (Utils.gPSIsOPen(getApplicationContext())) {
                        Toast.makeText(MainActivity.this, "GPS已打开", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "GPS未打开，请打开GPS模块", Toast.LENGTH_SHORT).show();
                    }
                    int status = mlocationclient.requestLocation();
                    if (status != 0)
                        Log.d("servicestart", "没有发起服务或者没有监听函数");
                    break;
                case R.id.btend:
                    mlocationclient.stop();
                    //获取唯一文件名称
                    final String filename = Utils.generateFilename();
                    //获取当前跑步截图
                    //Utils.getScreenHot((View) getWindow().getDecorView(), "/sdcard/" + filename);
                    bmap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                        //地图截屏回调接口
                        public void onSnapshotReady(Bitmap snapshot) {
                            File file = new File("/mnt/sdcard/" + filename);
                            if (file.exists()) {
                                file.delete();
                            }
                            FileOutputStream out;
                            try {
                                out = new FileOutputStream(file);
                                if (snapshot.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                                    out.flush();
                                    out.close();
                                }
                                Toast.makeText(MainActivity.this, "屏幕截图成功，图片存在: " + file.toString(),
                                        Toast.LENGTH_SHORT).show();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Intent it = new Intent();
                    Bundle bd = new Bundle();
                    bd.putString("filename", filename);
                    //传递跑步长度
                    bd.putString("distance", Utils.double2dot(distance));
                    //传递跑步速度
                    bd.putString("speed", Utils.double2dot(speed));
                    //穿肚跑步最大速度值
                    bd.putString("maxspeed", Utils.double2dot(maxSpeed));
                    //传递开始跑步时间
                    bd.putString("starttime",starttime);
                    //传递跑步时间
                    int second = (int) looptime * (Utils.scantime / 1000);
                    int minute = (second) / 60;
                    second = second % 60;
                    if(minute>=1){
                        bd.putString("spend",minute+"m:"+second+"s");
                    }
                    else{
                        bd.putString("spend",second+"s");
                    }
                    String info = "";
                    if (minute < 1) {
                        info = "今天我在RunStone上面一共耗时" + second + "秒,行程一共" + Utils.double2dot(distance) + "m,最高速度" + Utils.double2dot(maxSpeed) + "m/s,平均速度" + Utils.double2dot(speed) + "m/s";
                    } else {
                        info = "今天我在RunStone上面一共耗时" + minute + "分" + second + "秒,行程一共" + Utils.double2dot(distance) + "m,最高速度" + Utils.double2dot(maxSpeed) + "m/s,平均速度" + Utils.double2dot(speed) + "m/s";
                    }
                    bd.putString("info", info);
                    it.putExtras(bd);
                    it.setClass(MainActivity.this, result.class);
                    startActivity(it);
                    break;
            }
        }
    }

    class myLocationlistener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation.getRadius()<=20.0){
                bmap.clear();
                MyLocationData locationdate = new MyLocationData.Builder()
                        .longitude(bdLocation.getLongitude())
                        .latitude(bdLocation.getLatitude())
                        .accuracy(bdLocation.getRadius())
                        .build();
                //设置定位数据
                bmap.setMyLocationData(locationdate);


                //Latitude 纬度  Longitude  经度
                LatLng p = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                list.add(p);

                if (isfirst) {
                    distance = 0;
                    isfirst = false;
                    Toast.makeText(MainActivity.this, "定位成功!Run~", Toast.LENGTH_SHORT).show();
                    Date date=new Date();
                    DateFormat format=new SimpleDateFormat("yy.MM.dd HH:mm:ss");
                    starttime=format.format(date);
                    //LatLng point=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                    //MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(19);
                    //更新地图状态
                    //bmap.animateMapStatus(u1);
                }
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(p);
                //设置我的位置为地图中心店  animate  使活泼  使有生命  推动
                bmap.animateMapStatus(u);

                if (list.size() <= 1) ;
                else {

                    //画线
                    OverlayOptions ooPolyline = new PolylineOptions().width(15).color(0xAAFF0000).points(list);
                    bmap.addOverlay(ooPolyline);


                    int index = list.size();
                    LatLng p1 = list.get(index - 2);
                    LatLng p2 = list.get(index - 1);
                    //通过百度提供的API 获取两点之间实际距离  m
                    distance += DistanceUtil.getDistance(p1, p2);
                    int jindu=(int)(distance/goal*100);
                    if(jindu>=100){
                        npb.setProgress(100);
                    }
                    else{
                        npb.setProgress(jindu);
                    }
                    //f_bar1+=distance;
                    //if(f_bar1>=500){
                        //Toast.makeText(MainActivity.this,"500m完成",Toast.LENGTH_SHORT).show();
                      //  f_bar1=f_bar1%500;
                    //}
                    bar1.setProgress((float)(distance%500));
                    //Utils.getDistance(p1,p2);
                    //公里每小时  GPS定位时有信息
                    double speed1 = bdLocation.getSpeed();
                    speed1 = speed1 * 1000 / 3600;
                    //判断获取GPS定位速度的最大值
                    if (speed1 > maxSpeed)
                        maxSpeed = speed1;
                    speed = (distance) / looptime;
                    String str = "精度"+bdLocation.getRadius()+" 路程 :" + Utils.double2dot(distance) + "m 速度:" +Utils.double2dot(speed) + " m/s 瞬时速度 :" + Utils.double2dot(speed1) + " m/s 时间" + looptime+"s";
                    looptime = looptime + (long) Utils.scantime / 1000;
                    //content.setText(str);

                    OverlayOptions radius = new TextOptions()
                            .bgColor(0xAAFFFF00)
                            .fontSize(24)
                            .fontColor(0xFFFF00FF)
                            .text(str)
                            .position(new LatLng(p.latitude - 0.0001, p.longitude));

                    bmap.addOverlay(radius);
                }
                if(distance>=goal){
                    Toast.makeText(MainActivity.this,"完成目标！！",Toast.LENGTH_SHORT).show();
                }
                map.refreshDrawableState();
            }
            //获取当前时间毫秒数描述值
            //curtime = System.currentTimeMillis();
            else{
                Toast.makeText(MainActivity.this,"GPS问题，精度不够,请等待GPS搜星定位",Toast.LENGTH_SHORT).show();
            }
        }
    }
}