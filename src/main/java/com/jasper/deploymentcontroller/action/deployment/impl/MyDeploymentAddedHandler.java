package com.jasper.deploymentcontroller.action.deployment.impl;

import com.jasper.deploymentcontroller.action.CrdAction;
import com.jasper.deploymentcontroller.action.deployment.MyDeploymentActionHandler;
import com.jasper.deploymentcontroller.action.pod.UnifiedPodWatcher;
import com.jasper.deploymentcontroller.client.KubeClientDelegate;
import com.jasper.deploymentcontroller.crd.MyDeployment;
import io.fabric8.kubernetes.api.model.Pod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * @author zhoumeng
 * @version $Id: MyDeploymentAddedHandler.java, v 0.1 2018/12/8 下午1:40 zhoumeng Exp $
 */
@Component(value = MyDeploymentActionHandler.RESOURCE_NAME + CrdAction.ADDED)
@Slf4j
public class MyDeploymentAddedHandler implements MyDeploymentActionHandler {
    @Autowired
    private KubeClientDelegate delegate;

    @Override
    public void handle(MyDeployment myDeployment) {
        log.info("{} added", myDeployment.getMetadata().getName());
        // TODO 当第一次启动项目时，现存的MyDeployment会回调一次Added事件，这里会导致重复创建pod【可通过status解决】,目前解法是去查一下现存的pod[不可靠]
        // 有可能pod的状态还没来得及置为not ready
        int existedReadyPodNumber = delegate.client().pods().inNamespace(myDeployment.getMetadata().getNamespace()).withLabelSelector(myDeployment.getSpec().getLabelSelector()).list().getItems()
                .stream().filter(UnifiedPodWatcher::isPodReady).collect(Collectors.toList()).size();
        Integer replicas = myDeployment.getSpec().getReplicas();
        for (int i = 0; i < replicas - existedReadyPodNumber; i++) {
            Pod pod = myDeployment.createPod();
            log.info("Thread {}:creating pod[{}]: {} , {}", Thread.currentThread().getId(), i, pod.getMetadata().getName(), pod);
            delegate.client().pods().create(pod);
       }
    }
}