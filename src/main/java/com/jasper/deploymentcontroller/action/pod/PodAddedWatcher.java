package com.jasper.deploymentcontroller.action.pod;

import io.fabric8.kubernetes.api.model.Pod;

/**
 * @author zhoumeng
 * @version $Id: PodAddedWatcher.java, v 0.1 2018/12/10 下午3:56 zhoumeng Exp $
 */
public interface PodAddedWatcher {
    /**
     * 当MyDeployment管理的Pod添加时的回调方法
     * @param pod
     */
    void onPodAdded(Pod pod);
}