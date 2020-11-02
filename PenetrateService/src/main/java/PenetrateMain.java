import forward.ForwardListener;
import reply.RequestResponseListener;

import java.io.IOException;

/**
 * @program: webPenetrate
 * @description: 穿透服务的主方法
 * @author: zhangfl
 * @create: 2020-11-02 14:01
 **/
public class PenetrateMain {
    public static void main(String[] args) throws IOException {
        ForwardListener forwardListener = new ForwardListener();
        forwardListener.startForwardListen();
        RequestResponseListener requestResponseListener = new RequestResponseListener();
        requestResponseListener.startListen();
        requestResponseListener.setForwardListen(forwardListener);
        System.out.println("启动成功");
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {

            }
        }
    }
}
