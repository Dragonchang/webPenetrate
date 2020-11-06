package webService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

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
    private SocketChannel webServiceClient;

    private SocketChannel forwardChannel;

    public ServiceConnect(SocketChannel channel) {
        this.forwardChannel = channel;
    }

    public SocketChannel startServiceConnect(Selector selector) throws Exception {
        InetSocketAddress isa = new InetSocketAddress(webServiceAddress, webServicePort);
        webServiceClient = SocketChannel.open(isa);
        webServiceClient.configureBlocking(false);
        webServiceClient.register(selector, SelectionKey.OP_READ);
        return webServiceClient;
    }

    public void writeData() {
        try {
            System.out.println("开始读取forward连接上的数据");
            processRequestResponse(forwardChannel, webServiceClient);
            System.out.println("读取forward连接上的数据结束而且成功写入web服务的连接上");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readData() {
        try {
            System.out.println("开始读取forward连接上的数据");
            processRequestResponse(webServiceClient, forwardChannel);
            System.out.println("读取web服务器连接上的数据结束而且成功写入forward的连接上");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                webServiceClient.socket().close();
                webServiceClient.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void processRequestResponse(SocketChannel readChannel, SocketChannel writeChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int size = 0;
        while ((size = readChannel.read(buffer)) > 0) {
            System.out.println("开始读取数据进行写入大小：" + size);
            buffer.flip();
            writeChannel.write(buffer);
            buffer.clear();
        }
    }
}
