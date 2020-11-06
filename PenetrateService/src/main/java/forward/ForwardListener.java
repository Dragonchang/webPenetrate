package forward;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @program: webPenetrate
 * @description: 用来监听转发客户端的连接
 * @author: zhangfl
 * @create: 2020-11-02 11:14
 **/
public class ForwardListener {
    /**
     * 转发服务监听端口
     */
    private static int forwardListenPort = 12345;
    //private static String LocalHost = "104.36.67.68";
    private static String LocalHost = "104.36.67.68";

    /**
     * 转发服务监听socket
     */
    private ServerSocketChannel forwardListenSocket;

    private Selector selector;

    private ForwardConnectPoolManager poolManager;

    private ForwardAcceptor acceptor;

    public ForwardListener(ForwardConnectPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    /**
     * 启动转发服务的监听
     */
    public void startForwardListen() {
        try {
            selector = Selector.open();
            forwardListenSocket = ServerSocketChannel.open();
            InetSocketAddress isa = new InetSocketAddress(LocalHost, forwardListenPort);
            forwardListenSocket.socket().bind(isa);
            forwardListenSocket.configureBlocking(false);
            forwardListenSocket.register(selector, SelectionKey.OP_ACCEPT);
            startAcceptorThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动接收转发连接的socket的accept线程
     */
    protected void startAcceptorThread() {
        acceptor = new ForwardAcceptor(selector);
        String threadName = "forward-Acceptor";
        Thread t = new Thread(acceptor, threadName);
        t.start();
    }

    /**
     * 获取转发client端的连接
     * @return
     */
    public SocketChannel getForwardSocket() {
        return acceptor.getForwardConnect();
    }
}
