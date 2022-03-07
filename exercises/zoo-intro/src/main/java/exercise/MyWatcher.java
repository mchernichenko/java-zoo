package exercise;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class MyWatcher implements CuratorWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyWatcher.class);

    private CuratorFramework curatorFramework;
    private String path;

    public MyWatcher(CuratorFramework curatorFramework, String path) {
        this.curatorFramework = curatorFramework;
        this.path = path;
    }

    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        curatorFramework.checkExists().usingWatcher(this).forPath(path);
        byte[] value = this.curatorFramework.getData().forPath(path);
        String res = new String(value, Charset.forName("UTF-8"));
        LOGGER.info("Watcher fired, value {} {} {}", res, watchedEvent.getState(), watchedEvent.getType());
    }

}
