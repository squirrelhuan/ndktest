package cn.demomaster.huan.ndktest.model;

public class enums {
    public static enum requestType {
        receive(0),login(1), logoff(2), getData(3), sendData(4), heart(5),control(6);
        private int value;
        requestType(int value) {
            this.value = value;
        }
    }
    public static enum requestType2 {
        login(1), logoff(2), getData(3), sendData(4), heart(5);
        private int value;

        requestType2(int value) {
            this.value = value;
        }
    }

    public static enum ActionType{
        playAudio,stopAudio,showImage,hidImage,vibrator//震动
    }
}