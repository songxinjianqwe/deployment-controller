package com.jasper.deploymentcontroller.action.deployment.impl;

import com.jasper.deploymentcontroller.crd.MyDeployment;
import com.jasper.deploymentcontroller.action.CrdAction;
import com.jasper.deploymentcontroller.action.deployment.MyDeploymentActionHandler;
import com.jasper.deploymentcontroller.client.KubeClientDelegate;
import io.fabric8.kubernetes.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhoumeng
 * @version $Id: MyDeploymentAddedHandler.java, v 0.1 2018/12/8 下午1:40 zhoumeng Exp $
 */
@Component(value = MyDeploymentActionHandler.RESOURCE_NAME + CrdAction.MODIFIED)
@Slf4j
public class MyDeploymentModifiedHandler implements MyDeploymentActionHandler {
    @Autowired
    private KubeClientDelegate delegate;

    /**
     * 当修改spec里的任何内容时，都会触发modified
     * 如果是修改replicas，则进行缩扩容
     * 如果是修改pod template，则进行rolling-update
     * <p>
     * Deployment 的 rollout 当且仅当 Deployment 的 pod template（例如.spec.template）时才会触发。
     * 其他更新，例如扩容Deployment不会触发 rollout
     * <p>
     * Deployment can ensure that only a certain number of Pods may be down while they are being updated.
     * By default, it ensures that at least 25% less than the desired number of Pods are up (25% max unavailable).
     * Deployment can also ensure that only a certain number of Pods may be created above the desired number of Pods.
     * By default, it ensures that at most 25% more than the desired number of Pods are up (25% max surge).
     * <p>
     * update过程，假设replicas=3：
     * 1、创建一个新的replicaset ，replicas为1；将老的replicasset缩容为2
     * 2、new扩容为2；old缩容为1
     * 3、new扩容为3；old缩容为0
     *
     * @param myDeployment
     */
    @Override
    public void handle(MyDeployment myDeployment) {
        log.info("{} modified", myDeployment.getMetadata().getName());
        PodList pods = delegate.client().pods().inNamespace(myDeployment.getMetadata().getNamespace()).withLabelSelector(myDeployment.getSpec().getLabelSelector()).list();
        int podSize = pods.getItems().size();
        int replicas = myDeployment.getSpec().getReplicas();
        boolean needScale = podSize != replicas;
        boolean needUpdate = pods.getItems().stream().anyMatch(pod -> {
            return myDeployment.isPodTemplateChanged(pod);
        });
        log.info("needScale: {}", needScale);
        log.info("needUpdate: {}", needUpdate);
        // 仅更新podTemplate
        if (!needScale) {
            syncRollingUpdate(myDeployment, pods.getItems());
        } else if (!needUpdate) {
            // 仅扩缩容
            int diff = replicas - podSize;
            if (diff > 0) {
                scaleUp(myDeployment, diff);
            } else {
                // 把列表前面的缩容，后面的不动
                scaleDown(pods.getItems().subList(0, -diff));
            }
        } else {
            // 同时scale&update
            // 对剩余部分做rolling-update，然后对diff进行缩扩容
            syncRollingUpdate(myDeployment, pods.getItems().subList(0, Math.min(podSize, replicas)));
            int diff = replicas - podSize;
            if (diff > 0) {
                scaleUp(myDeployment, diff);
            } else {
                scaleDown(pods.getItems().subList(replicas, podSize));
            }
        }
    }

    /**
     * @param myDeployment
     * @param count        扩容的pod数量
     */
    private void scaleUp(MyDeployment myDeployment, int count) {
        for (int i = 0; i < count; i++) {
            Pod pod = myDeployment.createPod();
            log.info("scale up pod[{}]: {} , {}", i, pod.getMetadata().getName(), pod);
            delegate.client().pods().create(pod);
        }
    }

    /**
     * @param pods 待删掉的pod列表
     */
    private void scaleDown(List<Pod> pods) {
        for (int i = 0; i < pods.size(); i++) {
            Pod pod = pods.get(i);
            log.info("scale down pod[{}]: {} , {}", i, pod.getMetadata().getName(), pod);
            delegate.client().pods().delete(pod);
        }
    }

    /**
     * 简化版Rolling-Update
     * 同步方式完成，异步版本每次执行结果不一样
     *
     * @param myDeployment
     * @param pods
     */
    private void syncRollingUpdate(MyDeployment myDeployment, List<Pod> pods) {
        pods.forEach(oldPod -> {
            Pod newPod = myDeployment.createPod();
            log.info("Thread {}: pod {} is creating", Thread.currentThread().getId(), newPod.getMetadata().getName());
            delegate.createPodAndWait(newPod, myDeployment);
            log.info("Thread {}: pod {} is deleting", Thread.currentThread().getId(), oldPod.getMetadata().getName());
            delegate.deletePodAndWait(oldPod);
        });
    }
}