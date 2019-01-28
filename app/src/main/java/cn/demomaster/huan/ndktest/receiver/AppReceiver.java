package cn.demomaster.huan.ndktest.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import cn.demomaster.huan.quickdeveloplibrary.helper.NotifycationHelper;
import cn.demomaster.huan.quickdeveloplibrary.receiver.ApplicationReceiver;

/**
 * @author squirrel桓
 * @date 2019/1/24.
 * description：
 */
public class AppReceiver extends ApplicationReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //super.onReceive(context, intent);
        Bundle bundle = intent.getExtras();
        if(bundle!=null&&bundle.containsKey("message")){
            String message = bundle.getString("message","");
            NotifycationHelper.getInstance().init(context);
            NotifycationHelper.getInstance().sendChatMsg(message);
        }
        /*Intent sintent=new Intent("com.huan.squirrel.xuexue.service.ChatService");
        //context.startService(sintent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(sintent);
        } else {
            context.startService(sintent);
        }*/
    }
}
