package com.jasper.deploymentcontroller.action.pod.impl;

import com.jasper.deploymentcontroller.crd.MyDeployment;
import com.jasper.deploymentcontroller.action.pod.PodDeletedWatcher;
import com.jasper.deploymentcontroller.client.KubeClientDelegate;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoumeng
 * @version $Id: PodDeletedWatcherImpl.java, v 0.1 2018/12/10 下午4:17 zhoumeng Exp $
 */
@Component
@Slf4j
public class DefaultPodDeletedWatcherImpl implements PodDeletedWatcher {
    @Autowired
    private KubeClientDelegate delegate;

    @Override
    public void onPodDeleted(Pod pod) {
        MyDeployment myDeployment = delegate.getMyDeployment(pod);
        if(myDeployment == null) {
            // 说明是手工删掉了一个MyDeployment，k8s自动会删掉关联的pod，不需要我们自己去做
            log.info("cascade deletion, just ignore it");
            return;
        }
        PodList pods = delegate.client().pods().inNamespace(myDeployment.getMetadata().getNamespace()).withLabelSelector(myDeployment.getSpec().getLabelSelector())
                .list();
        boolean podTemplateChanged = myDeployment.isPodTemplateChanged(pod);
        log.info("podTemplateChanged: {}", podTemplateChanged);
        // 如果说MyDeployment#spec#relicas是n，podSize是大于等于n的，则说明是在缩容
        // 例1：缩容 replicas: 5 -> 3
        // 当删掉了第一个pod时，这里被回调，podSize为4 > 3
        // 当删掉了第二个pod时，这里被回调，podSize为3 = 3
        // 例2：MyDeployment#spec#replicas是3，然后手工删掉了一个pod，podSize为2 < 3，则需要重建
        // TODO 例3：仅更新时replicasChanged会被误认为true【待解决，如果modified事件能告诉我原来是什么样就好了】
        boolean replicasChanged = pods.getItems().size() >= myDeployment.getSpec().getReplicas();
        log.info("replicasChanged: {}",replicasChanged);
        // 如果是在缩容情况下被删掉，则不重建
        // 如果是在podTemplate更新的情况下被删掉，则不重建
        // 其他情况下【如手工删除一个pod】均要重建
        if (podTemplateChanged || replicasChanged) {
            // 如果是被更新，则不需要重建，并且去唤醒MyDeploymentModifiedHandler的deleteAndWait方法所在线程。
            log.info("Thread {}: pod {} template changed or scaling down, do not recreate", Thread.currentThread().getId(), pod.getMetadata().getName());
            delegate.countDownIfExistsByPod(pod);
        } else {
            Pod recreated = myDeployment.createPod();
            log.info("Thread {}: pod is deleted, recreating pod: {}", Thread.currentThread().getId(), recreated);
            delegate.client().pods().create(recreated);
        }
    }
}