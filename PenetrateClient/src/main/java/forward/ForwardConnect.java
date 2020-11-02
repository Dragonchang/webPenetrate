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
    private static String forwardServiceAddress = "10.16.33.73";

    /**
     * 转发服务端口
     */
    private static int forwardServicePort = 12345;

    /**
     * 和转发服务端的连接
     */
    public Socket forwardServiceClient;

    public ServiceConnect serviceConnect;
    /**
     * 连接到转发服务端
     */
    public void connectForwardService() {
        try {
            forwardServiceClient = new Socket(forwardServiceAddress, forwardServicePort);
            startReadWriteThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动对转发服务的连接的读写线程
     * 将请求读出写入到webService的connect
     * 同时将webService的connect写入到转发服务的连接上
     */
    public void startReadWriteThread() {
        ReadWriteRunnable runnable = new ReadWriteRunnable(this);
        String threadName = "read-write";
        Thread t = new Thread(runnable, threadName);
        t.start();
    }

}
