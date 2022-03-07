package exercise.HelloWorld;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// Пример с использованием curator - более удобной обёртки к Zookeeper Java API
public class CuratorExample {
    final static Logger logger = LoggerFactory.getLogger(CuratorExample.class);

    public static final String HOST = "localhost:2181";
    public static final String NODE_NAME = "/test-node";

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .retryPolicy(new RetryOneTime(1000))
                .connectString(HOST)
                .build();

        curatorFramework.start();
        curatorFramework.blockUntilConnected(); // блокируемся, пока не будет установлен коннект к zoo

        try {
            curatorFramework.create().forPath(NODE_NAME);
        } catch (KeeperException e) {
            logger.warn("Create node: " + e.getMessage());
        }

        curatorFramework.setData().forPath(NODE_NAME, "value".getBytes());
        curatorFramework.getData().forPath(NODE_NAME);
        List<String> zkNodes = curatorFramework.getChildren().forPath("/");
        System.out.println(zkNodes);
    }
}
