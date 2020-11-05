package reply;

import execute.RequestExecuteManger;
import forward.ForwardListener;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @program: webPenetrate
 * @description: 用来监听请求端的请求连接
 * @author: zhangfl
 * @create: 2020-11-02 10:56
 **/
public class RequestResponseListener {
    /**
     * 处理请求和响应服务的监听端口
     */
    private static int requestListenPort = 8888;

    /**
     * 处理请求和响应服务监听socket
     */
    private ServerSocketChannel serverSocket;

    /**
     * 服务管理对象
     */
    private RequestExecuteManger manager;

    private Selector selector;

    public RequestResponseListener(RequestExecuteManger manager) {
        this.manager = manager;
        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            InetSocketAddress isa = new InetSocketAddress("127.0.0.1", requestListenPort);
            serverSocket.socket().bind(isa);
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动请求和响应服务的监听
     */
    public void startAccept() {
        try {
            while (true) {
                System.out.println("开始accept用户请求");
                int count = selector.select();
                if(count > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();

                        //若此key的通道是等待接受新的套接字连接
                        if (key.isAcceptable()) {
                            //一定要把这个accpet状态的服务器key去掉，否则会出错
                            iterator.remove();
                            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                            //接受socket
                            SocketChannel socket = serverChannel.accept();
                            System.out.println(socket.toString()+ " 发起请求");
                            socket.configureBlocking(false);
                            manager.dealWithNewRequest(socket);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
