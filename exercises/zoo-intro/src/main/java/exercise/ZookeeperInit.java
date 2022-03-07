package exercise;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperInit.class);

    private static CuratorFramework curatorFrameworkClient;

    public static void init() {
        curatorFrameworkClient = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(new RetryNTimes(3, 1000))
                .sessionTimeoutMs(10000)
                .connectionTimeoutMs(1000)
                .build();
        curatorFrameworkClient.start();
        //block thread until zk connection is established

        try {
            curatorFrameworkClient.getZookeeperClient().blockUntilConnectedOrTimedOut();
        } catch (InterruptedException e) {
            throw  new IllegalStateException(e);
        }
        //manually check connection status, cause CuratorFramework does not throw any exception
        if (!curatorFrameworkClient.getZookeeperClient().isConnected()) {
            throw new IllegalStateException("Can't connect to Zookeeper");
        }

        try {
            LOGGER.info("Connected to Zookeeper {}", curatorFrameworkClient.getZookeeperClient().getZooKeeper().getSessionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static CuratorFramework getCuratorFrameworkClient() {
        if (curatorFrameworkClient == null) {
            init();
        }
        return curatorFrameworkClient;
    }
}
