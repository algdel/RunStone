package com.zph.run;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import at.markushi.ui.CircleButton;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class result extends Activity {

    String filename;
    private CircleButton btshrare;
    String distance;
    String speed;
    String maxspeed;
    private CircleButton save;
    private ImageView view;
    String spend;
    String starttime;
    String info;
    boolean isfirstclick=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.result);
        save=(CircleButton)findViewById(R.id.save);
        view=(ImageView)findViewById(R.id.view);
        Bundle bd=this.getIntent().getExtras();
        filename=bd.getString("filename");
        spend=bd.getString("spend");
        speed=bd.getString("speed");
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/"+filename);
        view.setImageBitmap(bitmap);
        distance=bd.getString("distance");
        maxspeed=bd.getString("maxspeed");
        info=bd.getString("info");
        starttime=bd.getString("starttime");
        btshrare=(CircleButton)findViewById(R.id.share);

        btshrare.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isfirstclick){
                    ContentValues value=new ContentValues();
                    value.put("time",starttime);
                    value.put("distance",distance);
                    value.put("speed",speed);
                    value.put("maxspeed",maxspeed);
                    value.put("spend", spend);
                    DbHelper dbHelper=new DbHelper(result.this,"record",null,1);
                    SQLiteDatabase db=dbHelper.getWritableDatabase();
                    long status;
                    status=db.insert("tb_history",null,value);
                    if(status!=-1){
                        Toast.makeText(result.this, "记录保存至数据库", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(result.this,"保存失败",Toast.LENGTH_SHORT).show();
                    }
                    isfirstclick=false;
                    save.setEnabled(false);
                }
            }
        });
    }
    private void showShare() {
        //ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(info+",大家一块来跟我锻炼吧~");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://weibo.com/u/1307443537?source=blog&sudaref=control.blog.sina.com.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(info+",大家一块来跟我锻炼吧~");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/"+filename);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://weibo.com/u/1307443537?source=blog&sudaref=control.blog.sina.com.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("今天我在RunStone上跑了"+distance+"m,大家一起来跟我锻炼吧~");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("http://weibo.com/u/1307443537?source=blog&sudaref=control.blog.sina.com.cn");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://weibo.com/u/1307443537?source=blog&sudaref=control.blog.sina.com.cn");

// 启动分享GUI
        oks.show(this);
    }
}
