package reply;

import forward.ForwardAcceptor;
import forward.ForwardListener;

import java.net.ServerSocket;

/**
 * @program: webPenetrate
 * @description: 用来监听请求端的请求连接
 * @author: zhangfl
 * @create: 2020-11-02 10:56
 **/
public class RequestResponseListener {
    /**
     * 处理请求和响应服务的监听端口
     */
    private static int requestListenPort = 8888;

    /**
     * 处理请求和响应服务监听socket
     */
    private ServerSocket requestListenSocket;

    /**
     * 服务启动是否成功
     */
    private boolean isSuccess;

    /**
     * 请求响应acceptor
     */
    private RequestResponseAcceptor acceptor;

    /**
     * 转发连接的管理服务
     */
    private ForwardListener listener;

    /**
     * 启动请求和响应服务的监听
     */
    public void startListen() {
        isSuccess = false;
        try {
            requestListenSocket = new ServerSocket(requestListenPort);
            startAcceptorThread();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 启动接收转发连接的socket的accept线程
     */
    protected void startAcceptorThread() {
        acceptor = new RequestResponseAcceptor(this, requestListenSocket);
        String threadName = "request-Acceptor";
        Thread t = new Thread(acceptor, threadName);
        t.start();
    }

    public void setForwardListen(ForwardListener forwardListener) {
        this.listener = forwardListener;
    }

    public ForwardListener getForwardListen() {
        return listener;
    }
}
