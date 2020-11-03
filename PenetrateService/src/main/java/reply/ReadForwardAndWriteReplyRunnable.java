package reply;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @program: webPenetrate
 * @description: 读取forward连接上的数据，写入到请求连接上
 * @author: zhangfl
 * @create: 2020-11-03 09:05
 **/
public class ReadForwardAndWriteReplyRunnable implements Runnable{

    RequestResponseListener listener;
    public ReadForwardAndWriteReplyRunnable(RequestResponseListener listener) {
        this.listener = listener;
    }

    public void run() {
        byte[] b = new byte[1024];
        InputStream is = null;
        OutputStream os = null;
        Socket request = null;
        while (true) {
            try {
                Socket forwardSocket = listener.getForwardListen().getForwardSocket();
                if(forwardSocket == null || forwardSocket.isClosed()) {
                    System.out.println("等待forward请求到来......");
                    Thread.sleep(500);
                    continue;
                }
                System.out.println("forward请求连接获取成功");
                is = forwardSocket.getInputStream();
                int size = 0;
                while((size = is.read(b)) != -1){
                    System.out.println("size: "+size);
                    String res = new String(b,"UTF-8");
                    System.out.println(res);
                    request = listener.acceptor.requestClientSocket;
                    if(request != null || !request.isClosed()) {
                        os = request.getOutputStream();
                    }
                    if (size > -1 && os!= null && !(request == null || request.isClosed())) {
                        os.write(b, 0, size);
                    }
                }
            } catch (Exception e) {

            } finally {
                try {
                    if (request != null ) {
                        request.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
