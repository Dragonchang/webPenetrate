import forward.ForwardConnect;
import forward.ForwardConnectManger;
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

        ForwardConnectManger manger = new ForwardConnectManger();
        manger.start();
    }

}
