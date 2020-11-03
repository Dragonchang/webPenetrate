package reply;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: webPenetrate
 * @description: 用来accept连接服务的socket
 * @author: zhangfl
 * @create: 2020-11-02 13:49
 **/
public class RequestResponseAcceptor implements Runnable{

    ServerSocket requestListenSocket;

    /**
     * 转发client 端连接的socket
     * TODO启动poller线程池进行读写的处理
     */
    public Socket requestClientSocket = null;

    RequestResponseListener listener;

    public RequestResponseAcceptor(RequestResponseListener listener, ServerSocket requestListenSocket) {
        this.listener = listener;
        this.requestListenSocket = requestListenSocket;
    }

    public void run() {
        while (true) {
            try {
                requestClientSocket = requestListenSocket.accept();
                System.out.println("请求到来");
                dealWithNewRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理新的请求
     */
    private void dealWithNewRequest() {
        Socket forwardSocket = listener.getForwardListen().getForwardSocket();
        byte[] b = new byte[1024];
        InputStream is = null;
        OutputStream os = null;
        try {
            is = requestClientSocket.getInputStream();
            if(forwardSocket != null && !forwardSocket.isClosed()) {
                os = forwardSocket.getOutputStream();
            }
            int size = 0;
            while((size = is.read(b)) != -1){
                System.out.println("size: "+size);
                String res = new String(b,"UTF-8");
                System.out.println(res);
                if (size > -1 && os!= null && !(forwardSocket == null || forwardSocket.isClosed())) {
                    os.write(b, 0, size);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
