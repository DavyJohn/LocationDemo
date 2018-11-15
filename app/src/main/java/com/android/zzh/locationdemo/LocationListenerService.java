package com.android.zzh.locationdemo;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class LocationListenerService extends IntentService implements LocationListener {
    public static final String TAG = "LocationListenerService";
    public static final String SERVICE_NAME = "LocationListenerService";
    private LocationManager locationManager;
    private int count=0;
    private Location location = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public LocationListenerService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        locationManager = (LocationManager) getSystemService(getApplication().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        String provider = locationManager.getBestProvider(criteria, true);
        System.out.print(provider);

        // 检查定位服务是否打开
        if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null
                || locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            Log.i(TAG, "正在定位");
            // Permission check
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locationManager.getLastKnownLocation(provider);
            //设置数据更新的条件，参数分别为1，使用GPS 2，最小时间间隔 3000毫秒 3，最短距离 100  4,设置事件监听者 this(类继承了Locationlistener)
            while (location == null){
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                locationManager.requestLocationUpdates(provider, 3000, 0, this);
            }
            double lan = location.getLatitude();
            double lon = location.getLongitude();
            Toast.makeText(this,"lat:"+lan+"\n"+",long:"+lon,Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "无法定位");
            Toast.makeText(this, "无法定位，请打开定位服务", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "LocationChanged");
        double latitude = location.getLatitude();
        Log.i(TAG, "纬度 =" + latitude);
        double longitude = location.getLongitude();
        Log.i(TAG, "经度 =" + longitude);
        //将location作为参数传递给广播
        BCL(location);
        Toast.makeText(this,"lat:"+latitude+"\n"+",long:"+longitude,Toast.LENGTH_LONG).show();
    }

    /**
     *   将经纬度信息赋值给一个ArrayList
     * @param location
     * @return  arraylist
     */

    private ArrayList<String> getConcretMessage(Location location) {
        ArrayList<String> arrayList=new ArrayList<String>();
        arrayList.add(location.getProvider()+"");
        arrayList.add(location.getAccuracy()+"");
        arrayList.add(location.getAltitude()+"");
        arrayList.add(location.getBearing()+"");
        arrayList.add(location.getExtras()+"");
        arrayList.add(location.getSpeed()+"");
        arrayList.add(location.getLatitude()+"");
        arrayList.add(location.getLongitude()+"");
        return arrayList ;
    }

    /**
     *   使用广播将经纬度等信息传递出去
     * @param location
     */
    private void BCL(Location location) {
        PendingIntent pi = getPI(location);
        //permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //第四个参数为PendingIntent
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, pi);
    }

    /**
     *
     * @param location
     * @return   返回一个已包装数据的PendingIntent
     */
    private PendingIntent getPI(Location location) {
        Intent intent = new Intent();
        intent.setAction("location1");
        for(int i=0;i<getConcretMessage(location).size();i++){
            intent.putExtra("DATA"+i,getConcretMessage(location).get(i));
            if(count>=8){
                count=0;
                count++;
            }else{
                count++;
            }
        }
        intent.putExtra("DATA",count+"");
        return PendingIntent.getBroadcast(getApplication(), 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        startId=START_NOT_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }
}

