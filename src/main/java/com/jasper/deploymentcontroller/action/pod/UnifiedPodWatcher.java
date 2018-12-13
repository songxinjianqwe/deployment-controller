package com.jasper.deploymentcontroller.action.pod;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Watcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将Pod事件监听统一收到此处
 * @author zhoumeng
 * @version $Id: PodWatcher.java, v 0.1 2018/12/9 下午6:40 zhoumeng Exp $
 */
@Slf4j
@Component
public class UnifiedPodWatcher {
    @Autowired
    private List<PodAddedWatcher> podAddedWatchers;
    @Autowired
    private List<PodModifiedWatcher> podModifiedWatchers;
    @Autowired
    private List<PodDeletedWatcher> podDeletedWatchers;

    /**
     * 将Pod事件统一收到此处
     * @param action
     * @param pod
     */
    public void eventReceived(Watcher.Action action, Pod pod) {
        log.info("Thread {}: PodWatcher: {} =>  {}, {}", Thread.currentThread().getId(), action, pod.getMetadata().getName(), pod);
        switch (action) {
            case ADDED:
                podAddedWatchers.forEach(watcher -> watcher.onPodAdded(pod));
                break;
            case MODIFIED:
                podModifiedWatchers.forEach(watcher -> watcher.onPodModified(pod));
                break;
            case DELETED:
                podDeletedWatchers.forEach(watcher -> watcher.onPodDeleted(pod));
                break;
            default:
                break;
        }
    }

    public static boolean isPodReady(Pod pod) {
        if (pod.getStatus() == null) {
            return false;
        }
        return pod.getStatus().getConditions().stream().anyMatch(condition ->
                condition.getType().equals("Ready") && condition.getStatus().equals("True")
        );
    }
}
