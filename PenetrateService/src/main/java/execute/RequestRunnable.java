package execute;

import forward.ForwardConnectPoolManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author ：dragonChang
 * @date ：Created in 2020/11/4 22:18
 * @description：请求执行器
 * @modified By：
 * @version: 1.0$
 */
public class RequestRunnable implements Runnable{

    /**
     * 请求执行器
     */
    private RequestExecuteManger executeManger;

    /**
     * 接收请求的socket
     */
    private SocketChannel requestClientConnect;

    /**
     * 转发连接池管理器
     */
    private ForwardConnectPoolManager forwardConnectPoolManager;

    private Selector selector;
    private SelectionKey serverKey;

    /**
     * LF.
     */
    public static final byte LF = (byte) '\n';


    public RequestRunnable(RequestExecuteManger executeManger, SocketChannel socket) {
        this.executeManger = executeManger;
        this.requestClientConnect = socket;
        this.forwardConnectPoolManager = executeManger.getForwardConnectPool();
        try {
            selector = Selector.open();
            requestClientConnect.register(selector, SelectionKey.OP_READ);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean executeOn(ExecutorService executorPool) {
        boolean success = false;
        try {
            executorPool.execute(this);
            success = true;
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        } finally {
            if (!success) {
                System.out.println("executeOn failed ");
            }
        }
        return success;
    }

    @Override
    public void run() {
        System.out.println("开始处理请求 "+requestClientConnect.toString()+" threadID: "+Thread.currentThread().getId());
        Integer connectCount = forwardConnectPoolManager.getConnectCount();
        //TODO 发送404http错误
        while (connectCount <= 0) {
            System.out.println("没有可用的转发链接");
            connectCount = forwardConnectPoolManager.getConnectCount();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //TODO 转发连接扩容操作
        while(forwardConnectPoolManager.getAvailableConnectCount() <= 0) {
            System.out.println("没有可用的连接");
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SocketChannel forwardConnect;
        do {
            forwardConnect = forwardConnectPoolManager.getAvailableConnect();
            if(forwardConnect != null) {
                System.out.println("获取到可用的转发链接"+ forwardConnect.toString()+" threadID: "+Thread.currentThread().getId());
                break;
            }
            System.out.println("等待可用的转发链接");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);

        boolean requestProcessEnd = false;
        try {
            forwardConnect.register(selector, SelectionKey.OP_READ);
            while (!requestProcessEnd) {
                System.out.println("开始轮询请求连接和转发连接 threadID: "+Thread.currentThread().getId());
                int count = selector.select();
                if(count > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if(key.isValid() && key.isReadable()){
                            SocketChannel channel = (SocketChannel)key.channel();
                            if(channel == requestClientConnect) {
                                System.out.println("开始读取请求连接的数据： threadID: "+Thread.currentThread().getId()+" 连接："+requestClientConnect.toString());
                                processRequest(channel, forwardConnect);
                            }
                            if(channel == forwardConnect) {
                                System.out.println("开始读取转发连接响应的数据threadID: "+Thread.currentThread().getId()+" 连接："+forwardConnect.toString());
                                processRequestResponse(channel, requestClientConnect);
                                requestProcessEnd = true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                requestClientConnect.socket().close();
                requestClientConnect.close();
                selector.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        forwardConnectPoolManager.returnBackConnect();
        executeManger.processEnd(this);
    }

    private void processRequest(SocketChannel readChannel, SocketChannel writeChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024*4);
        int size = 0;
        while ((size = readChannel.read(buffer)) > 0) {
            System.out.println("开始读取数据进行写入大小："+size+" threadID: "+Thread.currentThread().getId());
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
            System.out.println("开始读取数据进行写入大小："+size+" threadID: "+Thread.currentThread().getId());
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
