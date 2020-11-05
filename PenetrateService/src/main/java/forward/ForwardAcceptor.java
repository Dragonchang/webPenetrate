package forward;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @program: webPenetrate
 * @description: 用来accept连接服务的socket
 * @author: zhangfl
 * @create: 2020-11-02 13:49
 **/
public class ForwardAcceptor implements Runnable{

    Selector selector;

    /**
     * 转发client 端连接的socket
     */
    private SocketChannel penetrateClientSocket = null;

    public ForwardAcceptor(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("开始accept转发连接的到来");
                int count = selector.select();
                if(count > 0) {
                    System.out.println("有服务转发连接的请求到来");
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();

                        //若此key的通道是等待接受新的套接字连接
                        if (key.isAcceptable()) {
                            //一定要把这个accpet状态的服务器key去掉，否则会出错
                            iterator.remove();
                            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                            //接受socket
                            penetrateClientSocket = serverChannel.accept();
                            System.out.println(penetrateClientSocket.toString()+" 转发连接连接成功");
                            penetrateClientSocket.configureBlocking(false);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public SocketChannel getForwardConnect() {
        return penetrateClientSocket;
    }
}
