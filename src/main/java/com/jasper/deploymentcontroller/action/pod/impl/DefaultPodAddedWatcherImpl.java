package com.jasper.deploymentcontroller.action.pod.impl;

import com.jasper.deploymentcontroller.action.pod.PodAddedWatcher;
import com.jasper.deploymentcontroller.client.KubeClientDelegate;
import io.fabric8.kubernetes.api.model.Pod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoumeng
 * @version $Id: PodAddedWatcherImpl.java, v 0.1 2018/12/10 下午4:16 zhoumeng Exp $
 */
@Component
public class DefaultPodAddedWatcherImpl implements PodAddedWatcher {
    @Autowired
    private KubeClientDelegate delegate;

    @Override
    public void onPodAdded(Pod pod) {
        // TODO 根据pod状态来更新myDeployment的状态
        // POD 创建出来了，但容器还没创建
//        MyDeployment myDeployment = delegate.getMyDeployment(pod);
//        delegate.client().customResources(
//                delegate.client().customResourceDefinitions().withName(MyDeployment.CRD_NAME).get(),
//                MyDeployment.class,
//                MyDeploymentList.class,
//                DoneableMyDeployment.class
//        ).inNamespace(myDeployment.getMetadata().getNamespace())
//                .withName(myDeployment.getMetadata().getName())
//                .edit()
//                .increaseCurrent()
//                .done();
    }
}