package forward;

import webService.ServiceConnect;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

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

    InetSocketAddress isa = new InetSocketAddress(forwardServiceAddress, forwardServicePort);

    Selector selector;

    public ForwardConnect(Selector selector) {
        this.selector = selector;
    }
    /**
     * 连接到转发服务端
     */
    public SocketChannel connectForwardService() {
        try {
            SocketChannel sc = SocketChannel.open(isa);
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            return sc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
