package forward;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @program: webPenetrate
 * @description: 和转发服务的连接
 * @author: zhangfl
 * @create: 2020-11-02 13:29
 **/
public class ForwardConnect {
    /**
     * 转发服务地址
     */
    //private static String forwardServiceAddress = "10.16.33.73";
    private static String forwardServiceAddress = "localhost";
    /**
     * 转发服务端口
     */
    private static int forwardServicePort = 12345;

    Selector selector;

    public ForwardConnect(Selector selector) {
        this.selector = selector;
    }
    /**
     * 连接到转发服务端
     */
    public SocketChannel connectForwardService() throws Exception {
        InetSocketAddress isa = new InetSocketAddress(forwardServiceAddress, forwardServicePort);
        SocketChannel sc = SocketChannel.open(isa);
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        return sc;
    }
}
