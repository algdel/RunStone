package com.zph.run;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import net.simonvt.numberpicker.NumberPicker;

import at.markushi.ui.CircleButton;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class Main extends Activity {

    private CircleButton btset;
    private CircleButton btin,btout;
    private Button share;
    private Button screenshoot;
    private CircleButton history;
    private CircleButton planlist;
    //private Spinner sp;
    private NumberPicker np;
    //EditText info;
    String filename="";
    String goal="2000";
    String[] dis={"1000","2000","3000","4000","5000","6000","7000","8000","9000","10000"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.main);

        init();
        history.setOnClickListener(new myOnclicklistener());
        btset.setOnClickListener(new myOnclicklistener());
        btout.setOnClickListener(new myOnclicklistener());
        btin.setOnClickListener(new myOnclicklistener());
        planlist.setOnClickListener(new myOnclicklistener());

        np.setOnValueChangedListener(new myOnValueChangeListener());
        //info.setOnClickListener(new myOnclicklistener());
        /*share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });*/
        /*screenshoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename=Utils.generateFilename();
                Utils.getScreenHot((View) getWindow().getDecorView(), "/sdcard/" + filename);
            }
        });*/

        /*ArrayAdapter ada=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,dis);
        sp.setAdapter(ada);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                goal = dis[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                goal = "2000";
            }
        });*/
    }
    class myOnclicklistener implements View.OnClickListener{

        Intent it;
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btset:
                    it=new Intent();
                    it.setClass(Main.this,nextplan.class);
                    startActivity(it);
                    break;
                case R.id.shiwai:
                    // if(tp.is)
                    /*long now=System.currentTimeMillis();
                    Bundle bd=new Bundle();
                    bd.putLong("time",now);
                    it.putExtras(bd);*/
                    it=new Intent();
                    it.setClass(Main.this, MainActivity.class);
                    Bundle bd=new Bundle();
                    bd.putString("goal", goal);
                    it.putExtras(bd);
                    startActivity(it);
                    break;
                case R.id.history:
                    it=new Intent();
                    it.setClass(Main.this, com.zph.run.history.class);
                    startActivity(it);
                    break;
                case R.id.shinei:
                    it = new Intent();
                    //it.setClass(Main.this, test1.class);
                    it.setClass(Main.this,Pedometer.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("run", false);
                    it.putExtras(bundle);
                    startActivity(it);
                    break;
                case R.id.planlist:
                    it = new Intent();
                    it.setClass(Main.this, plan.class);
                    startActivity(it);
                    break;
            }
        }
    }

    private void init(){
        btset=(CircleButton)findViewById(R.id.btset);
        btout=(CircleButton)findViewById(R.id.shiwai);
        btin=(CircleButton)findViewById(R.id.shinei);
        history=(CircleButton)findViewById(R.id.history);
        //sp=(Spinner)findViewById(R.id.spinner);
        //share=(Button)findViewById(R.id.share);
        //screenshoot=(Button)findViewById(R.id.screenshoot);
        //info=(EditText)findViewById(R.id.info);
        planlist=(CircleButton)findViewById(R.id.planlist);
        np=(NumberPicker)findViewById(R.id.numberPicker);
        String[] city = {"1000","2000","3000","4000","5000","6000","7000","8000","9000","10000"};
        np.setDisplayedValues(city);
        np.setMinValue(0);
        np.setMaxValue(city.length - 1);
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    //一键分享功能
    private void showShare() {
        //ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("我今天在Run上跑了~");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("哈哈哈马学森");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("今天我在RunStone上跑了4672m,大家一起来跟我锻炼吧~");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/runstone/"+filename);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://weibo.com/u/1307443537?source=blog&sudaref=control.blog.sina.com.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("今天我在RunStone上跑了,大家一起来跟我锻炼吧~");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("http://weibo.com/u/1307443537?source=blog&sudaref=control.blog.sina.com.cn");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://weibo.com/u/1307443537?source=blog&sudaref=control.blog.sina.com.cn");

// 启动分享GUI
        oks.show(this);
    }

    class myOnValueChangeListener implements NumberPicker.OnValueChangeListener {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            goal=dis[newVal];
        }
    }
}
