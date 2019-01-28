package cn.demomaster.huan.ndktest.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;


import cn.demomaster.huan.ndktest.App;
import cn.demomaster.huan.ndktest.R;
import cn.demomaster.huan.ndktest.activity.LoginActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;

public class AppStateUtil {
    private static Context context;

    //第一次打开app
    public static String APP_STATE_IS_FIRST_OPEN = "APP_STATE_IS_FIRST_OPEN";
    public static String APP_STATE_IS_LOGINED = "APP_STATE_IS_LOGINED";
    public static String APP_MODE_IS_NORMAL = "APP_MODE_IS_NORMAL";
    public static String APP_MODE_IS_TOURIST = "APP_MODE_IS_TOURIST";
    //升级提示
    public static String APP_SETTING_UPDATEAPP_HINTS = "APP_SETTING_UPDATEAPP_HINTS";

    private static AppStateUtil instance;

    public static AppStateUtil init(Context context) {
        if (instance == null) {
            instance = new AppStateUtil(context);
        }
        return instance;
    }

    public static AppStateUtil getInstance() {
        return instance;
    }

    public AppStateUtil(Context context) {
        this.context = context;
        //this.sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);

    }

    public static boolean isFirstOpen() {
        return SharedPreferencesHelper.getInstance().getBoolean(APP_STATE_IS_FIRST_OPEN, true);
    }

    public static void setIsFirstOpen(boolean isfirst) {
        SharedPreferencesHelper.getInstance().putBoolean(APP_STATE_IS_FIRST_OPEN, isfirst);
    }

    public static boolean isLogined() {
        return SharedPreferencesHelper.getInstance().getBoolean(APP_STATE_IS_LOGINED, false);
    }

    public static void setAppStateIsLogined(boolean b) {
        SharedPreferencesHelper.getInstance().putBoolean(APP_STATE_IS_LOGINED, b);
    }

    public void setIsNormal(boolean b) {//true客户真实数据，false游客模式
        SharedPreferencesHelper.getInstance().putBoolean(APP_MODE_IS_NORMAL, b);
    }

    public boolean IsNormal() {
        return SharedPreferencesHelper.getInstance().getBoolean(APP_MODE_IS_NORMAL, true);
    }

    public void setIsTourist(boolean b) {
        SharedPreferencesHelper.getInstance().putBoolean(APP_MODE_IS_TOURIST, b);
    }

    public boolean IsTourist() {
        return SharedPreferencesHelper.getInstance().getBoolean(APP_MODE_IS_TOURIST, false);
    }

    public void setUpdateAppHints(boolean b) {
        SharedPreferencesHelper.getInstance().putBoolean(APP_SETTING_UPDATEAPP_HINTS, b);
    }

    public static boolean getUpdateAppHints() {
        return SharedPreferencesHelper.getInstance().getBoolean(APP_SETTING_UPDATEAPP_HINTS, true);
    }


    public void logout(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        (context).startActivity(intent);
        getInstance().setIsTourist(false);
        getInstance().setAppStateIsLogined(false);
        //UserHelper.clear();
        App.getInstance().deleteAllActivity();
    }

    public static final String TAG = "AppStateUtil";
    public static final int INSTALL_PERMISS_CODE = 47721;
    public static final int REQUEST_PERMISS_CODE = 37721;

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Activity context) {

        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivityForResult(intent, INSTALL_PERMISS_CODE);

    }


    // 获取本地的版本号
    private static int getVersionCode(Context context) {
        int versionCode = -1;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    // 获取本地的版本名称
    public String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    /**
     * alert 消息提示框显示
     *
     * @param context  上下文
     * @param title    标题
     * @param message  消息
     * @param listener 监听器
     */
    public static void showAlert(Context context, String title, String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", listener);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static String[] PERMISSIONS_ALL = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.SYSTEM_ALERT_WINDOW};

}
