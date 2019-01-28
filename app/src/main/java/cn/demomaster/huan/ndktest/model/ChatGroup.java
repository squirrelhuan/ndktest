package cn.demomaster.huan.ndktest.model;


import java.io.Serializable;
import java.util.List;

import cn.demomaster.huan.ndktest.helper.DBHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;

/**
 * @author squirrel桓
 * @date 2019/1/18.
 * description：
 */
public class ChatGroup implements Serializable {

    private String name;
    private List<User> users;
    private String groupId;
    private String headerUrl;
    private String description;
    private int ownerId;
    private int state;
    private int isPrivate;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHeaderUrl() {
        if (isPrivate == 1) {
            String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
            if (getUsers() == null) {
                return "";
            }
            for (User user : getUsers()) {
                if (!user.getId().equals(id)) {
                    headerUrl = user.getFace();
                    return headerUrl;
                }
            }
        }
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        if (isPrivate == 1) {
            String id = SharedPreferencesHelper.getInstance().getString("UserId", "");
            for (User user : getUsers()) {
                if (!user.getId().equals(id.trim())) {
                    name = user.getNickname();
                }
            }
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        if (users == null) {
            users = DBHelper.getChatGroupUserList(groupId);
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
