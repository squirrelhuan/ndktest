package cn.demomaster.huan.ndktest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cn.demomaster.huan.ndktest.R;

import static cn.demomaster.huan.quickdeveloplibrary.base.BaseActivityRoot.TAG;

/**
 * Created by Squirrel桓 on 2019/1/26.
 */
public class GuardService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "守护服务 onCreate");
        super.onCreate();

/*

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction("android.intent.action.USER_PRESENT");
        //registerReceiver(conncetReceiver, filter);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "chat").build();
        // Notification notification = new Notification(,"chat");
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(0, notification);//如何让通知不显示呢？只需要将id设为0即可。
*/
        sendBroadcast( GuardService.this);
        /*final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendBroadcast( GuardService.this);
            }
        }, 0, 5000);*/

       /* if(call_thread ==null){
            call_thread = new Call_Thread(this);
        }
        call_thread.start();*/
    }

   static Call_Thread call_thread =null;
    // 心跳线程类
    static class Call_Thread extends Thread {
        Service service;
        Call_Thread(Service service){
            this.service = service;
        }
        @Override
        public void run() {
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendBroadcast( service);
                }
            }, 0, 5000);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "守护服务 onStartCommand");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        flags = Service.START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        Log.d(TAG, "守护服务onDestroy");
        super.onDestroy();
        sendBroadcast(this);

    }

    private static void sendBroadcast(Service service) {
        Intent intent = new Intent(ACTION);
        intent.putExtra("type", "normal");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        // intent.addFlags(Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND)
        intent.setClassName(service.getClass().getPackage().getName(), ACTION);
        intent.setPackage(service.getClass().getPackage().getName());
        Bundle bundle = new Bundle();
        bundle.putString("message", "您有新的消息");
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= 23) {
            ComponentName componentName = new ComponentName(service.getApplicationContext(), ACTION);//参数1-包名 参数2-广播接收者所在的路径名
            intent.setComponent(componentName);
        }
               /* if(Build.VERSION.SDK_INT >= 26) {
                    intent.addFlags(0x01000000);//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题}
                }*/
        service.sendBroadcast(intent);
        Log.d(TAG, "守护服务 sendBroadcast[" + ACTION + "]");
    }

    public final static String ACTION = "cn.demomaster.huan.ndktest.receiver.ServiceReceiver";

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 通知渠道的id
        String id = "my_channel_01";
        // 用户可以看到的通知渠道的名字.
        CharSequence name = "chat";
//         用户可以看到的通知渠道的描述
        String description = "chat message";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//         配置通知渠道的属性
        mChannel.setDescription(description);
//         设置通知出现时的闪灯（如果 android 设备支持的话）
        //mChannel.enableLights(true); mChannel.setLightColor(Color.RED);
//         设置通知出现时的震动（如果 android 设备支持的话）
        //mChannel.enableVibration(false);
        //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//         最后在notificationmanager中创建该通知渠道 //
        mNotificationManager.createNotificationChannel(mChannel);

        // 为该通知设置一个id
        int notifyID = 1;
        // 通知渠道的id
        String CHANNEL_ID = "my_channel_01";
        // Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle("守护进程") .setContentText("You've received new messages.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId(CHANNEL_ID)
                .build();
        startForeground(1,notification);
    }
}