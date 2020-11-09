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

    /**
     * LF.
     */
    public static final byte LF = (byte) '\n';

    /**
     * 开始连接web服务器
     * @param selector
     * @return
     * @throws Exception
     */
    public SocketChannel startServiceConnect(Selector selector) throws Exception {
        InetSocketAddress isa = new InetSocketAddress(webServiceAddress, webServicePort);
        webServiceClient = SocketChannel.open(isa);
        webServiceClient.configureBlocking(false);
        webServiceClient.register(selector, SelectionKey.OP_READ);
        return webServiceClient;
    }

    /**
     * 从转发连接上读取数据写入到web连接上
     */
    public void writeData() {
        try {
            System.out.println("开始读取forward连接上的数据");
            processRequest(forwardChannel, webServiceClient);
            System.out.println("读取forward连接上的数据结束而且成功写入web服务的连接上");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从web服务器连接上读取数据写入到转发连接上
     */
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

    private void processRequest(SocketChannel readChannel, SocketChannel writeChannel) throws IOException {
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

    private void processRequestResponse(SocketChannel readChannel, SocketChannel writeChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024*4);
        int size = 0;
        StringBuffer response = new StringBuffer();
        while ((size = readChannel.read(buffer)) > 0 || !isResponseEnd(response.toString())) {
            System.out.println("开始读取数据进行写入大小：" + size);
            buffer.flip();
            String str = new String(buffer.array(), 0, size);
            response.append(str);
            System.out.println(response.toString());
            writeChannel.write(buffer);
            buffer.clear();
        }
    }


    private boolean isResponseEnd(String response) {
        int count = 0;
        int index = 0;
        byte[] bytes = response.getBytes();
        while (index < bytes.length ) {
            if(bytes[index] == LF) {
                int indexCR = index + 2;
                if(indexCR < bytes.length && bytes[indexCR] == LF) {
                    count ++;
                }
            }
            index++;
        }
        if(count > 1) {
            System.out.println("响应接收完成");
            return true;
        } else {
            System.out.println("响应接收没有完成");
            return false;
        }
    }
}
