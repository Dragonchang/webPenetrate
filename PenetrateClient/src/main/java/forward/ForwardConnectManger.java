package forward;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author ：dragonChang
 * @date ：Created in 2020/11/5 21:49
 * @description：转发连接池的管理
 * @modified By：
 * @version: 2.0
 */
public class ForwardConnectManger {

    /**
     * 连接池大小
     */
    private int ConnectPoolSize = 1;
    //TODO
    private SocketChannel forwardConnect;
    private Selector selector;
    private ForwardConnect connect;

    public ForwardConnectManger() {
        try {
            selector = Selector.open();
            connect = new ForwardConnect(selector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        initConnectPool();
        try {
            while (true) {
                System.out.println("开始accept转发连接上的请求");
                int count = selector.select();
                if(count > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化转发的连接池
     */
    private void initConnectPool() {
        forwardConnect = connect.connectForwardService();
    }
}
