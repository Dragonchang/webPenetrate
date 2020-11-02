package forward;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @program: webPenetrate
 * @description: 处理转发服务连接上的请求和写入响应
 * @author: zhangfl
 * @create: 2020-11-02 16:24
 **/
public class ReadWriteRunnable implements Runnable{

    ForwardConnect forwardConnect;

    public ReadWriteRunnable(ForwardConnect forwardConnect) {
        this.forwardConnect = forwardConnect;
    }

    public void run() {
        byte[] b = new byte[1024];
        InputStream is = null;
        OutputStream os = null;
        try {
            is = forwardConnect.forwardServiceClient.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {

            try {
                Socket writeSocket = forwardConnect.serviceConnect.serviceClient;
                os = writeSocket.getOutputStream();
                int size = 0;
                while(size > -1){
                    size = is.read(b);
                    System.out.println("size: "+size);
                    String res = new String(b,"UTF-8");
                    System.out.println(res);
                    if (size > -1 && !(writeSocket == null || writeSocket.isClosed())) {
                        os.write(b, 0, size);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
