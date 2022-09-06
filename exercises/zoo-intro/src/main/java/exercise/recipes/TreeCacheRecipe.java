package exercise.recipes;

import exercise.ZookeeperInit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.shaded.com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Общая конфигурация. Готовый рецепт от куратора TreeCache
// Получаем нотификацию по любым изменениям в ноде и её дочерних элементов.
public class TreeCacheRecipe {

    private static final Logger LOGGER = LoggerFactory.getLogger(TreeCacheRecipe.class);
    public static final String CONFIG_PATH = "/configuration";

    private TreeCache cache ;

    public static void main(String[] args) throws Exception {
        new TreeCacheRecipe().start();
    }

    private void start() throws Exception {
        CuratorFramework curatorFramework = ZookeeperInit.getCuratorFrameworkClient();

        // создаём ноду для конфигурации, если её нет
        curatorFramework.create()
                .creatingParentsIfNeeded()
                .forPath(CONFIG_PATH, "Нода для конфигурации".getBytes(Charsets.UTF_8));

        cache = new TreeCache(curatorFramework, CONFIG_PATH);
        // Регистрируем колбэк на любое изменение конфигурации, которая лежит в CONFIG_PATH
        // При установлении TCP соединения клиента и сервера, при наступлении события на сервере, он отправляет
        // это событие в TCP соединение и клиент уже его ловит и вызывает колбэк связанный с этим событием.
        // По сути клиент получает все события от сервера и уже на клиенте идёт обработка этих событий и вызов колбэков.
        // Аналогия с RMQ, когда идёт публикация сообщений в exchange, а далее клиенты подписываются на нужные им сообщения
        cache.getListenable().addListener((clientCuratorFramework, treeCacheEvent) -> {
            TreeCacheEvent.Type type = treeCacheEvent.getType();
            ChildData childData = treeCacheEvent.getData();

            switch (type) {
                    // Subclass cache system initialization is complete
                case INITIALIZED:
                    LOGGER.info("Subclass Cache System Initialization Completed");
                    break;
                case NODE_ADDED:
                    LOGGER.info("NODE_ADDED: {}", childData != null ? childData.getPath() : null);
                    break;
                case NODE_UPDATED:
                    LOGGER.info("NODE_UPDATED. Value: {}", childData != null ?
                            new String(childData.getData(), Charsets.UTF_8) + " Path:" + childData.getPath() :
                            null);
                    break;
                case NODE_REMOVED:
                    LOGGER.info("NODE_REMOVED: {}", childData != null ? childData.getPath() : null);
                    break;
            }
        });
        cache.start();
        addShutdownHook();

        Thread.sleep(Integer.MAX_VALUE);
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                cache.close();
            }
        });
    }
}
