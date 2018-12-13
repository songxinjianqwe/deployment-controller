package com.jasper.deploymentcontroller.action.pod;

import io.fabric8.kubernetes.api.model.Pod;

/**
 * @author zhoumeng
 * @version $Id: PodModifiedWatcher.java, v 0.1 2018/12/10 下午3:57 zhoumeng Exp $
 */
public interface PodModifiedWatcher {
    /**
     * 当MyDeployment管理的Pod更新时的回调方法
     * @param pod
     */
    void onPodModified(Pod pod);
}