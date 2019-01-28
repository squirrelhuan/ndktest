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
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.alibaba.fastjson.JSON;

import java.io.IOException;

import cn.demomaster.huan.ndktest.R;
import cn.demomaster.huan.ndktest.model.Message;
import cn.demomaster.huan.ndktest.model.UdpData;
import cn.demomaster.huan.ndktest.model.User;
import cn.demomaster.huan.ndktest.model.enums;
import cn.demomaster.huan.ndktest.receiver.ServiceReceiver;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.jni.BaseService;
import cn.demomaster.huan.quickdeveloplibrary.jni.Watcher;

import static cn.demomaster.huan.quickdeveloplibrary.base.BaseActivityRoot.TAG;
import static cn.demomaster.huan.quickdeveloplibrary.jni.BaseService.baseBinder;

/**
 * Created by Squirrel桓 on 2019/1/26.
 */
public class MessageService extends Service {

    public static interface OnReceiveMessageListener {
        void onReceive(UdpData udpData);
    }

    private PostMan.OnReceiveMessageListener onReceiveMessageListener = new PostMan.OnReceiveMessageListener() {
        @Override
        public void onReceive(UdpData udpData) {
            if (listener != null) {
                listener.onReceive(udpData);
            } else {
                Intent intent = new Intent();
                intent.setAction("com.huan.squirrel.ndktest.receiver.AppReceiver");
                intent.putExtra("type", "normal");
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                // intent.addFlags(Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND)
                intent.setClassName(this.getClass().getPackage().getName(), "com.huan.squirrel.ndktest.receiver.AppReceiver");
                intent.setPackage(this.getClass().getPackage().getName());
                Bundle bundle = new Bundle();
                bundle.putString("message", "您有新的消息");
                intent.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= 23) {
                    ComponentName componentName = new ComponentName(getApplicationContext(), "com.huan.squirrel.ndktest.receiver.AppReceiver");//参数1-包名 参数2-广播接收者所在的路径名
                    intent.setComponent(componentName);
                }
               /* if(Build.VERSION.SDK_INT >= 26) {
                    intent.addFlags(0x01000000);//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题}
                }*/
                sendBroadcast(intent);
            }
        }
    };

    private PostMan.OnReceiveMessageListener listener;

    public void setOnReceiveMessageListener(final PostMan.OnReceiveMessageListener listener) {
        this.listener = listener;
    }

   /* @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        mBinder = new ChatBinder(this);
        return mBinder;
    }*/

    private static PostMan postMan = null;
    private static String server_ip_test = "192.168.0.102";
    private static String server_ip_rela = "118.25.63.138";
    private static String server_ip = server_ip_rela;// ;"192.168.31.199""192.168.0.102"
    private static int server_port = 8000;
    private static int client_port = 7080;

    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(getApplicationContext(), "service onBind",Toast.LENGTH_SHORT).show();
        if (baseBinder == null)
            baseBinder = new BaseService.BaseBinder(this);
        return baseBinder;
    }

    private RecyclerView recyclerView;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        startGuardService();
        //DeviceEngine.getInst().init(this.getApplicationContext());


      /* IntentFilter filter = new IntentFilter();
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
        startForeground(0, notification);//如何让通知不显示呢？只需要将id设为0即可。*/

        try {
            String packageName = getApplicationContext().getPackageName();
            String serviceName = this.getClass().getName();
            Log.i(TAG, "packageName=" + packageName + ",serviceName=" + serviceName);
            Watcher watcher = new Watcher();
            watcher.createWatcher(String.valueOf(Process.myUid()), packageName, serviceName);
            watcher.connectMonitor();
            Log.i(TAG, "守护进程已启动");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CGQ", "" + e.getMessage());
        }

        try {
            postMan = new PostMan(server_ip, server_port);
            postMan.setOnReceiveMessageListener(onReceiveMessageListener);
            String nickname = SharedPreferencesHelper.getInstance().getString("nickname", "");
            User user = new User();
            user.setNickname(nickname);
            String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
            user.setId(id);
            sendMessage("", nickname + "上线了", user);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CGQ", "" + e.getMessage());
        }
        //Heartbeat_Thread heartbeat_thread = new Heartbeat_Thread();
        heartbeat_thread.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ServiceDemo onStartCommand");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        Intent intent = new Intent(ACTION);
        intent.putExtra("type", "normal");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        // intent.addFlags(Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND)
        intent.setClassName(this.getClass().getPackage().getName(), ACTION);
        intent.setPackage(this.getClass().getPackage().getName());
        Bundle bundle = new Bundle();
        bundle.putString("message", "您有新的消息");
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= 23) {
            ComponentName componentName = new ComponentName(getApplicationContext(), ACTION);//参数1-包名 参数2-广播接收者所在的路径名
            intent.setComponent(componentName);
        }
               /* if(Build.VERSION.SDK_INT >= 26) {
                    intent.addFlags(0x01000000);//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题}
                }*/
        sendBroadcast(intent);
        // unregisterReceiver(conncetReceiver);
        Log.d(TAG, "sendBroadcast[" + ACTION + "]");

    }
    private void createErrorNotification() {
        Notification notification = new Notification.Builder(this).build();
        startForeground(0, notification);
    }
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
       // mChannel.enableLights(true); mChannel.setLightColor(Color.RED);
