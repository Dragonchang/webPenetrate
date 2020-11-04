package execute;

import java.net.Socket;

/**
 * @author ：dragonChang
 * @date ：Created in 2020/11/4 22:18
 * @description：请求执行器
 * @modified By：
 * @version: 1.0$
 */
public class RequestRunnable implements Runnable{

    /**
     * 请求执行器
     */
    private RequestExecuteManger executeManger;

    /**
     * 接收请求的socket
     */
    private Socket requestClientSocket;

    public RequestRunnable(RequestExecuteManger executeManger, Socket socket) {
        this.executeManger = executeManger;
        this.requestClientSocket = socket;
    }

    public void run() {

    }
}
