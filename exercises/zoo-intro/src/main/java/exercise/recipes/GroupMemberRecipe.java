package exercise.recipes;

import exercise.ZookeeperInit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.nodes.GroupMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.UUID;

// Организация списка участников на сервере. Curator предоставляет готовую реализацию этого сервиса 'GroupMember'
public class GroupMemberRecipe {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupMemberRecipe.class);
    public static final String PATH = "/member";

    private GroupMember group;

    public static void main(String[] args) throws Exception {
   //    System.out.println("Текущая папка: " + Paths.get(".").toRealPath().toString());
        LOGGER.info(">>>>> Текущая папка: {}", Paths.get(".").toRealPath().toString());
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

    // Инициализируем поток, который запускается при завершении JVM, т.е. когда завершается последний поток
    // или пользователь прарывает выполнение по ^C
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOGGER.info("Running Shutdown Hook");
                group.close();
            }
        });
    }

    // в бесконечном цикле раз в 5 сек. проверяем текущее количество эфемерных нод в "/member" и
    // и если оно изменилось, то логируем это событие
    private void logMembers() throws InterruptedException {
        int membershipSize = 0;
        for (;;) {
            if (group.getCurrentMembers().size() != membershipSize) {
                membershipSize = group.getCurrentMembers().size();
                LOGGER.info("{} nodes, {}", membershipSize, group.getCurrentMembers().keySet());
            }
            Thread.sleep(5000);
        }
    }
}
