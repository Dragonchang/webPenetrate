package forward;


import java.nio.channels.SocketChannel;

/**
 * @program: webPenetrate
 * @description: 转发连接池的管理
 * @author: zhangfl
 * @create: 2020-11-05 13:50
 **/
public class ForwardConnectPoolManager {
    private ForwardListener forwardListener;

    private int availableCount;

    /**
     * 开始进行转发链接的处理
     */
    public void startConnectPool() {
        forwardListener = new ForwardListener(this);
        forwardListener.startForwardListen();
        availableCount = 1;
    }

    /**
     * 获取管理的转发链接数
     * @return
     */
    public Integer getConnectCount() {
        return 1;
    }

    /**
     * 获取可用的链接数
     * @return
     */
    synchronized public Integer getAvailableConnectCount() {
        System.out.println(" getAvailableConnectCount availableCount: "+availableCount);
        return availableCount;
    }

    /**
     * 获取可用的转发连接
     * @return
     */
    synchronized public SocketChannel getAvailableConnect() {
        //TODO 连接池获取
        SocketChannel availableConnect = forwardListener.getForwardSocket();
        if(availableConnect == null) {
            return null;
        }
        availableCount --;
        System.out.println(" getAvailableConnect availableCount: "+availableCount);
        return availableConnect;
    }

    synchronized public void returnBackConnect() {
        availableCount++;
        System.out.println(" returnBackConnect availableCount: "+availableCount);
    }

}
