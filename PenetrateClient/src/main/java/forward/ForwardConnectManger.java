package forward;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import webService.*;

/**
 * @author ：dragonChang
 * @date ：Created in 2020/11/5 21:49
 * @description：转发连接池的管理
 * @modified By：
 * @version: 2.0
 */
public class ForwardConnectManger {

    //TODO
    private SocketChannel forwardConnect;
    /**
     * web 服务端的连接
     */
    private SocketChannel webServiceClient;
    private Selector selector;
    private ForwardConnect connect;
    private ServiceConnect webConnect;

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
        int n= 0;
        try {
            while (true) {
                System.out.println("开始accept转发连接上的请求读操作");
                int count = selector.select();
                if(count > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        //若此key的通道是等待接受新的套接字连接
                        if(key.isValid() && key.isReadable()){
                            if(key.channel() == forwardConnect) {
                                SocketChannel channel = (SocketChannel)key.channel();
                                System.out.println("有服务转发连接的读数据请求到来: "+channel.toString()+" *****************N: "+n);
                                webConnect = new ServiceConnect(channel);
                                webServiceClient = webConnect.startServiceConnect(selector);
                                webConnect.writeData();
                                n++;
                                System.out.println("转发数据处理结束*******************************END！！");
                            } else {
                                webConnect.readData();
                                System.out.println("web服务数据处理结束*******************************END！！");
                            }

                        }
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
        while (true) {
            try {
                System.out.println("开始连接forward服务器");
                forwardConnect = connect.connectForwardService();
                System.out.println("连接forward服务器成功："+forwardConnect.toString());
                if(forwardConnect != null && forwardConnect.isConnected()) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
