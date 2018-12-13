package com.jasper.deploymentcontroller.action.pod.impl;

import com.jasper.deploymentcontroller.action.pod.PodModifiedWatcher;
import com.jasper.deploymentcontroller.client.KubeClientDelegate;
import io.fabric8.kubernetes.api.model.Pod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoumeng
 * @version $Id: PodModifiedWatcherImpl.java, v 0.1 2018/12/10 下午4:18 zhoumeng Exp $
 */
@Component
@Slf4j
public class DefaultPodModifiedWatcherImpl implements PodModifiedWatcher {
    @Autowired
    private KubeClientDelegate delegate;

    @Override
    public void onPodModified(Pod pod) {
        // 注意POD被删掉前会回调三次MODIFIED
        // 有可能是容器创建出来了
//        if (UnifiedPodWatcher.isPodReady(pod)) {
//            MyDeployment myDeployment = delegate.getMyDeployment(pod);
//            delegate.client().customResources(
//                    delegate.client().customResourceDefinitions().withName(MyDeployment.CRD_NAME).get(),
//                    MyDeployment.class,
//                    MyDeploymentList.class,
//                    DoneableMyDeployment.class
//            ).inNamespace(myDeployment.getMetadata().getNamespace())
//                    .withName(myDeployment.getMetadata().getName())
//                    .edit()
//                    .increaseAvailable()
//                    .increaseUpdated()
//                    .done();
//        }
    }
}