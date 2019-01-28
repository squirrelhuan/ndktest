package cn.demomaster.huan.ndktest.model.control;


import java.io.Serializable;

import cn.demomaster.huan.ndktest.model.enums;

/**
 * Created by Squirrel桓 on 2019/1/19.
 */
public class ControlModel implements Serializable {

    private String src;//源文件路径
    private enums.ActionType actionType;//动作类型

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public enums.ActionType getActionType() {
        return actionType;
    }

    public void setActionType(enums.ActionType actionType) {
        this.actionType = actionType;
    }
}
