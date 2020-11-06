package webService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @program: webPenetrate
 * @description: 管理和webservice的连接
 * @author: zhangfl
 * @create: 2020-11-02 11:40
 **/
public class ServiceConnect implements Runnable{
    /**
     * web 服务地址
     */
    private static String webServiceAddress = "localhost";

    /**
     * web服务端口
     */
    private static int webServicePort = 8403;

    /**
     * web 服务端的连接
     */
    private SocketChannel webServiceClient;

    private SocketChannel forwardChannel;

    public ServiceConnect(SocketChannel channel) {
        this.forwardChannel = channel;
    }

    private void startServiceConnect() throws Exception{
        InetSocketAddress isa = new InetSocketAddress(webServiceAddress, webServicePort);
        webServiceClient = SocketChannel.open(isa);
        webServiceClient.configureBlocking(true);
    }

    @Override
    public void run() {
        try {
            System.out.println("开始连接web服务器");
            startServiceConnect();
            System.out.println("web服务器连接成功");
            System.out.println("开始读取forward连接上的数据");
            processRequestResponse(forwardChannel, webServiceClient);
            System.out.println("读取forward连接上的数据结束而且成功写入web服务的连接上");
            processRequestResponse(webServiceClient, forwardChannel);
            System.out.println("读取web服务器连接上的数据结束而且成功写入forward的连接上");
            try {
                webServiceClient.socket().close();
                webServiceClient.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRequestResponse(SocketChannel readChannel, SocketChannel writeChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024*4);
        int size = 0;
        while ((size = readChannel.read(buffer)) > 0) {
            System.out.println("开始读取数据进行写入大小：" + size);
            buffer.flip();
            String str = new String(buffer.array(), 0, size);
            System.out.println(str);
            writeChannel.write(buffer);
            buffer.clear();
        }
    }
}
