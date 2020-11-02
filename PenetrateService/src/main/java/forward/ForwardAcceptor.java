package forward;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: webPenetrate
 * @description: 用来accept连接服务的socket
 * @author: zhangfl
 * @create: 2020-11-02 13:49
 **/
public class ForwardAcceptor implements Runnable{

    ServerSocket forwardListenSocket;

    /**
     * 转发client 端连接的socket
     */
    private Socket penetrateClientSocket = null;

    public ForwardAcceptor(ServerSocket forwardListenSocket) {
        this.forwardListenSocket = forwardListenSocket;
    }

    public void run() {
        while (true) {
            try {
                penetrateClientSocket = forwardListenSocket.accept();
                System.out.println("有服务转发连接的请求到来");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getForwardConnect() {
        return penetrateClientSocket;
    }
}
