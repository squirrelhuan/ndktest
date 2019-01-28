package cn.demomaster.huan.ndktest.service;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import cn.demomaster.huan.ndktest.model.Message;
import cn.demomaster.huan.ndktest.model.UdpData;
import cn.demomaster.huan.ndktest.model.enums;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;

public class PostMan {

    public int server_port = 8001;
    public String server_ip = "localhost";
    private static DatagramSocket receiveSocket;
    private static SocketAddress server;
    private static DatagramPacket dp;
    private byte[] buf;

    public PostMan(String server_ip, int server_port) throws IOException {
        this.server_ip = server_ip;
        this.server_port = server_port;

        //创建数据包
        buf = new byte[2048];
        dp = new DatagramPacket(buf, buf.length);

        server = new InetSocketAddress(server_ip, server_port);
        //receiveSocket = new DatagramSocket(client_port);
        receiveSocket = new DatagramSocket();
        System.out.println("postman创建成功");
        receiver.start();
    }

    public static interface OnReceiveMessageListener {
        void onReceive(UdpData udpData);
    }

    private OnReceiveMessageListener onReceiveMessageListener;

    public void setOnReceiveMessageListener(OnReceiveMessageListener onReceiveMessageListener) {
        this.onReceiveMessageListener = onReceiveMessageListener;
    }

    public static class SendThread extends Thread {
        private String message;
        public SendThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            super.run();
            try {
                //DatagramSocket socket = new DatagramSocket();
                String text = message;
                byte[] buf = text.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, server);
                //dp.setData(buf,0,buf.length);
                receiveSocket.send(packet);
                //socket.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }


    public Thread receiver = new Thread() {

        public void run() {
            try {
                while (true) {
                    dp.setData(buf, 0, buf.length);
                    //使用接收方法将数据存储到数据包中
                    receiveSocket.receive(dp);
                    String txt = new String(dp.getData(), 0, dp.getLength());
                    UdpData udpData = null;
                    try {
                        udpData = JSONObject.parseObject(txt, UdpData.class);
                        if (onReceiveMessageListener != null) {
                            onReceiveMessageListener.onReceive(udpData);
                        }
                        //收到数据后要给服务端反馈，告知数据已接受
                        sendReceiveToServer(udpData);
                    } catch (Exception e) {
                        Log.e("CGQ", "error:" + e.getMessage());
                        //e.printStackTrace();
                    }
                    Log.i("CGQ", "来自服务端:" + txt);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void sendMessage(String message) {
        SendThread sendThread = new SendThread(message);
        sendThread.start();
    }

    /**
     * 接收到数据反馈给服务器
     * @param oldUdp
     */
    public void sendReceiveToServer(UdpData oldUdp){
        UdpData udpData = new UdpData();
        udpData.setRequestType(enums.requestType.receive);
        Message message = new Message();
        message.setId(oldUdp.getId());
        String nickname = SharedPreferencesHelper.getInstance().getString("nickname","");
        String id = SharedPreferencesHelper.getInstance().getString("UserId","");
        message.setSendUserId(id);
        udpData.setMessage(message);

        String sendStr = JSON.toJSONString(udpData);
        sendMessage(sendStr);
    }

}
