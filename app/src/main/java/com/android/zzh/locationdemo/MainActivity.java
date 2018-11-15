package com.android.zzh.locationdemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
        if (Build.VERSION.SDK_INT>=23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
                requestPermissions( new String[]{ Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE },100 );
            } else {
                initData();
                initUI();
            }
        }else {
            initData();
            initUI();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //获取到权限，做相应处理
                //调用定位SDK应确保相关权限均被授权，否则会引起定位失败
                initData();
                initUI();
            } else{
                //没有获取到权限，做特殊处理
            }
        }
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
