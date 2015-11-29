package com.zph.run;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class history extends Activity {

    private ListView list;
    private DbHelper dbhelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);

        list=(ListView)findViewById(R.id.list);

        dbhelper=new DbHelper(this,"record",null,1);
        db=dbhelper.getReadableDatabase();
        Cursor c=db.query("tb_history", new String[]{"_id", "time", "distance", "speed", "maxspeed", "spend"}, null, null, null, null, null);
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.history_layout,
                c,new String[]{"time","distance","speed","maxspeed","spend"},new int[]{R.id.time,R.id.distance,R.id.speed,R.id.maxspeed,R.id.spend});
        this.list.setAdapter(adapter);
    }

   /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
    }
   * */
}
