package execute;

import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
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
    private ExecutorService executorService;

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
     * 接收到的请求
     */
    private final Deque<RequestRunnable> mReadyRequests = new ArrayDeque<RequestRunnable>();

    /**
     * 执行中的请求
     */
    private final Deque<RequestRunnable> mRunningRequests = new ArrayDeque<RequestRunnable>();

    /**
     * 将请求socket放入队列中进行处理
     * @param socket
     */
    public void dealWithNewRequest(Socket socket) {
        enqueueRequest(new RequestRunnable(this, socket));
    }

    private void enqueueRequest(RequestRunnable requestRunnable) {

    }
}
