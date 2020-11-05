package execute;

import forward.ForwardConnectPoolManager;
import reply.RequestResponseListener;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * @author ：dragonChang
 * @date ：Created in 2020/11/4 22:20
 * @description：web请求管理器
 * @modified By：
 * @version: 1.0$
 */
public class RequestExecuteManger {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 5));
    private static final int MAXIMUM_POOL_SIZE = 60;
    private static final int KEEP_ALIVE_SECONDS = 20;
    private static int waringCount = 50;
    private ExecutorService executorService;

    private ForwardConnectPoolManager forwardConnectPoolManager;

    private RequestResponseListener requestResponseListener;

    /**
     * 接收到的请求
     */
    private final Deque<RequestRunnable> mReadyRequests = new ArrayDeque<RequestRunnable>();

    /**
     * 执行中的请求
     */
    private final Deque<RequestRunnable> mRunningRequests = new ArrayDeque<RequestRunnable>();

    /**
     * 服务启动
     */
    public void startService() {
        forwardConnectPoolManager = new ForwardConnectPoolManager();
        forwardConnectPoolManager.startConnectPool();

        requestResponseListener = new RequestResponseListener(this);
        requestResponseListener.startAccept();
    }

    /**
     * 执行请求的线程池
     * @return
     */
    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory("RobotsMqttProcessor", false));
        }
        return executorService;
    }

    /**
     * 将请求socket放入队列中进行处理
     * @param socket
     */
    public void dealWithNewRequest(SocketChannel socket) {
        enqueueRequest(new RequestRunnable(this, socket));
    }

    /**
     * 取出请求进行处理
     */
    public void promoteAndExecute() {
        for (Iterator<RequestRunnable> i = mReadyRequests.iterator(); i.hasNext(); ) {
            RequestRunnable command = i.next();
            if(command == null) {
                continue;
            }
            if(command.executeOn(executorService())) {
                //成功加入到线程池队列中
                i.remove();
                mRunningRequests.add(command);
            }
        }
    }

    /**
     * 获取转发连接池管理器
     * @return
     */
    public ForwardConnectPoolManager getForwardConnectPool() {
        return this.forwardConnectPoolManager;
    }

    /**
     * 请求处理结束
     * @param requestRunnable
     */
    public void processEnd(RequestRunnable requestRunnable) {
        if(mReadyRequests.size() > waringCount
                || mRunningRequests.size() > waringCount) {
            System.out.println("processEnd this is warning log for mReadyCommands size : "+mReadyRequests.size()
                    +" mRunningCommands size: "+mRunningRequests.size());
        }
        for (Iterator<RequestRunnable> i = mRunningRequests.iterator(); i.hasNext(); ) {
            RequestRunnable request = i.next();
            if (request == requestRunnable) {
                i.remove();
                break;
            }
        }
    }

    private static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread result = new Thread(r, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    /**
     * 将请求放入到请求队列中
     * @param requestRunnable
     */
    private void enqueueRequest(RequestRunnable requestRunnable) {
        if(mReadyRequests.size() > waringCount
                || mRunningRequests.size() > waringCount) {
            System.out.println("enqueueCommand this is warning log for mReadyCommands size : "+mReadyRequests.size()
                    +" mRunningCommands size: "+mRunningRequests.size());
        }
        if(requestRunnable == null) {
            return;
        }
        mReadyRequests.add(requestRunnable);
        promoteAndExecute();
    }
}
