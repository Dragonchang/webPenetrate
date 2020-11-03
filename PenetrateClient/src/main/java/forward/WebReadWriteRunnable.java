package forward;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @program: webPenetrate
 * @description: 读取web服务上的响应，写入到转发连接上
 * @author: zhangfl
 * @create: 2020-11-03 08:35
 **/
public class WebReadWriteRunnable  implements Runnable{

    ForwardConnect forwardConnect;

    public WebReadWriteRunnable(ForwardConnect forwardConnect) {
        this.forwardConnect = forwardConnect;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        //web service 读取通道
        InputStream webServiceIS = null;
        //forward service 写入通道
        OutputStream forwardOS = null;
        try {
            forwardOS = forwardConnect.forwardServiceClient.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                //获取web service的连接
                Socket serviceClientSocket = forwardConnect.serviceConnect.serviceClient;
                //和web service的连接通道没有打开,打开连接
                if (serviceClientSocket == null || serviceClientSocket.isClosed()) {
                    forwardConnect.serviceConnect.startServiceConnect();
                    Thread.sleep(500);
                    continue;
                }
                webServiceIS = serviceClientSocket.getInputStream();
                int size = 0;
                while((size = webServiceIS.read(buffer)) != -1) {
                    System.out.println("从web连接上读取数据：size: "+size);
                    String res = new String(buffer,"UTF-8");
                    System.out.println(res);
                    if (size > -1 && !(forwardConnect.forwardServiceClient == null || forwardConnect.forwardServiceClient.isClosed())) {
                        forwardOS.write(buffer, 0, size);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
