package com.zw.yanshinavi.http;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.zw.yanshinavi.common.App;
import com.zw.yanshinavi.common.StaticHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 接收UDP点位地址
 *
 * @author zhangwei
 * @since 2019-2-25
 */
public class UdpSocket {

    private static final String TAG = "UdpSocket";

    private static final String LOCAL_HOST = "127.0.0.1"; // 本地IP
    private static final int RECEIVE_PORT = 8034;  // 点位接收端口
    private static final int SEND_PORT = 1209; // 外部软件接收端口

    private Context mContext;
    private ExecutorService mThreadPool;
    private DatagramSocket mSendSocket;
    private DatagramSocket mReceiveSocket;
    private UdpSocketHandler mHandler;

    private DatagramPacket mReceivePacket;

    private byte[] receiveBytes = new byte[1024];
    private boolean isReceive = true;
    private static final int SEND_MESSAGE = 123; // Handler处理去发送心跳包
    private static final int DELAY_TIME = 10 * 1000; // 延迟10秒发送心跳包
    private static final int HEART_BEAT_TIME_OUT = 60 * 1000; // 超时的时间
    private Thread mReceiveThread;
    private long mSendHeartBeatTime; //发送心跳包的时间

    private UdpSocket(){
        mContext = App.getAppContext();
        int cpuNums = Runtime.getRuntime().availableProcessors();
        mThreadPool = Executors.newFixedThreadPool(cpuNums);
        mHandler = new UdpSocketHandler(mContext);
    }

    /**
     * 开启接收和发送
     */
    public void startUdpSocket(){
        if(mSendSocket != null && !mSendSocket.isClosed()) return;

        Log.e("zhangwei",TAG + " startUdpSocket");
        isReceive = true;
        try {
            mReceiveSocket = new DatagramSocket(RECEIVE_PORT);
            mSendSocket = new DatagramSocket(SEND_PORT);
            if(mReceivePacket == null) {
                mReceivePacket = new DatagramPacket(receiveBytes,receiveBytes.length);
            }
            startReceiveThread();
        } catch (SocketException e) {
            Log.e("zhangwei",TAG + "startUdpSocket is error :" + e.getMessage());
        }
    }

    /**
     * 停止发送和接收
     */
    public void stopUdpSocket(){
        isReceive = false;
        mReceiveThread.interrupt();
        mHandler.removeCallbacksAndMessages(null);
        mThreadPool.shutdownNow();
        mSendSocket.close();
        mSendSocket = null;
        mReceiveSocket.close();
        mReceiveSocket = null;
        Log.e("zw",TAG + " socket is close.");
    }

    /**
     * 开启接收点位消息的线程
     */
    private void startReceiveThread() {
        mReceiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiveMessage();
            }
        });
        mReceiveThread.start();

        // 发送心跳包
        mHandler.sendEmptyMessageDelayed(SEND_MESSAGE,DELAY_TIME);
    }

    private void receiveMessage() {
        while (isReceive) {
            if(mReceiveSocket != null){
                try {
                    mReceiveSocket.receive(mReceivePacket);

                    if(mReceivePacket == null) continue;

                    mSendHeartBeatTime = System.currentTimeMillis();
                    String message = new String(mReceivePacket.getData(),mReceivePacket.getData().length).trim();
                    if(!TextUtils.isEmpty(message)) {
                        //TODO 接收点位消息后的处理
                        Log.e("zhangwei","receive Msg : " + message);

                    }

                } catch (IOException e) {
                    Log.e("zhangwei","receiveMessage error : " + e.getMessage());
                }
            }
        }
    }

    /**
     * 定期10秒发送心跳包信息
     */
    private void sendUdpMessage(){
        long currentTime = System.currentTimeMillis();
        if(currentTime - mSendHeartBeatTime > HEART_BEAT_TIME_OUT){
            // 超时处理
            try {
                mReceiveSocket = new DatagramSocket(RECEIVE_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        String message = "send message " + System.currentTimeMillis();
        mSendHeartBeatTime = System.currentTimeMillis();
        mThreadPool.execute(new SendUdpRunnable(message,mSendSocket));

        // 定期10秒发送消息
        mHandler.sendEmptyMessageDelayed(SEND_MESSAGE, DELAY_TIME);
    }

    public static UdpSocket getInstance(){
        return Holder.INSTANCE;
    }

    private static class Holder{
        public static final UdpSocket INSTANCE = new UdpSocket();
    }

    private static class SendUdpRunnable implements Runnable{

        private byte[] msg;
        private DatagramSocket sendSocket;

        public SendUdpRunnable(String message,DatagramSocket sendSocket) {
            this.msg = message.getBytes();
            this.sendSocket = sendSocket;
        }

        @Override
        public void run() {
            DatagramPacket sendPacket =
                    new DatagramPacket(msg,msg.length,new InetSocketAddress(LOCAL_HOST,RECEIVE_PORT));
            try {
                sendSocket.send(sendPacket);
                Log.e("zhangwei",TAG + "socket is send");
            } catch (IOException e) {
                Log.e("zhangwei",TAG + "send socket error : " + e.getMessage());
            }
        }
    }

    private class UdpSocketHandler extends StaticHandler<Context> {

        public UdpSocketHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == SEND_MESSAGE) {
                sendUdpMessage();
            }
        }
    }
}
