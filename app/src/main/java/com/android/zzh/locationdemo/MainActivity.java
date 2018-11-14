package com.android.zzh.locationdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private  boolean CL=true;
    TextView textView;
    ListView listme;
    private static final String TAG ="LocationListenerService" ;
    private  BC bc=new BC();
    private String[] str=new String[8];
    private ArrayList<String> nameList=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initUI();
    }

    /**
     * 初始化listvie数据
     */
    private void initData() {
        nameList.add("Provider :");
        nameList.add("Accuracy :");
        nameList.add("Altitude :");
        nameList.add("Bearing :");
        nameList.add("Extras :");
        nameList.add("Speed:");
        nameList.add("Latitude:");
        nameList.add("Longitude:");
        for(int i=0;i<8;i++){
            str[i]="initting";
        }
    }

    private void initUI() {
        textView=(TextView)findViewById(R.id.text2);
        listme=(ListView)findViewById(R.id.listme);
        Button text_show=(Button)findViewById(R.id.text_show);
        text_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //动态注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("location1");
        registerReceiver(bc, intentFilter);

    }
    //自定义 BroadcastReceiver
    public class BC extends BroadcastReceiver {
        public static final String TAG="BC";

        @Override
        public void onReceive(Context context, Intent intent) {
            int count=Integer.parseInt(intent.getStringExtra("DATA"));
            for(int i=0;i<count;i++){
                str[i]=intent.getStringExtra("DATA"+i);
            }
            //为数据展示所在的listview添加一个适配器
            listme.setAdapter(new MyAdapter());
        }
    }
    //自定义数据适配器
    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return nameList.size();
        }

        @Override
        public Object getItem(int position) {
            return nameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(getApplicationContext(),R.layout.list_view,null);
            TextView text_name=(TextView)view.findViewById(R.id.text_name);
            TextView text_value=(TextView)view.findViewById(R.id.text_value);
            text_name.setText(nameList.get(position));
            text_value.setText(str[position]);
            return view;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑广播接收者
        unregisterReceiver(bc);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startServer();
    }

    /**
     * 开启服务
     */
    public void startServer(){
        Intent intent = new Intent(MainActivity.this,LocationListenerService.class);
        startService(intent);
    }

}
