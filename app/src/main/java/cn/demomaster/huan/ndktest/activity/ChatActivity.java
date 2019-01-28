package cn.demomaster.huan.ndktest.activity;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.demomaster.huan.ndktest.R;
import cn.demomaster.huan.ndktest.adapter.ChatAdapter;
import cn.demomaster.huan.ndktest.adapter.ChatMenuAdapter;
import cn.demomaster.huan.ndktest.helper.DBHelper;
import cn.demomaster.huan.ndktest.model.ChatGroup;
import cn.demomaster.huan.ndktest.model.ChatRecode;
import cn.demomaster.huan.ndktest.model.control.ControlModel;
import cn.demomaster.huan.ndktest.model.enums;
import cn.demomaster.huan.quickdeveloplibrary.base.BaseActivityParent;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.ActionBarLayout;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.CPopupWindow;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.decorator.GridDividerItemDecoration;

import static cn.demomaster.huan.ndktest.MainActivity.messageService;


public class ChatActivity extends BaseActivityParent {

    private RecyclerView recyclerView_menu;
    private ChatGroup chatGroup;

    public static class MessageBroadcastReceiver extends BroadcastReceiver {
        private OnReceiveListener onReceiveListener;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (onReceiveListener != null) {
                onReceiveListener.onReceive();
            }
        }

