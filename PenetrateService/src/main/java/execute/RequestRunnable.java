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
        System.out.println("开始处理请求 "+requestClientConnect.toString());
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
        if(forwardConnectPoolManager.getAvailableConnectCount() <= 0) {
            System.out.println("没有可用的连接");
        }
        SocketChannel forwardConnect;
        do {
            forwardConnect = forwardConnectPoolManager.getAvailableConnect();
            if(forwardConnect != null) {
                System.out.println("获取到可用的转发链接"+ forwardConnect.toString());
                break;
            }
            System.out.println("等待可用的转发链接");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);

        try {
            forwardConnect.register(selector, SelectionKey.OP_READ);
            while (true) {
                System.out.println("开始轮询请求连接和转发连接");
                int count = selector.select();
                if(count > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if(key.isValid() && key.isReadable()){
                            SocketChannel channel = (SocketChannel)key.channel();
                            if(channel == requestClientConnect) {
                                System.out.println("开始读取请求连接的数据： "+requestClientConnect.toString());
                                processRequestResponse(channel, forwardConnect);
                            }
                            if(channel == forwardConnect) {
                                System.out.println("开始读取转发连接响应的数据： "+forwardConnect.toString());
                                processRequestResponse(channel, requestClientConnect);
                                try {
                                    requestClientConnect.socket().close();
                                    requestClientConnect.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                selector.close();
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                requestClientConnect.socket().close();
                requestClientConnect.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        executeManger.processEnd(this);
    }

    private void processRequestResponse(SocketChannel readChannel, SocketChannel writeChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int size = 0;
        while ((size = readChannel.read(buffer)) > 0) {
            System.out.println("开始读取数据进行写入大小："+size);
            buffer.flip();
            String str = new String(buffer.array(), 0, size);
            System.out.println(str);
            writeChannel.write(buffer);
        }
    }
}
