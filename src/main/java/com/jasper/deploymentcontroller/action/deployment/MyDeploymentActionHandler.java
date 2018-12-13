package com.jasper.deploymentcontroller.action.deployment;

import com.jasper.deploymentcontroller.crd.MyDeployment;

/**
 * @author zhoumeng
 * @version $Id: MyDeploymentActionHandler.java, v 0.1 2018/12/8 下午1:38 zhoumeng Exp $
 */
public interface MyDeploymentActionHandler {
    String RESOURCE_NAME = "MyDeployment";

    void handle(MyDeployment myDeployment);
}