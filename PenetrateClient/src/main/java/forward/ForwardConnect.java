package forward;

import webService.ServiceConnect;

import java.net.Socket;

/**
 * @program: webPenetrate
 * @description: 和转发服务的连接
 * @author: zhangfl
 * @create: 2020-11-02 13:29
 **/
public class ForwardConnect {
    /**
     * 转发服务地址
     */
    //private static String forwardServiceAddress = "10.16.33.73";
    private static String forwardServiceAddress = "localhost";
    /**
     * 转发服务端口
     */
    private static int forwardServicePort = 12345;

    /**
     * 和转发服务端的连接
     */
    public Socket forwardServiceClient;

    /**
     * 和 webservice 的连接
     */
    public ServiceConnect serviceConnect;
    /**
     * 连接到转发服务端
     */
    public void connectForwardService() {
        try {
            forwardServiceClient = new Socket(forwardServiceAddress, forwardServicePort);
            startForwardReadWriteThread();
            startWebReadWriteThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动对转发服务的连接的读写线程
     *
     */
    public void startForwardReadWriteThread() {
        ForwardReadWriteRunnable runnable = new ForwardReadWriteRunnable(this);
        String threadName = "forward-read-write";
        Thread t = new Thread(runnable, threadName);
        t.start();
    }

    /**
     * 启动对web service连接的读写线程
     */
    public void startWebReadWriteThread() {
        WebReadWriteRunnable runnable = new WebReadWriteRunnable(this);
        String threadName = "web-read-write";
        Thread t = new Thread(runnable, threadName);
        t.start();
    }
}
