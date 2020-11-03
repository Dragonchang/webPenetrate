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
public class ForwardReadWriteRunnable implements Runnable{

    ForwardConnect forwardConnect;

    public ForwardReadWriteRunnable(ForwardConnect forwardConnect) {
        this.forwardConnect = forwardConnect;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        //forward service 读取通道
        InputStream forwardIS = null;

        //web service 写入通道
        OutputStream webServiceOS = null;

        try {
            forwardIS = forwardConnect.forwardServiceClient.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                //获取web service的连接
                Socket serviceClientSocket = forwardConnect.serviceConnect.serviceClient;
                //和web service的连接通道没有打开,打开连接
                if(serviceClientSocket == null || serviceClientSocket.isClosed()) {
                    forwardConnect.serviceConnect.startServiceConnect();
                    Thread.sleep(500);
                    continue;
                }
                //将forward读取出来的数据写入到webservice的连接通道上
                webServiceOS = serviceClientSocket.getOutputStream();
                int size = 0;
                while((size = forwardIS.read(buffer)) != -1) {
                    System.out.println("size: "+size);
                    String res = new String(buffer,"UTF-8");
                    System.out.println(res);
                    if (size > -1 && !(serviceClientSocket == null || serviceClientSocket.isClosed())) {
                        webServiceOS.write(buffer, 0, size);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