//         设置通知出现时的震动（如果 android 设备支持的话）
        //mChannel.enableVibration(true);
        //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//         最后在notificationmanager中创建该通知渠道 //
        mNotificationManager.createNotificationChannel(mChannel);

        // 为该通知设置一个id
        int notifyID = 1;
        // 通知渠道的id
        String CHANNEL_ID = "my_channel_01";
        // Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle("消息进程") .setContentText("You've received new messages.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId(CHANNEL_ID)
                .build();
        startForeground(1,notification);
    }

    public void startGuardService() {
        Intent intent = new Intent();
        intent.setClass(this, GuardService.class);
        startService(intent);
    }

    public final static String ACTION = "cn.demomaster.huan.ndktest.receiver.ServiceReceiver";


    MessageService.Heartbeat_Thread heartbeat_thread = new MessageService.Heartbeat_Thread();

    // 心跳线程类
    static class Heartbeat_Thread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(20000);
                    Log.i("CGQ", "发送心跳包...");

                    UdpData udpData = new UdpData();
                    udpData.setId((int) (Math.random() * 1000) + "");
                    udpData.setRequestType(enums.requestType.heart);
                    Message message = new Message();
                    message.setContent("我是pc心跳包");
                    User user_sender = new User();
                    String nickname = SharedPreferencesHelper.getInstance().getString("nickname", "");
                    user_sender.setUsername(nickname);
                    user_sender.setPassword("112233");
                    String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
                    user_sender.setId(id);
                    message.setReciveGroupId("");
                    message.setSendUserId(user_sender.getId());
                    udpData.setMessage(message);

                    String sendStr = JSON.toJSONString(udpData);
                    postMan.sendMessage(sendStr);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(String groupId, String content, User user) {
        sendMessageToUser(groupId, content, user, enums.requestType.sendData);
    }

   /* public void sendMessage(int groupId,String str,enums.requestType requestType){
        User user_reciver = new User();
        user_reciver.setId("4");
        sendMessageToUser(groupId,str,user_reciver,requestType);
    }*/


    public void sendMessageToUser(String groupId, String str, User reciveUser, enums.requestType requestType) {

        UdpData udpData = new UdpData();
        udpData.setId(groupId);
        udpData.setRequestType(requestType);
        Message message = new Message();
        message.setContent(str);
        String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
        message.setSendUserId(id);
        message.setReciveGroupId(groupId);
        udpData.setMessage(message);

        String sendStr = JSON.toJSONString(udpData);
        postMan.sendMessage(sendStr);
    }
}