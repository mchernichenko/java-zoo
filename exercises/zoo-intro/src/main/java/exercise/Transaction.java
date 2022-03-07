package exercise;

import org.apache.curator.framework.CuratorFramework;

public class Transaction {
    public static void main(String[] args) throws Exception {
        ZookeeperInit zookeeperInit = new ZookeeperInit();
        zookeeperInit.init();

        CuratorFramework curatorFramework = zookeeperInit.getCuratorFrameworkClient();
        curatorFramework.create().forPath("/winter_soon");
        curatorFramework.create().forPath("/jon_snow");

        curatorFramework.inTransaction()
                .check().forPath("/winter_soon").and()
                .delete().forPath("/jon_snow").and()
                .create().forPath("/dragon", "and mother of dragons".getBytes()).and().commit();

    }
}
