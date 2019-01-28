package cn.demomaster.huan.ndktest.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import cn.demomaster.huan.ndktest.App;
import cn.demomaster.huan.ndktest.model.ChatGroup;
import cn.demomaster.huan.ndktest.model.ChatRecode;
import cn.demomaster.huan.ndktest.model.UdpData;
import cn.demomaster.huan.ndktest.model.User;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;

import static cn.demomaster.huan.quickdeveloplibrary.base.BaseActivityRoot.TAG;

/**
 * Created by Squirrel桓 on 2019/1/20.
 */
public class DBHelper {

    public static List<ChatGroup> getChatGroup() {
        List<ChatGroup> groups = new ArrayList<>();
        //CBHelper dbHelper = new CBHelper(mContext,"yidao",null,1);
        //得到一个可写的数据库
        //db =dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式chat_record
        Cursor cursor = App.getInstance().db.query("chat_group", new String[]{"name", "groupId", "headerUrl", "state", "isPrivate","ownerId"}, null, new String[]{}, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String groupId = cursor.getString(cursor.getColumnIndex("groupId"));
            String headerUrl = cursor.getString(cursor.getColumnIndex("headerUrl"));
            int isPrivate = cursor.getInt(cursor.getColumnIndex("isPrivate"));
            int ownerId = cursor.getInt(cursor.getColumnIndex("ownerId"));
            int state = cursor.getInt(cursor.getColumnIndex("state"));
            Log.i(TAG, "query------->" + "name：" + name + " " + ",groupId：" + groupId);
            ChatGroup chatGroup = new ChatGroup();
            chatGroup.setName(name);
            chatGroup.setHeaderUrl(headerUrl);
            chatGroup.setState(state);
            chatGroup.setIsPrivate(isPrivate);
            chatGroup.setOwnerId(ownerId);
            chatGroup.setGroupId(groupId);
            groups.add(chatGroup);
        }
        Log.i(TAG, "query------->" + "close");
        //关闭数据库
        //db.close();
        return groups;
    }

    public static List<User> getChatGroupUserList(String groupId) {
        List<User> users = new ArrayList<>();
        //CBHelper dbHelper = new CBHelper(mContext,"yidao",null,1);
        //得到一个可写的数据库
        //db =dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式chat_record
        Cursor cursor = App.getInstance().db.query("user", new String[]{"nickName", "id", "headerUrl", "isPrivate"}, "id=?", new String[]{groupId+""}, null, null, null);
        while (cursor.moveToNext()) {
            String nickName = cursor.getString(cursor.getColumnIndex("nickName"));
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String headerUrl = cursor.getString(cursor.getColumnIndex("headerUrl"));
            int isPrivate = cursor.getInt(cursor.getColumnIndex("isPrivate"));
            Log.i(TAG, "query------->" + " " + ",groupId：" + groupId);
            User user = new User();
            user.setNickname(nickName);
            user.setFace(headerUrl);
            user.setId(id);
            users.add(user);
        }
        Log.i(TAG, "query------->" + "close");
        //关闭数据库
        //db.close();
        return users;
    }

    public static User getUserById(String id){
        Cursor cursor = App.getInstance().db.query("user", new String[]{"nickName", "id", "headerUrl", "isPrivate"}, "id=?", new String[]{id+""}, null, null, null);
        User user =null;
        while (cursor.moveToNext()) {
            String nickName = cursor.getString(cursor.getColumnIndex("nickName"));
            String headerUrl = cursor.getString(cursor.getColumnIndex("headerUrl"));
            int isPrivate = cursor.getInt(cursor.getColumnIndex("isPrivate"));
            Log.i(TAG, "query------->" + " " + ",groupId：" );
            user = new User();
            user.setNickname(nickName);
            user.setFace(headerUrl);
            user.setId(id);
        }
        Log.i(TAG, "query------->" + "close");
        //关闭数据库
        //db.close();
        return user;
    }

    public static void insert() {
        //生成ContentValues对象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        cv.put("id", 2);
        cv.put("groupId", 3);
        //调用insert方法，将数据插入数据库
        App.getInstance().db.insert("chat_group", null, cv);
        //关闭数据库
        //db.close();
        Log.i(TAG, "insert------->" + "id：" + 2 + " " + ",groupId：" + 3);
    }

    /**
     * 更新会话列表
     * @param chatGroupList
     */
    public static void updateChatGroup(List<ChatGroup> chatGroupList) {
        for (int i = 0; i < chatGroupList.size(); i++) {
            //生成ContentValues对象 //key:列名，value:想插入的值
            ContentValues cv = new ContentValues();
            //往ContentValues对象存放数据，键-值对模式
            cv.put("name", chatGroupList.get(i).getName());
            cv.put("groupId", chatGroupList.get(i).getGroupId());
            cv.put("headerUrl", chatGroupList.get(i).getHeaderUrl());
            cv.put("isPrivate", chatGroupList.get(i).getIsPrivate());
            cv.put("ownerId", chatGroupList.get(i).getOwnerId());
            Cursor cursor = App.getInstance().db.query("chat_group", new String[]{"groupId,id"}, "groupId=?", new String[]{chatGroupList.get(i).getGroupId() + ""}, null, null, null);

            if (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String whereClause = "id=?";
                String[] whereArgs = {id};
                App.getInstance().db.update("chat_group", cv, whereClause, whereArgs);
            }else {
                //调用insert方法，将数据插入数据库
                App.getInstance().db.insert("chat_group", null, cv);
            }
            updateUser(chatGroupList.get(i).getUsers());
        }
    }

