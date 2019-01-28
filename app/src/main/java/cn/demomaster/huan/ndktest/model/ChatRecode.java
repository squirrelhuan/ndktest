package cn.demomaster.huan.ndktest.model;

/**
 * Created by Squirrel桓 on 2019/1/20.
 */
public class ChatRecode {


    /**
     * id : 34
     * senderId : 45
     * sendDateTime : 2019-01-22 21:53:09
     * reciverId : null
     */

    private String id;
    private String sendDateTime;
    private String reciverId;
    private String senderId;
    private String groupId;
    private String content;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendDateTime() {
        return sendDateTime;
    }

    public void setSendDateTime(String sendDateTime) {
        this.sendDateTime = sendDateTime;
    }

    public String getReciverId() {
        return reciverId;
    }

    public void setReciverId(String reciverId) {
        this.reciverId = reciverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
