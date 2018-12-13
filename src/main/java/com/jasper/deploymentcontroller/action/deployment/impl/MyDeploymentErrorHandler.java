package com.jasper.deploymentcontroller.action.deployment.impl;

import com.jasper.deploymentcontroller.crd.MyDeployment;
import com.jasper.deploymentcontroller.action.CrdAction;
import com.jasper.deploymentcontroller.action.deployment.MyDeploymentActionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhoumeng
 * @version $Id: MyDeploymentAddedHandler.java, v 0.1 2018/12/8 下午1:40 zhoumeng Exp $
 */
@Component(value = MyDeploymentActionHandler.RESOURCE_NAME + CrdAction.ERROR)
@Slf4j
public class MyDeploymentErrorHandler implements MyDeploymentActionHandler {

    @Override
    public void handle(MyDeployment myDeployment) {
        log.error("{} error",myDeployment.getMetadata().getName());
    }
}