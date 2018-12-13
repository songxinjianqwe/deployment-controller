package com.jasper.deploymentcontroller.crd;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhoumeng
 * @version $Id: DoneableMyDeployment.java, v 0.1 2018/12/7 下午8:15 zhoumeng Exp $
 */
@Slf4j
public class DoneableMyDeployment extends CustomResourceDoneable<MyDeployment> {
    private MyDeployment myDeployment;
    private Function function;

    public DoneableMyDeployment(MyDeployment resource, Function function) {
        super(resource, function);
        this.myDeployment = resource;
        this.function = function;
    }

    public DoneableMyDeployment increaseCurrent() {
        MyDeploymentStatus status = myDeployment.getStatus();
        status.setReplicas((status.getReplicas() != null ? status.getReplicas() : 0) + 1);
        log.info("current: {} incrementAndGet", status.getReplicas());
        return this;
    }

    public DoneableMyDeployment decreaseCurrent() {
        MyDeploymentStatus status = myDeployment.getStatus();
        status.setReplicas((status.getReplicas() != null ? status.getReplicas() : 0) - 1);
        log.info("current: {} decrementAndGet", status.getReplicas());
        return this;
    }


    public DoneableMyDeployment increaseUpdated() {
        MyDeploymentStatus status = myDeployment.getStatus();
        status.setUpdatedReplicas((status.getUpdatedReplicas() != null ? status.getUpdatedReplicas() : 0) + 1);
        log.info("updated: {} incrementAndGet", status.getUpdatedReplicas());
        return this;
    }

    public DoneableMyDeployment decreaseUpdated() {
        MyDeploymentStatus status = myDeployment.getStatus();
        status.setUpdatedReplicas((status.getUpdatedReplicas() != null ? status.getUpdatedReplicas() : 0) + 1);
        log.info("updated: {} decrementAndGet", status.getUpdatedReplicas());
        return this;
    }


    public DoneableMyDeployment increaseAvailable() {
        MyDeploymentStatus status = myDeployment.getStatus();
        status.setAvailableReplicas((status.getAvailableReplicas() != null ? status.getAvailableReplicas() : 0) + 1);
        log.info("available: {} incrementAndGet", status.getAvailableReplicas());
        return this;
    }

    public DoneableMyDeployment decreaseAvailable() {
        MyDeploymentStatus status = myDeployment.getStatus();
        status.setAvailableReplicas((status.getAvailableReplicas() != null ? status.getAvailableReplicas() : 0) - 1);
        log.info("available: {} decrementAndGet", status.getAvailableReplicas());
        return this;
    }

    @Override
    public MyDeployment done() {
        return (MyDeployment) this.function.apply(this.myDeployment);
    }
}
