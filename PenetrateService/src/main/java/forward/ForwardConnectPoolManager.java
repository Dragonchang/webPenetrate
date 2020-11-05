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

    /**
     * 开始进行转发链接的处理
     */
    public void startConnectPool() {
        forwardListener = new ForwardListener(this);
        forwardListener.startForwardListen();
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
    public Integer getAvailableConnectCount() {
        return 1;
    }

    public SocketChannel getAvailableConnect() {
        //TODO 连接池获取
        SocketChannel availableConnect = forwardListener.getForwardSocket();
        if(availableConnect == null) {
            return null;
        }
        return availableConnect;
    }
}
