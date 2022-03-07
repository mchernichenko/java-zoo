package exercise.HelloWorld;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

// пример использования простого java API zookeeper
public class ZookeeperExample {

    public static final String HOST = "localhost:2181";
    public static final String NODE_NAME = "/test-node";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        ZooKeeper zkClient = new ZooKeeper(HOST
                , 10000
                // регистрация колбэка (Watcher). Выполнится после установки коннекта
                , watcherEvent -> System.out.println(">>>>>>>>" + watcherEvent.getState()));
        Thread.sleep(1000);

        if (zkClient.exists(NODE_NAME, null) == null) {
            zkClient.create(NODE_NAME, "summer".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        zkClient.setData(NODE_NAME, "winter1".getBytes(), zkClient.exists(NODE_NAME, true).getVersion());

        zkClient.getData(NODE_NAME, false, null);

        List<String> zkNodes = zkClient.getChildren("/", true);
        System.out.println("Список всех нод в корне: " + zkNodes);

        zkClient.close();
    }
}
