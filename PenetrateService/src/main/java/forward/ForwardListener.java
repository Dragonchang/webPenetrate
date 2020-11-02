package forward;

import java.net.ServerSocket;
import java.net.Socket;

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

    /**
     * 转发服务监听socket
     */
    private ServerSocket forwardListenSocket;



    /**
     * 服务启动是否成功
     */
    private boolean isSuccess;

    private ForwardAcceptor acceptor;

    /**
     * 启动转发服务的监听
     */
    public void startForwardListen() {
        isSuccess = false;
        try {
            forwardListenSocket = new ServerSocket(forwardListenPort);
            startAcceptorThread();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        isSuccess = true;
    }

    /**
     * 启动接收转发连接的socket的accept线程
     */
    protected void startAcceptorThread() {
        acceptor = new ForwardAcceptor(forwardListenSocket);
        String threadName = "forward-Acceptor";
        Thread t = new Thread(acceptor, threadName);
        t.start();
    }

    /**
     * 获取转发client端的连接
     * @return
     */
    public Socket getForwardSocket() {
        return acceptor.getForwardConnect();
    }
}
