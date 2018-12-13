package com.jasper.deploymentcontroller.action.pod;

import io.fabric8.kubernetes.api.model.Pod;

/**
 * @author zhoumeng
 * @version $Id: PodDeletedWatcher.java, v 0.1 2018/12/10 下午3:57 zhoumeng Exp $
 */
public interface PodDeletedWatcher {
    /**
     * 当MyDeployment管理的Pod删除时的回调方法
     * @param pod
     */
    void onPodDeleted(Pod pod);
}