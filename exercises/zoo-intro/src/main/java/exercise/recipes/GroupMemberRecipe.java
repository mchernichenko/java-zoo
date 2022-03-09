package exercise.recipes;

import exercise.ZookeeperInit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.nodes.GroupMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

// Организация списка участников на сервере. Curator предоставляет готовую реализацию этого сервиса 'GroupMember'
public class GroupMemberRecipe {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupMemberRecipe.class);
    public static final String PATH = "/member";

    private GroupMember group;

    public static void main(String[] args) throws Exception {
        new GroupMemberRecipe().start();
    }

    private void start() throws InterruptedException {
        CuratorFramework curatorFramework = ZookeeperInit.getCuratorFrameworkClient();

        // регистрируем ноду
        group = new GroupMember(curatorFramework
                , PATH                                         // нода, где регаются приложения
                , UUID.randomUUID().toString().substring(0, 4) // генерация уникальный ID в группе
        );
        group.start(); // начать кэширование всех участников

        addShutdownHook(); // после регистрации участника, нужно закрыть GroupMember
        logMembers();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                group.close();
            }
        });
    }

    private void logMembers() throws InterruptedException {
        int membershipSize = 0;
        for (;;) {
            if (group.getCurrentMembers().size() != membershipSize) {
                membershipSize = group.getCurrentMembers().size();
                LOGGER.info("{} nodes, {}", membershipSize, group.getCurrentMembers().keySet());
            }
            Thread.sleep(10);
        }
    }
}
