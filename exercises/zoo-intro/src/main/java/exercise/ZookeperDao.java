package exercise;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;

public class ZookeperDao {
    public static final String UTF_8 = "UTF-8";

    public ZookeperDao(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    private CuratorFramework curatorFramework;

    public boolean checkExistence(String path) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(path);
        return stat != null;
    }

    public void setData(String value, String path) throws Exception {
        if (!checkExistence(path)) {
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(path);
        }
        curatorFramework.setData().forPath(path, value.getBytes(Charset.forName(UTF_8)));
    }

    public String getData(String path) throws Exception {
        if (checkExistence(path)) {
            byte[] value = this.curatorFramework.getData().forPath(path);
            return new String(value, Charset.forName(UTF_8));
        }
        return null;
    }
}