        public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
            this.onReceiveListener = onReceiveListener;
        }

        public static interface OnReceiveListener {
            void onReceive();
        }
    }

    private MessageBroadcastReceiver messageBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);

        messageBroadcastReceiver = new MessageBroadcastReceiver();
        messageBroadcastReceiver.setOnReceiveListener(new MessageBroadcastReceiver.OnReceiveListener() {
            @Override
            public void onReceive() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshUI();
                    }
                });
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("action_chat_message");
        registerReceiver(messageBroadcastReceiver, filter);

        getActionBarLayout().setActionBarModel(ActionBarLayout.ACTIONBAR_TYPE.NORMAL);
        getActionBarLayout().setBackGroundColor(getResources().getColor(R.color.lawngreen));
        getActionBarLayout().setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChatMenu(view);
            }
        });

        mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey("ChatGroup")) {
            chatGroup = (ChatGroup) mBundle.getSerializable("ChatGroup");
            if (chatGroup != null) {
                getActionBarLayout().setTitle(chatGroup.getName());
            }
        }

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageBroadcastReceiver);
    }

    private PopupWindow popupWindow;

    private void showChatMenu(View view) {
        CPopupWindow.PopBuilder builder = new CPopupWindow.PopBuilder((Activity) mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_menu, null, false);

        recyclerView_menu = contentView.findViewById(R.id.recyclerView_menu);
        int spanCount = 4;
        //使用网格布局展示
        recyclerView_menu.setLayoutManager(new GridLayoutManager(mContext, spanCount));
        //设置分隔线
        recyclerView_menu.addItemDecoration(new GridDividerItemDecoration(mContext, spanCount));
        //设置增加或删除条目的动画
        recyclerView_menu.setItemAnimator(new DefaultItemAnimator());
        final String menus[] = {"消息", "播放音乐", "停止音乐", "图片", "知否知否", "半壶纱", "买了否冷"};
        stringList = new ArrayList<>();
        for (int i = 0; i < menus.length; i++) {
            stringList.add(menus[i]);
        }
        ChatMenuAdapter recycleViewAdapter = new ChatMenuAdapter(mContext, stringList);


        recycleViewAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取手机震动服务
                Vibrator mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                mVibrator.vibrate(50);
                ControlModel controlModel = new ControlModel();
                switch (position) {
                    case 0:
                        messageService.sendMessage(chatGroup.getGroupId(),stringList.get(position), chatGroup.getUsers().get(0));
                        break;
                    case 1:
                        controlModel = new ControlModel();
                        controlModel.setActionType(enums.ActionType.playAudio);//.ActionType.playAudio
                        controlModel.setSrc("http://140.206.235.104/amobile.music.tc.qq.com/C400003397GY0UFJB6.m4a?guid=9260526215&amp;vkey=1A02492C9AF8F9D4ABF500130439262817E0E9812A12F7F8A3C5723B5B025D5C9A5D66EC1D4731AE5C6B8315D440E6E2348B4ACA76112038&amp;uin=0&amp;fromtag=66");
                        messageService.sendMessageToUser(chatGroup.getGroupId(),JSON.toJSONString(controlModel), chatGroup.getUsers().get(0), enums.requestType.control);
                        break;
                    case 2:
                        controlModel = new ControlModel();
                        controlModel.setActionType(enums.ActionType.stopAudio);
                        messageService.sendMessageToUser(chatGroup.getGroupId(),JSON.toJSONString(controlModel), chatGroup.getUsers().get(0), enums.requestType.control);
                        break;
                    case 3:
                        messageService.sendMessageToUser(chatGroup.getGroupId(),stringList.get(position), chatGroup.getUsers().get(0), enums.requestType.sendData);
                        break;
                    case 4:
                        ControlModel controlModel23 = new ControlModel();
                        controlModel23.setActionType(enums.ActionType.playAudio);
                        controlModel23.setSrc("http://140.207.247.18/amobile.music.tc.qq.com/C400001IqfAs2KZzeZ.m4a?guid=9260526215&amp;vkey=0A1AB4E5896242D0AAB9FEF36016A0F9008567D0FDB59103BEB70E5A8BD47D71391C8E4F78EE3A4C28EBD0ACC8E25DE1325DCA4190CA0193&amp;uin=0&amp;fromtag=66");
                        messageService.sendMessageToUser(chatGroup.getGroupId(),JSON.toJSONString(controlModel23), chatGroup.getUsers().get(0), enums.requestType.control);
                        break;
                    case 5:
                        controlModel = new ControlModel();
                        controlModel.setActionType(enums.ActionType.playAudio);
                        controlModel.setSrc("http://dl.stream.qqmusic.qq.com/C400003mAan70zUy5O.m4a?guid=9260526215&amp;vkey=9B7D9D598D494D33D896B065B7A013E1234811B2E4F1CB9611CEB074195B007A2580FF4590F5FD40B10588D3BF203BCF6785B4F51E8E7AAB&amp;uin=0&amp;fromtag=3&amp;r=9210949505559849");
                        messageService.sendMessageToUser(chatGroup.getGroupId(),JSON.toJSONString(controlModel), chatGroup.getUsers().get(0), enums.requestType.control);
                        break;
                    case 6:
                        controlModel = new ControlModel();
                        controlModel.setActionType(enums.ActionType.playAudio);
                        controlModel.setSrc("http://140.207.247.16/amobile.music.tc.qq.com/C400003mAan70zUy5O.m4a?guid=9260526215&amp;vkey=9B7D9D598D494D33D896B065B7A013E1234811B2E4F1CB9611CEB074195B007A2580FF4590F5FD40B10588D3BF203BCF6785B4F51E8E7AAB&amp;uin=0&amp;fromtag=3&amp;r=9094777812982173");
                        messageService.sendMessageToUser(chatGroup.getGroupId(),JSON.toJSONString(controlModel), chatGroup.getUsers().get(0), enums.requestType.control);
                        break;
                }
                PopToastUtil.ShowToast(mContext, stringList.get(position));
            }
        });

        recyclerView_menu.setAdapter(recycleViewAdapter);
        popupWindow = builder.setContentView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true).build();
        popupWindow.showAsDropDown(view);
    }

    private EditText et_message;
    private Button btn_send;
    private List<String> stringList;

    private RecyclerView recyclerView_chat;

    private void initView() {
        et_message = findViewById(R.id.et_message);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_message.getText())){
                    return;
                }
                messageService.sendMessage(chatGroup.getGroupId(),et_message.getText().toString(), chatGroup.getUsers().get(0));

                String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
                DBHelper.insertMessage(chatGroup.getGroupId(), id, et_message.getText().toString());
                et_message.setText("");
                refreshUI();
            }
        });

        recyclerView_chat = findViewById(R.id.recyclerView_chat);
        int spanCount = 1;
        //使用网格布局展示
        recyclerView_chat.setLayoutManager(new GridLayoutManager(mContext, spanCount));
        //设置分隔线
        //recyclerView.addItemDecoration(new GridDividerItemDecoration(mContext, spanCount));
        //设置增加或删除条目的动画
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        refreshUI();
    }

    List<ChatRecode> chatRecodeList;
    private void refreshUI() {
        //DBHelper.insert();
        chatRecodeList = DBHelper.getChatRecode();
        ChatAdapter recycleViewAdapter = new ChatAdapter(mContext, chatRecodeList);
        recycleViewAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRecode chatGroup = chatRecodeList.get(position);
                Bundle bundle = new Bundle();
                //bundle.putSerializable("ChatGroup",chatGroup);
                //startActivity(ChatActivity.class,bundle);
                //mBinder.sendMessge(et_message.getText().toString());
            }
        });
        recycleViewAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        recyclerView_chat.setAdapter(recycleViewAdapter);
        recyclerView_chat.scrollToPosition(chatRecodeList.size()-1);
    }
}