    /**
     * 更新会话列表
     * @param chatRecordList
     */
    public static void updateChatRecord(List<ChatRecode> chatRecordList) {
        for (int i = 0; i < chatRecordList.size(); i++) {
            //生成ContentValues对象 //key:列名，value:想插入的值
            ContentValues cv = new ContentValues();
            //往ContentValues对象存放数据，键-值对模式
            cv.put("id", chatRecordList.get(i).getId());
            cv.put("senderId", chatRecordList.get(i).getSenderId());
            cv.put("groupId", chatRecordList.get(i).getGroupId());
            cv.put("reciverId", chatRecordList.get(i).getReciverId());
            cv.put("content", chatRecordList.get(i).getContent());
            cv.put("sendDateTime", chatRecordList.get(i).getSendDateTime());
            Cursor cursor = App.getInstance().db.query("chat_record", new String[]{"id"}, "id=?", new String[]{chatRecordList.get(i).getId() + ""}, null, null, null);

            if (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String whereClause = "id=?";
                String[] whereArgs = {id};
                App.getInstance().db.update("chat_record", cv, whereClause, whereArgs);
            }else {
                //调用insert方法，将数据插入数据库
                App.getInstance().db.insert("chat_record", null, cv);
            }
        }
    }

    /**
     * 更新会话列表
     * @param users
     */
    public static void updateUser(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            //生成ContentValues对象 //key:列名，value:想插入的值
            ContentValues cv = new ContentValues();
            //往ContentValues对象存放数据，键-值对模式
            cv.put("nickName", users.get(i).getNickname());
            cv.put("headerUrl", users.get(i).getFace());
            cv.put("id", users.get(i).getId());
            Cursor cursor = App.getInstance().db.query("user", new String[]{"nickName,headerUrl,id"}, "id=?", new String[]{users.get(i).getId() + ""}, null, null, null);

            if (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String whereClause = "id=?";
                String[] whereArgs = {id};
                App.getInstance().db.update("user", cv, whereClause, whereArgs);
            }else {
                //调用insert方法，将数据插入数据库
                App.getInstance().db.insert("user", null, cv);
            }
        }
    }


    /**
     * 添加消息到本地数据库
     * @param udpData
     * @param udpData
     */
    public static void insertMessage(UdpData udpData) {

        String id = SharedPreferencesHelper.getInstance().getString("UserId","");
        if(!udpData.getMessage().getSendUserId().equals(id)) {
            //生成ContentValues对象 //key:列名，value:想插入的值
            ContentValues cv = new ContentValues();
            //往ContentValues对象存放数据，键-值对模式
            cv.put("content", udpData.getMessage().getContent());
            cv.put("senderId", udpData.getMessage().getSendUserId());
            cv.put("groupId", udpData.getId());
            //调用insert方法，将数据插入数据库
            App.getInstance().db.insert("chat_record", null, cv);
        }
    }
    /**
     * 添加消息到本地数据库
     */
    public static void insertMessage(String groupId, String senderId,String content) {
        //生成ContentValues对象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        cv.put("content", content);
        cv.put("senderId", senderId);
        cv.put("groupId", groupId);
        //调用insert方法，将数据插入数据库
        App.getInstance().db.insert("chat_record", null, cv);

    }

    public static List<ChatRecode> getChatRecode(){
        List<ChatRecode> recodeList = new ArrayList<>();
        //CBHelper dbHelper = new CBHelper(mContext,"yidao",null,1);
        //得到一个可写的数据库
        //db =dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式chat_record
        Cursor cursor = App.getInstance().db.query("chat_record", new String[]{"content", "groupId", "senderId"}, null, new String[]{}, null, null, null);
        while (cursor.moveToNext()) {
            String senderId = cursor.getString(cursor.getColumnIndex("senderId"));
            String groupId = cursor.getString(cursor.getColumnIndex("groupId"));
            String content = cursor.getString(cursor.getColumnIndex("content"));

            Log.i(TAG, "query------->" + "senderId：" + senderId + " " + ",groupId：" + groupId);
            ChatRecode chatRecode = new ChatRecode();
            chatRecode.setContent(content);
            chatRecode.setGroupId(groupId);
            chatRecode.setSenderId(senderId);
            recodeList.add(chatRecode);
        }
        Log.i(TAG, "query------->" + "close");
        //关闭数据库
        //db.close();
        return recodeList;
    }


}
