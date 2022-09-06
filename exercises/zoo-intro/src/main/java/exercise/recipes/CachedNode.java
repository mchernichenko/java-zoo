package exercise.recipes;

import exercise.ZookeeperInit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.shaded.com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

// Общая конфигурация. Готовый рецепт от куратора NodeCache
// Получаем нотификацию по любым изменениям в конкретной ноде, но не в её дочерних элементов (для этого TreeCache).
public class CachedNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedNode.class);
    public static final String CONFIG_PATH = "/cache";

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = ZookeeperInit.getCuratorFrameworkClient();

        curatorFramework.create()
                .creatingParentsIfNeeded()
                .forPath(CONFIG_PATH, "testValue".getBytes(Charsets.UTF_8));

        final NodeCache nodeCache = new NodeCache(curatorFramework, CONFIG_PATH);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                ChildData currentData = nodeCache.getCurrentData();

                if (currentData != null) {
                    LOGGER.info("Data changes, new data: {}", new String(currentData.getData(), StandardCharsets.UTF_8));
                } else {
                    // если нода  CONFIG_PATH удаляется
                    LOGGER.info("Data changes, new data: {}, nodes may be deleted", currentData, StandardCharsets.UTF_8);
                }

             //   System.out.println("Data change watched, and current data = " + new String(currentData.getData()));
              //  System.out.println(nodeCache.getCurrentData().getStat().getVersion());
            }
        });

        nodeCache.start(true);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
