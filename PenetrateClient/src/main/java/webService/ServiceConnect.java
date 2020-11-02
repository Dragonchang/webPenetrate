package webService;

import java.io.IOException;
import java.net.Socket;

/**
 * @program: webPenetrate
 * @description: 管理和webservice的连接
 * @author: zhangfl
 * @create: 2020-11-02 11:40
 **/
public class ServiceConnect {
    /**
     * web 服务地址
     */
    private static String webServiceAddress = "localhost";

    /**
     * web服务端口
     */
    private static int webServicePort = 8201;

    /**
     * web 服务端的连接
     */
    public Socket serviceClient;

    public void startServiceConnect() {
        try {
            serviceClient = new Socket(webServiceAddress, webServicePort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        ServiceConnect connect = new ServiceConnect();
        connect.serviceClient = new Socket(webServiceAddress, webServicePort);
        connect.serviceClient.setSoTimeout(10000);
    }
}
