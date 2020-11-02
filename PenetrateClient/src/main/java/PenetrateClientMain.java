import forward.ForwardConnect;
import webService.ServiceConnect;

import java.io.IOException;

/**
 * @program: webPenetrate
 * @description: 转发客户端主函数
 * @author: zhangfl
 * @create: 2020-11-02 15:04
 **/
public class PenetrateClientMain {

    public static void main(String[] args) throws IOException {
        ServiceConnect serviceConnect = new ServiceConnect();
        serviceConnect.startServiceConnect();

        ForwardConnect forwardConnect = new ForwardConnect();
        forwardConnect.connectForwardService();
        forwardConnect.serviceConnect = serviceConnect;
        System.out.println("启动成功");
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {

            }
        }
    }

}
