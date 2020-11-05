import execute.RequestExecuteManger;

import java.io.IOException;

/**
 * @program: webPenetrate
 * @description: 穿透服务的主方法
 * @author: zhangfl
 * @create: 2020-11-02 14:01
 **/
public class PenetrateMain {
    public static void main(String[] args) throws IOException {
        RequestExecuteManger manager = new RequestExecuteManger();
        manager.startService();
    }
}
