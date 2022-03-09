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
                // стратегия подключения
                .retryPolicy(new RetryOneTime(1000)) // пытаемся подключиться в течение 1 сек.
                //.retryPolicy(new RetryNTimes(100, 3)) // пытаемся подключиться 3 раза с перерывом 0.1 сек.
                .connectString(HOST)
                .build();

        curatorFramework.start();
        // блокируемся до тех пор, пока не будет доступно подключение к ZooKeeper. Cогласно retryPolicy это может занять
        // некоторое время. В нашем примере, попытка подключения может затянуться до 1 сек.
        curatorFramework.blockUntilConnected();

        // создание ноды
        try {
            curatorFramework.create().forPath(NODE_NAME);
        } catch (KeeperException e) {  // eсли нода существует, то ошибка
            logger.warn("Create node: {}", e.getMessage());
        }

        // установка значения ноды
        curatorFramework.setData().forPath(NODE_NAME, "value".getBytes());
        // чтение ноды
        curatorFramework.getData().forPath(NODE_NAME);
        // чтение всех дочерних нод
        List<String> zkNodes = curatorFramework.getChildren().forPath("/");
        System.out.println(zkNodes);
    }
}
