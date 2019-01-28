package cn.demomaster.huan.ndktest;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.demomaster.huan.ndktest.activity.ChatActivity;
import cn.demomaster.huan.ndktest.activity.LoginActivity;
import cn.demomaster.huan.ndktest.adapter.GroupAdapter;
import cn.demomaster.huan.ndktest.helper.DBHelper;
import cn.demomaster.huan.ndktest.model.ChatGroup;
import cn.demomaster.huan.ndktest.model.ChatRecode;
import cn.demomaster.huan.ndktest.model.CommonApi;
import cn.demomaster.huan.ndktest.model.UdpData;
import cn.demomaster.huan.ndktest.model.User;
import cn.demomaster.huan.ndktest.model.control.ControlModel;
import cn.demomaster.huan.ndktest.net.RetrofitInterface;
import cn.demomaster.huan.ndktest.service.MessageService;
import cn.demomaster.huan.ndktest.service.PostMan;
import cn.demomaster.huan.ndktest.util.AppStateUtil;
import cn.demomaster.huan.quickdeveloplibrary.base.BaseActivityParent;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.ActionBarLayout;
import cn.demomaster.huan.quickdeveloplibrary.helper.NotifycationHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils;
import cn.demomaster.huan.quickdeveloplibrary.jni.BaseService;
import cn.demomaster.huan.quickdeveloplibrary.jni.ServiceHelper;
import cn.demomaster.huan.quickdeveloplibrary.jni.ServiceToken;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static cn.demomaster.huan.ndktest.model.enums.ActionType.stopAudio;
import static cn.demomaster.huan.quickdeveloplibrary.jni.BaseService.baseBinder;

public class MainActivity extends BaseActivityParent {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private ServiceToken mToken;
    public static MessageService messageService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            BaseService.BaseBinder baseBinder = (BaseService.BaseBinder) iBinder;
            messageService = (MessageService) baseBinder.getService();
            //第5步所说的在Activity里面取得Service里的binder对象
            // mBinder = (ChatBinder) iBinder;
            //第6步注册自定义回调
            messageService.setOnReceiveMessageListener(new PostMan.OnReceiveMessageListener() {
                @Override
                public void onReceive(final UdpData udpData) {
                    if (udpData == null && udpData.getRequestType() == null) {
                        return;
                    }
                    String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
                    switch (udpData.getRequestType()) {
                        case heart:
                            break;
                        case login:
                            break;
                        case control:
                            if (udpData.getMessage().getSendUserId().equals(id)) {
                                return;
                            }
                            ControlModel controlModel = JSON.parseObject(udpData.getMessage().getContent(), ControlModel.class);
                            if (controlModel.getActionType() == null) {
                                return;
                            }
                            switch (controlModel.getActionType()) {
                                case playAudio:
                                    Toast.makeText(mContext, "播放音乐", Toast.LENGTH_LONG).show();
                                    plauAudio(controlModel.getSrc());
                                    break;
                                case stopAudio:
                                    Toast.makeText(mContext, "停止播放", Toast.LENGTH_LONG).show();
                                    stopAudio();
                                    break;
                            }
                            break;
                        case sendData:
                            if (!udpData.getMessage().getSendUserId().equals(id)) {
                                NotifycationHelper.getInstance().sendChatMsg(udpData.getMessage().getContent());
                            }
                            DBHelper.insertMessage(udpData);
                            Intent intent = new Intent("action_chat_message");
                            intent.putExtra("type", "normal");
                            sendBroadcast(intent);
                            //Toast.makeText(mContext, ""+udpData.getMessage().getContent(), Toast.LENGTH_LONG).show();
                            break;
                        case getData:
                            break;
                        case logoff:
                            break;
                    }
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getChatGroupList();
        getChatRecordList();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!AppStateUtil.getInstance().isLogined()) {//退出登录后会进入此逻辑
            startActivity(new Intent(mContext, LoginActivity.class));
            MainActivity.this.finish();
        } else {
            getActionBarLayout().setActionBarModel(ActionBarLayout.ACTIONBAR_TYPE.NO_ACTION_BAR);
            getActionBarLayout().getLeftView().setVisibility(View.GONE);

            initView();
            mToken = ServiceHelper.bindToService(mContext, MessageService.class, serviceConnection);
        }

        /*// Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        Button btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bind to Service
                mToken = ServiceHelper.bindToService(MainActivity.this,MessageService.class, serviceConnection);
                //getActivity().startService(new Intent(getContext(),BaseService.class));

                //Intent mIntent = new Intent();
                // mIntent.setClass(mContext, MessageService.class);
                //mContext.startService(mIntent);
            }
        });*/
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceHelper.unbindFromService(mToken);
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        int spanCount = 2;
        //使用网格布局展示
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, spanCount));
        //设置分隔线
        //recyclerView.addItemDecoration(new GridDividerItemDecoration(mContext, spanCount));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        refreshUI();
    }

    MediaPlayer mMediaPlayer;

    void plauAudio(final String src) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            } else {
                mMediaPlayer.stop();
                mMediaPlayer = new MediaPlayer();
            }
            String path = src.trim();//
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stopAudio() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    List<ChatGroup> chatGroupList;

    private void getChatGroupList() {
        //Retrofit
        RetrofitInterface retrofitInterface = HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, "http://www.demomaster.cn/");
        String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
        retrofitInterface.getChatGroupList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        try {
                            chatGroupList = JSON.parseArray(response.getData().toString(), ChatGroup.class);

                            DBHelper.updateChatGroup(chatGroupList);
                            refreshUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onStart() {
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


    List<ChatRecode> chatRecodeList;

    private void getChatRecordList() {
        //Retrofit
        RetrofitInterface retrofitInterface = HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, "http://www.demomaster.cn/");
        String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
        String nowDaDate = SharedPreferencesHelper.getInstance().getString("dateTime", "");
        retrofitInterface.getChatRecordList(id, nowDaDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        try {
                            chatRecodeList = JSON.parseArray(response.getData().toString(), ChatRecode.class);
                            deleteChatRecordList();
                            DBHelper.updateChatRecord(chatRecodeList);
                            refreshUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onStart() {
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


    private void deleteChatRecordList() {
        //Retrofit
        RetrofitInterface retrofitInterface = HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, "http://www.demomaster.cn/");
        String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();//获得系统时间
        String nowDaDate = sdf.format(date);
        SharedPreferencesHelper.getInstance().putString("dateTime", nowDaDate);
        String sendDateTime = nowDaDate;
        retrofitInterface.deleteChatRecordList(id, sendDateTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                    }

                    @Override
                    protected void onStart() {
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    List<ChatGroup> groups;
    List<User> users;
    private RecyclerView recyclerView;

    private void refreshUI() {

        //DBHelper.insert();
        groups = DBHelper.getChatGroup();
        GroupAdapter recycleViewAdapter = new GroupAdapter(mContext, groups);
        recycleViewAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatGroup chatGroup = groups.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ChatGroup", chatGroup);
                startActivity(ChatActivity.class, bundle);
                //mBinder.sendMessge(et_message.getText().toString());
            }
        });
        recycleViewAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //获取手机震动服务
                Vibrator mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                mVibrator.vibrate(50);

                PopToastUtil.ShowToast(mContext, users.get(position).getNickname());
                return false;
            }
        });

        recyclerView.setAdapter(recycleViewAdapter);
    }
}
