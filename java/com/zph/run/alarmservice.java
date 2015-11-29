package com.zph.run;

/**
 * Created by Administrator on 2015/11/12.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class alarmservice extends Service {

      static Timer timer = null;
//清除通知
     public  static void cleanAllNotification(Context context) {
    NotificationManager mn= (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
     mn.cancelAll();
     if (timer != null) {
             timer.cancel();
             timer = null;
         }
 }

     //添加通知
     public static void addNotification(Context context,int[] tia,String tickerText,String contentTitle,String contentText)
     {
         Intent intent = new Intent(context, alarmservice.class);
         intent.putExtra("when", tia);
         intent.putExtra("tickerText", tickerText);
         intent.putExtra("contentTitle", contentTitle);
         intent.putExtra("contentText", contentText);
         context.startService(intent);
     }
    @Override
     public void onCreate() {
         Log.e("addNotification", "===========create=======");
         //Toast.makeText(this,"service create", Toast.LENGTH_SHORT).show();
     }
     @Override
    public IBinder onBind(Intent arg0) {
     // TODO Auto-generated method stub
     return null;
    }
    @Override
    public void onStart(final Intent intent,int startid){
        //int[] tia=intent.getIntArrayExtra("when");
        //Date when=new Date(tia[0],tia[1]-1,tia[2],tia[3],tia[4],0);
        long delay=intent.getLongExtra("when",1000*12);
        if (null == timer) {
            timer = new Timer();
        }
        //DateFormat format=new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        //String starttime=format.format(when);
        //Toast.makeText(this,"Service start",Toast.LENGTH_SHORT).show();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                //Looper.prepare();
                // TODO Auto-generated method stub
                NotificationManager mn = (NotificationManager) alarmservice.this.getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(alarmservice.this);
                Intent notificationIntent = new Intent(alarmservice.this, Main.class);//点击跳转位置
                PendingIntent contentIntent = PendingIntent.getActivity(alarmservice.this, 0, notificationIntent, 0);
                builder.setContentIntent(contentIntent);
                builder.setSmallIcon(R.drawable.app);
                notificationIntent.putExtra("distance", intent.getStringExtra("distance"));
                builder.setTicker(intent.getStringExtra("tickerText")); //测试通知栏标题
                builder.setContentText(intent.getStringExtra("contentText")); //下拉通知啦内容
                builder.setContentTitle(intent.getStringExtra("contentTitle"));//下拉通知栏标题
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_ALL);
                Notification notification = builder.build();
                mn.notify(1, notification);
                //Looper.loop();
                //Toast.makeText(alarmservice.this,"haaaaaaggaaaaaa",Toast.LENGTH_SHORT).show();
                //Log.e("hadddddgfgfghfghfghaaaa","service");
            }
        }, delay);
    }
     /*public int onStartCommand(final Intent intent, int flags, int startId) {

         //long period = 24 * 60 * 60 * 1000; //24小时一个周期
         //int delay = intent.getIntExtra("delayTime", 0);
         //Date when
         int[] tia=intent.getIntArrayExtra("when");
         Date when=new Date(tia[0],tia[1],tia[2],tia[3],tia[4],0);
         if (null == timer) {
             timer = new Timer();
         }
         timer.schedule(new TimerTask() {

             @Override
             public void run() {
                 // TODO Auto-generated method stub
                 NotificationManager mn = (NotificationManager) alarmservice.this.getSystemService(NOTIFICATION_SERVICE);
                 Notification.Builder builder = new Notification.Builder(alarmservice.this);
                 Intent notificationIntent = new Intent(alarmservice.this, Main.class);//点击跳转位置
                 PendingIntent contentIntent = PendingIntent.getActivity(alarmservice.this, 0, notificationIntent, 0);
                 builder.setContentIntent(contentIntent);
                 //builder.setSmallIcon(R.drawable.ic_launcher);
                 builder.setTicker(intent.getStringExtra("tickerText")); //测试通知栏标题
                 builder.setContentText(intent.getStringExtra("contentText")); //下拉通知啦内容
                 builder.setContentTitle(intent.getStringExtra("contentTitle"));//下拉通知栏标题
                 builder.setAutoCancel(true);
                 builder.setDefaults(Notification.DEFAULT_ALL);
                 Notification notification = builder.build();
                 mn.notify((int) System.currentTimeMillis(), notification);
             }
         }, when);

         return super.onStartCommand(intent, flags, startId);
     }*/

     @Override
    public void onDestroy(){
         Log.e("addNotification", "===========destroy=======");
         super.onDestroy();
         //this.stopService();
     }
}