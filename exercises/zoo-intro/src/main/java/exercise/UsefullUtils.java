package exercise;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooKeeper;

// В кураторе есть готовые полезные утилитарные функции
public class UsefullUtils {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = ZookeeperInit.getCuratorFrameworkClient();
        ZooKeeper zookeeper = curatorFramework.getZookeeperClient().getZooKeeper();

        // валидация пути
        PathUtils.validatePath("/path");

        // конструирование пути
        ZKPaths.makePath("father/", "/child1/", "/child2", "child3");
        // /father/child1/child2/child3

        // достать дочерние ноды
        ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode("/father/child1/child2");
        // /father/child1/child2=child3

        // получить упорядоченный список нод
        ZKPaths.getSortedChildren(zookeeper, "path");

        // создание иерархию нод
        ZKPaths.mkdirs(zookeeper, "/father/child1/child2");

    }
}
