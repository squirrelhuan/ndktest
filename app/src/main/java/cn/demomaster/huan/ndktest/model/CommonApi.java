package cn.demomaster.huan.ndktest.model;

/**
 * 接口通用数据模型
 * Created by Squirrel桓
 */
public class CommonApi {
    private int status;
    private Object data;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
