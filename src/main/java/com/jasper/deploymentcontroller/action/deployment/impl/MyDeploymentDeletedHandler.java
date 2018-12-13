package com.jasper.deploymentcontroller.action.deployment.impl;

import com.jasper.deploymentcontroller.crd.MyDeployment;
import com.jasper.deploymentcontroller.action.CrdAction;
import com.jasper.deploymentcontroller.action.deployment.MyDeploymentActionHandler;
import com.jasper.deploymentcontroller.client.KubeClientDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoumeng
 * @version $Id: MyDeploymentAddedHandler.java, v 0.1 2018/12/8 下午1:40 zhoumeng Exp $
 */
@Component(value = MyDeploymentActionHandler.RESOURCE_NAME + CrdAction.DELETED)
@Slf4j
public class MyDeploymentDeletedHandler implements MyDeploymentActionHandler {
    @Autowired
    private KubeClientDelegate delegate;

    @Override
    public void handle(MyDeployment myDeployment) {
        log.info("{} deleted",myDeployment.getMetadata().getName());
        // 这个删除在没启动controller也是会进行的，k8s似乎会自动级联删掉CRD关联的资源【官方说了是这样的】
        // The default deletion policy for all resources defined through CRD is to cascade. However, as you noted above, this only works for CRD as of k8s 1.8+.
        // You just need to add an entry to metadata.ownerReferences pointing back to the parent, whenever you create a child object.
        // https://github.com/kubeflow/tf-operator/issues/42
    }
}